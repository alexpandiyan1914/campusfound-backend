package com.campusfound.email.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${brevo.api-key}")
    private String apiKey;

    @Value("${brevo.sender-email}")
    private String senderEmail;

    @Value("${brevo.sender-name}")
    private String senderName;

    private final RestClient restClient = RestClient.create();

    public void sendOtp(String recipientEmail, String otp) {

        String htmlContent = """
                <html>
                <body style="font-family: Arial, sans-serif;">
                    <h2>CampusFound Email Verification</h2>

                    <p>Your verification code is:</p>

                    <h1>%s</h1>

                    <p>This OTP is valid for 5 minutes.</p>

                    <p>If you did not request this code,
                    you can ignore this email.</p>

                    <br>
                    <p>CampusFound</p>
                </body>
                </html>
                """.formatted(otp);

        Map<String, Object> requestBody = Map.of(
                "sender", Map.of(
                        "name", senderName,
                        "email", senderEmail
                ),

                "to", List.of(
                        Map.of(
                                "email", recipientEmail
                        )
                ),

                "subject", "CampusFound Email Verification",

                "htmlContent", htmlContent
        );

        restClient.post()
                .uri("https://api.brevo.com/v3/smtp/email")
                .header("api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .toBodilessEntity();
    }
}