package com.app.quizzservice.rest;

import com.app.quizzservice.jwt.JwtTokenProvider;
import com.app.quizzservice.model.ResponseContainer;
import com.app.quizzservice.model.User;
import com.app.quizzservice.request.payload.CreateStudentPayload;
import com.app.quizzservice.request.payload.ImportExcelStudent;
import com.app.quizzservice.request.response.UserInfoResponse;
import com.app.quizzservice.security.UserPrincipal;
import com.app.quizzservice.service.UserService;
import com.app.quizzservice.utils.Base64Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final List<String> EXCEL_HEADER = List.of(
            "No",
            "Student ID",
            "First Name",
            "Last Name",
            "Email",
            "Gender"
    );

    private static final List<String> IMPORT_HEADER = List.of(
            "Student ID",
            "Fullname",
            "Gender"
    );
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping(value = "create", consumes = {"multipart/form-data"})
    public Object create(
            @RequestPart("data") CreateStudentPayload payload,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar
    ) throws IOException {
        if (userService.checkStudentIdExist(payload.studentId())) {
            return ResponseContainer.failure("Student ID already exists");
        }
        String imageUrl = null;
        if (avatar != null && !avatar.isEmpty()) {
            imageUrl = Base64Utils.encodeImage(avatar);
        }
        userService.createStudent(payload, imageUrl);
        return ResponseContainer.success("OK");
    }

    @PostMapping(value = "import", consumes = {"multipart/form-data"})
    public Object importExcel(@RequestParam("file") MultipartFile file) {
        var listStudent = new ArrayList<ImportExcelStudent>();
        try (var workbook = new XSSFWorkbook(file.getInputStream())) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                var sheet = workbook.getSheetAt(i);
                // validate header format is: Student ID, Fullname, Gender
                var headerRow = sheet.getRow(0);
                if (headerRow == null) {
                    return ResponseContainer.failure("Sheet is empty");
                }
                var header = new ArrayList<String>();
                headerRow.forEach(cell -> header.add(cell.getStringCellValue()));
                if (!(header.size() == 3)) {
                    return ResponseContainer.failure("Invalid excel format");
                }
                sheet.forEach(row -> {
                    if (row.getRowNum() < 2) return;
                    listStudent.add(new ImportExcelStudent(row));
                });
            }
            var result = userService.importStudentExcel(listStudent);
            return CollectionUtils.isEmpty(result)
                    ? ResponseContainer.success("OK")
                    : ResponseContainer.failure(result);
        } catch (Exception e) {
            return ResponseContainer.failure(e.getMessage());
        }
    }

    @GetMapping("export")
    public Object exportExcel() {
        try (var workbook = new XSSFWorkbook()) {
            var listStudent = userService.findAll();
            var sheet = workbook.createSheet("Student");
            var headerRow = sheet.createRow(0);
            var cellStyle = workbook.createCellStyle();
            var font = workbook.createFont();
            font.setBold(true);
            cellStyle.setFont(font);
            for (int i = 0; i < EXCEL_HEADER.size(); i++) {
                var cell = headerRow.createCell(i);
                cell.setCellValue(EXCEL_HEADER.get(i));
                cell.setCellStyle(cellStyle);
            }

            listStudent.forEach(e -> {
                var row = sheet.createRow(sheet.getLastRowNum() + 1);
                row.createCell(0).setCellValue(row.getRowNum());
                row.createCell(1).setCellValue(e.getStudentId());
                row.createCell(2).setCellValue(e.getFirstName());
                row.createCell(3).setCellValue(e.getLastName());
                row.createCell(4).setCellValue(e.getEmail());
                row.createCell(5).setCellValue(e.getGender().toString());
            });

            EXCEL_HEADER.forEach(e -> sheet.autoSizeColumn(EXCEL_HEADER.indexOf(e)));

            var out = new ByteArrayOutputStream();
            workbook.write(out);
            var in = new ByteArrayResource(out.toByteArray());
            return ResponseEntity.ok()
                                 .header(
                                         HttpHeaders.CONTENT_DISPOSITION,
                                         "attachment; filename=list_student.xlsx"
                                 )
                                 .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                 .contentLength(in.contentLength())
                                 .body(in);
        } catch (IOException e) {
            return ResponseContainer.failure(e.getMessage());
        }
    }

    @GetMapping("info")
    public Object info(@AuthenticationPrincipal User user) {
        return ResponseContainer.success(new UserInfoResponse(user));
    }

    @GetMapping
    public Object list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "key", required = false) String key
    ) {
        return ResponseContainer.success(userService.findAll(page, size, key));
    }

    @GetMapping("all")
    public Object all() {
        return ResponseContainer.success(userService.findAllV2());
    }

    @PostMapping("change-status")
    public Object changeStatus(long userId, boolean status) {
        return ResponseContainer.success(userService.updateStatus(userId, status));
    }

    @PostMapping("multiple-login")
    public Object multipleLogin(HttpServletRequest request, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        var token = jwtTokenProvider.getToken(request);
        return ResponseContainer.success(userService.checkSameToken(token, userPrincipal.getUserId()));
    }
}
