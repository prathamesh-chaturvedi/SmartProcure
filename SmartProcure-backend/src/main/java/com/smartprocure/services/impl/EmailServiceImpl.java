package com.smartprocure.services.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.smartprocure.entities.ProcurementCase;
import com.smartprocure.entities.User;
import com.smartprocure.services.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String subject, String body) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    @Override
    public void sendApprovalEmail(ProcurementCase procurementCase) {

    	// TODO:
    	// Attach the generated Comparative Statement PDF
    	// before sending the final approval email.
    	
        String subject = "Procurement Approved - "
                + procurementCase.getProcurementCode();

        String body = """
                Dear %s,

                Your procurement case has been approved.

                Procurement Code : %s
                Title            : %s

                The Comparative Statement has been generated and is available in the system.

                Regards,
                SmartProcure
                """.formatted(
                procurementCase.getCreatedBy().getFirstName(),
                procurementCase.getProcurementCode(),
                procurementCase.getTitle());

        sendEmail(
                procurementCase.getCreatedBy().getEmail(),
                subject,
                body);
    }

    @Override
    public void sendRejectionEmail(ProcurementCase procurementCase) {

        String subject = "Procurement Rejected - "
                + procurementCase.getProcurementCode();

        String body = """
                Dear %s,

                Your procurement case has been rejected.

                Procurement Code : %s
                Title            : %s

                Please login to SmartProcure to review the remarks.

                Regards,
                SmartProcure
                """.formatted(
                procurementCase.getCreatedBy().getFirstName(),
                procurementCase.getProcurementCode(),
                procurementCase.getTitle());

        sendEmail(
                procurementCase.getCreatedBy().getEmail(),
                subject,
                body);
    }

    @Override
    public void sendReviewEmail(ProcurementCase procurementCase, User approver) {

    	 // TODO:
        // After sending the review email to the approver,
        // optionally notify the procurement creator that the
        // procurement case has moved to the next approval level.

        // TODO:
        // Replace localhost with the deployed frontend URL.
    	
        String subject = "Procurement Approval Required - "
                + procurementCase.getProcurementCode();

        String body = """
                Dear %s,

                A procurement case requires your approval.

                Procurement Code : %s
                Title            : %s
                Submitted By     : %s %s

                Please review the procurement using the link below:

                http://localhost:3000/procurement-cases/%d

                Regards,
                SmartProcure
                """.formatted(
                approver.getFirstName(),
                procurementCase.getProcurementCode(),
                procurementCase.getTitle(),
                procurementCase.getCreatedBy().getFirstName(),
                procurementCase.getCreatedBy().getLastName(),
                procurementCase.getProcurementCaseId());

        sendEmail(
                approver.getEmail(),
                subject,
                body);
    }
}