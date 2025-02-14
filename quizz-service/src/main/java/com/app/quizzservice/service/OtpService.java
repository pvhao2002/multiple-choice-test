package com.app.quizzservice.service;

import com.app.quizzservice.model.Otp;
import com.app.quizzservice.repo.OtpRepo;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Log
@Service
public class OtpService {
    private static final int EXPIRE_MINUTES = 1; // 30 minutes
    private final OtpRepo otpRepo;
    private final Random random = new Random();

    public OtpService(OtpRepo otpRepo) {
        this.otpRepo = otpRepo;
    }


    public String generateOtp(String email) {
        var newOtp = generateOTP();
        var expiredAt = new Date(System.currentTimeMillis() + Duration.ofMinutes(EXPIRE_MINUTES).toMillis());
        var otp = new Otp(0, email, newOtp, expiredAt.getTime(), new Date());
        otpRepo.save(otp);
        return newOtp;
    }

    public boolean verifyOtp(String email, String otp) {
        return otpRepo.findByEmail(email)
                      .filter(o -> o.isValid(otp))
                      .isPresent();
    }

    public Optional<Otp> getOtp(String email) {
        return otpRepo.findByEmail(email);
    }


    public void deleteOtp(String email) {
        otpRepo.deleteByEmail(email);
    }

    private String generateOTP() {
        int length = 6;
        String numbers = "0123456789";
        char[] otp = new char[length];
        for (int i = 0; i < length; i++) {
            otp[i] = numbers.charAt(random.nextInt(numbers.length()));
        }
        return new String(otp);
    }
}
