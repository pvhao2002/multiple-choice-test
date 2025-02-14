package com.app.quizzservice.service;

import com.app.quizzservice.config.SystemConfigService;
import com.app.quizzservice.model.User;
import com.app.quizzservice.model.email.OtpEmail;
import com.app.quizzservice.utils.Constants;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.stringtemplate.v4.ST;

import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

@Log
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EmailService {
    private static final int NUMBER_SEND_EMAIL_THREADS = 10;
    private static final String MAIL = "mail";

    SystemConfigService systemConfigService;
    ExecutorService sendEmailExecutor = Executors.newFixedThreadPool(NUMBER_SEND_EMAIL_THREADS);
    OtpService otpService;

    public void sendEmailAsync(JavaMailSenderImpl mailSender, MimeMessage mimeMessage) {
        sendEmailExecutor.submit(() -> {
            try {
                mailSender.send(mimeMessage);
            } catch (Exception ex) {
                log.log(
                        Level.SEVERE,
                        String.format(
                                "-sendMailAsync : Error while sending email: Info mailSender {from: %s, host: %s, port: %s}",
                                mailSender.getUsername(),
                                mailSender.getHost(),
                                mailSender.getPort()
                        ),
                        ex
                );
            }
        });
    }

    public void sendEmailWelcome(User user) throws MessagingException {
        var subject = systemConfigService.getConfigValue(Constants.EMAIL_SUBJECT_REGISTER_WELCOME);
        var emailContent = buildEmail(Constants.EMAIL_REGISTER_WELCOME_TEMPLATE, Map.of("email", user.getEmail()));

        send(subject, emailContent, user.getEmail());
    }

    public void sendEmailOtp(String email) throws MessagingException {
        var subject = systemConfigService.getConfigValue(Constants.EMAIL_SUBJECT_OTP);
        var generatedOtp = otpService.generateOtp(email);
        var otpEmail = OtpEmail.builder()
                               .email(email)
                               .otpNo(generatedOtp)
                               .build();
        var emailContent = buildEmail(Constants.EMAIL_OTP_TEMPLATE, Map.of(MAIL, otpEmail));
        send(subject, emailContent, email);
    }

    public void send(String subject, String content, String email) throws MessagingException {
        var mailSender = buildJavaMailSender();
        var mailMessage = mailSender.createMimeMessage();
        var mimeMessageHelper = new MimeMessageHelper(mailMessage, "UTF-8");
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setText(content, true);
        mimeMessageHelper.setSubject(subject);
        sendEmailAsync(mailSender, mailMessage);
    }


    private JavaMailSenderImpl buildJavaMailSender() {
        var host = systemConfigService.getConfigValue(Constants.EMAIL_HOST);
        var port = Integer.parseInt(systemConfigService.getConfigValue(Constants.EMAIL_PORT));
        var username = systemConfigService.getConfigValue(Constants.EMAIL_USERNAME);
        var password = systemConfigService.getConfigValue(Constants.EMAIL_PASSWORD);
        var mailSender = new JavaMailSenderImpl();
        var mailProperties = buildJavaMailProperties();
        mailSender.setJavaMailProperties(mailProperties);
        mailSender.setProtocol("smtp");
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setDefaultEncoding("UTF-8");
        return mailSender;
    }

    private Properties buildJavaMailProperties() {
        var mailProperties = new Properties();
        mailProperties.put("mail.smtp.auth", true);
        mailProperties.put("mail.smtp.starttls.enable", true);
        mailProperties.put("mail.smtp.starttls.required", true);
        mailProperties.put("mail.smtp.socketFactory.port", 465);
        mailProperties.put("mail.smtp.debug", true);
        mailProperties.put("mail.smtp.ssl.checkserveridentity", true);
        mailProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        mailProperties.put("mail.smtp.socketFactory.fallback", false);
        return mailProperties;
    }

    private String buildEmail(String templateName, Map<String, Object> args) {
        String emailContent = systemConfigService.getConfigValue(templateName);

        if (StringUtils.isNotBlank(emailContent)) {
            var template = new ST(emailContent, Constants.DOLLAR, Constants.DOLLAR);
            if (args != null) {
                for (var entrySet : args.entrySet()) {
                    template.add(entrySet.getKey(), entrySet.getValue());
                }
            }
            emailContent = template.render();
        }
        return emailContent;
    }
}
