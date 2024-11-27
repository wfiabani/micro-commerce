package com.commerce.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Service
public class EmailManager {

    private final SesClient sesClient;

    private static final Logger logger = LoggerFactory.getLogger(EmailManager.class);

    public EmailManager(SesClient sesClient) {
        this.sesClient = sesClient;
    }

    public void sendEmail(String to, String subject, String body) {
        try {
            SendEmailRequest request = SendEmailRequest.builder()
                    .destination(Destination.builder()
                            .toAddresses(to)
                            .build())
                    .message(Message.builder()
                            .subject(Content.builder().data(subject).build())
                            .body(Body.builder()
                                    .text(Content.builder().data(body).build())
                                    .build())
                            .build())
                    .source("fiabani.wilian@gmail.com")
                    .build();
            sesClient.sendEmail(request);
            logger.info("E-mail enviado com sucesso para {}", to);
        } catch (SesException e) {
            logger.error("Erro ao enviar e-mail: {}", e.awsErrorDetails().errorMessage());
        }
    }
}
