INSERT IGNORE INTO system_settings (name, value) VALUES ('authorizedRedirectUris', 'http://localhost:4200/oauth2/redirect');
INSERT IGNORE INTO system_settings (setting_id, name, value, created_at, updated_at) VALUES (1, 'CLIENT_GOOGLE_ID', '', '2025-02-05 04:16:44', '2025-02-05 04:16:44');
INSERT IGNORE INTO system_settings (setting_id, name, value, created_at, updated_at) VALUES (2, 'CLIENT_GOOGLE_SECRET', '', '2025-02-05 04:16:44', '2025-02-05 04:16:44');
INSERT IGNORE INTO system_settings (name, value)
VALUES ('email.host', 'smtp.gmail.com'),
       ('email.port', '587'),
       ('email.username', ''),
       ('email.password', '');


select *
from system_settings;

INSERT IGNORE INTO system_settings (name, value)
VALUES ('email.subject.otp', 'Your OTP Code'),
       ('email.subject.reset-password', 'Reset Your Password'),
       ('email.subject.change-password', 'Password Change Confirmation'),
       ('email.subject.register-welcome', 'Welcome to Our Website'),
       ('email.template.otp', '<blockquote>
    <strong>Dear $mail.email$ </strong>,
    <br/><br/>

    <p>Please enter the following one time password (OTP) to complete your $mail.action$. OTP will expire in <strong>30
        minutes</strong>.</p>
    <div style="border: 1px solid black; text-align:center"><p>One-Time Password (OTP): <strong> $mail.otpNo$ </strong></p>
    </div>
    <p>If you have problems $mail.action$ using the OTP, please contact our Support Department at quiz.eaut@gmail.com for
        further assistance.</p>
    <br/><br/>
    <p>Enjoy betting with QuizEAUT !
        <br/><strong>Support Department QuizEAUT</strong></p><br/>
</blockquote>
'),
       ('email.template.reset-password', '<body width="100%" style="background-color: #cccccc;">
<table align="center" cellpadding="10" cellspacing="10" width="800" style="border-collapse: collapse; font-family: arial; background-color: white;">
    <tr>
        <td style="display: block; padding: 0;" colspan="3">
            <img src="https://image.talentnetwork.vn/dhcndonga//rws//logo_1470717056_1470974891.png" alt="banner" width="800" height="100" style="display: block;" />
            <br/>
        </td>
    </tr>
    <tr>
        <td style="text-align: left; font-size: 15px;" colspan="3">
            <strong>Dear $mail.email$,</strong>
        </td>
    </tr>
    <tr>
        <td style="text-align: left; font-size: 15px;" colspan="3">
            <p>Forgotten your password? Don''t worry, we all do it. You can reset it by clicking this link.</p>
        </td>
    </tr>
    <tr>
        <td>
            <a href="$mail.link$" style="font-size: 15px">Reset My Password >></a>
            <br/>
        </td>
    </tr>
    <tr>
        <td style="text-align: left; font-size: 15px;" colspan="3">
            <p>Note: This link will only be valid for $forgot.timeout$ hours.</p>
        </td>
    </tr>
    <tr>
        <td style="text-align: left; font-size: 15px;" colspan="3">
            <p>If you have any questions, please contact the quiz.euat@gmail.com</p>
        </td>
    </tr>
    <tr>
        <td style="text-align: left; font-size: 15px;" colspan="3">
            <p>Thank you,</p>
            <p>The QuizEUAT team</p>
        </td>
    </tr>
    <tr style="background-color: #3d60a4">
        <td style="text-align: center; font-size: 15px; color: white;" colspan="3">
            <br/>
            <strong style="font-size: 15px;">Help & support</strong>
            <p style="font-size: 15px; margin: 10px;">Customer Service: QuizEUAT</p>
        </td>
    </tr>
</table>
</body>
'),
       ('email.template.change-password', '<body width="100%" style="background-color: #cccccc;">
<table align="center" cellpadding="10" cellspacing="10" width="800"
       style="border-collapse: collapse; font-family: arial; background-color: white;">
    <tr>
        <td style="display: block; padding: 0;" colspan="3">
            <img src="https://image.talentnetwork.vn/dhcndonga//rws//logo_1470717056_1470974891.png" alt="banner"
                 width="800" height="100" style="display: block;"/>
            <br/>
        </td>
    </tr>
    <tr>
        <td style="text-align: left; font-size: 15px;" colspan="3">
            <strong>Dear $mail.email$,</strong>
        </td>
    </tr>
    <tr>
        <td style="text-align: left; font-size: 15px;" colspan="3">
            <p>New password is: $mail.newPassword$.</p>
        </td>
    </tr>
    <tr>
        <td style="text-align: left; font-size: 15px;" colspan="3">
            <p>If you did not request this please contact the QuizEUAT Helpdesk</p>
        </td>
    </tr>
    <tr>
        <td style="text-align: left; font-size: 15px;" colspan="3">
            <p>Thank you,</p>
            <p>The QuizEUAT team</p>
        </td>
    </tr>
    <tr style="background-color: #3d60a4">
        <td style="text-align: center; font-size: 15px; color: white;" colspan="3">
            <br/>
            <strong style="font-size: 15px;">Help & support</strong>
            <p style="font-size: 15px; margin: 10px;">Customer Service: QuizEUAT</p>
        </td>
    </tr>
</table>
</body>
'),
       ('email.template.register-welcome', '<body width="100%" style="background-color: #cccccc;">
<table align="center" cellpadding="15" cellspacing="10" width="800"
       style="border-collapse: collapse; font-family: arial; background-color: white;">
    <tr>
        <td style="display: block; padding: 0;">
            <img src="https://image.talentnetwork.vn/dhcndonga//rws//logo_1470717056_1470974891.png" alt="banner" width="800" height="100" style="display: block;" />
            <br />
        </td>
    </tr>
    <tr>
        <td style="text-align: center; font-size: 20px;">
            <strong>$email$,</strong>
        </td>
    </tr>
    <tr>
        <td style="text-align: center; font-size: 20px;">
            <p>You have successfully activated your account.</p>
        </td>
    </tr>
    <tr>
        <td style="text-align: center; font-size: 20px;">
            <p>Use your Email and password to login.</p>
        </td>
    </tr>
    <tr>
        <td style="text-align: center; font-size: 20px;">
            <p>Email: $email$</p>
        </td>
    </tr>
    <tr>
        <td style="text-align: center; font-size: 20px;">
            <p>Please retain this email for future reference.</p>
        </td>
    </tr>
    <tr>
        <td style="text-align: center; font-size: 20px;">
            <strong>Customer Service Department</strong>
        </td>
    </tr>
    <tr style="background-color: #3d60a4">
        <td style="text-align: center; font-size: 10px; color: white;">
            <br />
            <strong style="font-size: 15px;">Help & support</strong>
            <p style="font-size: 15px; margin: 10px;">Customer Service: quiz.eaut@gmail.com</p>
            <br />
        </td>
    </tr>
</table>
</body>
')
;
