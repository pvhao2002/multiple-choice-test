package com.app.quizzservice.rest;

import com.app.quizzservice.model.ResponseContainer;
import com.app.quizzservice.security.UserPrincipal;
import com.app.quizzservice.service.DashboardService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/home")
public class HomeController {
    private static final List<String> EXCEL_HEADER = List.of(
            "Email",
            "Subject",
            "Exam",
            "Has Monitor",
            "Total Question",
            "Total Correct",
            "Total Warning",
            "Last Time"
    );
    private final DashboardService dashboardService;

    public HomeController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("about")
    public Object about() {
        return ResponseContainer.success(dashboardService.about());
    }

    @GetMapping("student")
    public Object getStudent(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "30") int size,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        return ResponseContainer.success(dashboardService.getStudent(page, size, userPrincipal.getUserId()));
    }


    @GetMapping("admin")
    public Object getAdam() {
        return ResponseContainer.success(dashboardService.getAdmin());
    }

    @GetMapping("user-test")
    public Object userTest(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "30") int size
    ) {
        return ResponseContainer.success(dashboardService.statisticUserTest(page, size));
    }

    @GetMapping("export")
    public Object export() {
        try (var workbook = new XSSFWorkbook()) {
            var listUserTest = dashboardService.statisticUserTest(1, 1_000_000);
            var sheet = workbook.createSheet("Home");
            var headerRow = sheet.createRow(sheet.getLastRowNum() + 1);
            var cellStyle = workbook.createCellStyle();
            var font = workbook.createFont();
            font.setBold(true);
            cellStyle.setFont(font);
            for (int i = 0; i < EXCEL_HEADER.size(); i++) {
                var cell = headerRow.createCell(i);
                cell.setCellValue(EXCEL_HEADER.get(i));
                cell.setCellStyle(cellStyle);
            }

            listUserTest.getContents().forEach(e -> {
                var row = sheet.createRow(sheet.getLastRowNum() + 1);
                row.createCell(0).setCellValue(e.email());
                row.createCell(1).setCellValue(e.subject());
                row.createCell(2).setCellValue(e.testName());
                row.createCell(3).setCellValue(e.hasMonitor());
                row.createCell(4).setCellValue(e.totalQuestion());
                row.createCell(5).setCellValue(e.totalCorrect());
                row.createCell(6).setCellValue(e.numberOfWarning());
                row.createCell(7).setCellValue(e.lastUpdate());
            });

            EXCEL_HEADER.forEach(e -> sheet.autoSizeColumn(EXCEL_HEADER.indexOf(e)));

            var out = new ByteArrayOutputStream();
            workbook.write(out);
            var in = new ByteArrayResource(out.toByteArray());
            return ResponseEntity.ok()
                                 .header(
                                         HttpHeaders.CONTENT_DISPOSITION,
                                         "attachment; filename=export_user_test.xlsx"
                                 )
                                 .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                 .contentLength(in.contentLength())
                                 .body(in);
        } catch (IOException e) {
            return ResponseContainer.failure(e.getMessage());
        }
    }
}
