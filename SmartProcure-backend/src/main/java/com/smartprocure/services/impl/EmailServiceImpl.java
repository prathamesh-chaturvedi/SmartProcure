package com.smartprocure.services.impl;

import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
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

	    try {
	        MimeMessage message = mailSender.createMimeMessage();

	        MimeMessageHelper helper = new MimeMessageHelper(message, true);

	        helper.setFrom("smartprocure.project@gmail.com");
	        helper.setTo(to);
	        helper.setSubject(subject);

	        // true = HTML email
	        helper.setText(body, true);

	        System.out.println("Sending HTML mail");

	        mailSender.send(message);

	    } catch (Exception e) {
	        throw new RuntimeException("Failed to send email", e);
	    }
	}

    @Override
    public void sendApprovalEmail(ProcurementCase procurementCase) {

    	// TODO:
    	// Attach the generated Comparative Statement PDF
    	// before sending the final approval email.
    	
        String subject = "Procurement Approved - "
                + procurementCase.getProcurementCode();
        
        String body = """
        		<html>
        		<body style="font-family: Arial, sans-serif; font-size:14px; color:#333333;">

        		<p>Dear <b>%s</b>,</p>

        		<p>Your procurement case has been <span style="color:green;font-weight:bold;">APPROVED</span>.</p>

        		<table cellpadding="5" cellspacing="0">
        		    <tr>
        		        <td><b>Procurement Code</b></td>
        		        <td>%s</td>
        		    </tr>
        		    <tr>
        		        <td><b>Title</b></td>
        		        <td>%s</td>
        		    </tr>
        		</table>

        		<p>
        		The Comparative Statement PDF has been generated successfully.
        		</p>

        		<p>
        		<a href="http://localhost:5173/procurement-cases/%d"
        		style="
        		background:#198754;
        		color:white;
        		padding:10px 18px;
        		text-decoration:none;
        		border-radius:5px;
        		font-weight:bold;">
        		View Procurement Case
        		</a>
        		</p>

        		<p>
        		<b>PDF Location</b><br>
        		%s
        		</p>

        		<br>

        		<p>Regards,<br>
        		<b>SmartProcure</b></p>

        		</body>
        		</html>
        		""".formatted(
        		        procurementCase.getCreatedBy().getFirstName(),
        		        procurementCase.getProcurementCode(),
        		        procurementCase.getTitle(),
        		        procurementCase.getProcurementCaseId(),
        		        procurementCase.getCsPdfPath());
        System.out.println("sendApprovalEmail called");
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
        		<html>
        		<body style="font-family: Arial, sans-serif; font-size:14px; color:#333333;">

        		<p>Dear <b>%s</b>,</p>

        		<p>Your procurement case has been <span style="color:red;font-weight:bold;">REJECTED</span>.</p>

        		<table cellpadding="5" cellspacing="0">
        		    <tr>
        		        <td><b>Procurement Code</b></td>
        		        <td>%s</td>
        		    </tr>
        		    <tr>
        		        <td><b>Title</b></td>
        		        <td>%s</td>
        		    </tr>
        		</table>

        		<p>
        		Please review the remarks and make the necessary changes.
        		</p>

        		<p>
        		<a href="http://localhost:5173/procurement-cases/%d"
        		style="
        		background:#dc3545;
        		color:white;
        		padding:10px 18px;
        		text-decoration:none;
        		border-radius:5px;
        		font-weight:bold;">
        		View Procurement Case
        		</a>
        		</p>

        		<br>

        		<p>Regards,<br>
        		<b>SmartProcure</b></p>

        		</body>
        		</html>
        		""".formatted(
        		        procurementCase.getCreatedBy().getFirstName(),
        		        procurementCase.getProcurementCode(),
        		        procurementCase.getTitle(),
        		        procurementCase.getProcurementCaseId());
        System.out.println("sendRejectionEmail called");
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
        		<html>
        		<body style="font-family: Arial, sans-serif; font-size:14px; color:#333333;">

        		<p>Dear <b>%s</b>,</p>

        		<p>A procurement case requires your approval.</p>

        		<table cellpadding="5" cellspacing="0">
        		    <tr>
        		        <td><b>Procurement Code</b></td>
        		        <td>%s</td>
        		    </tr>
        		    <tr>
        		        <td><b>Title</b></td>
        		        <td>%s</td>
        		    </tr>
        		    <tr>
        		        <td><b>Submitted By</b></td>
        		        <td>%s %s</td>
        		    </tr>
        		</table>

        		<p>Please review the procurement case by clicking the button below.</p>

        		<p>
        		    <a href="http://localhost:5173/procurement-cases/%d"
        		       style="
        		            background-color:#0d6efd;
        		            color:white;
        		            text-decoration:none;
        		            padding:10px 18px;
        		            border-radius:5px;
        		            display:inline-block;
        		            font-weight:bold;">
        		        Review Procurement
        		    </a>
        		</p>

        		<p>Regards,<br>
        		<b>SmartProcure</b></p>

        		</body>
        		</html>
        		""".formatted(
        		        approver.getFirstName(),
        		        procurementCase.getProcurementCode(),
        		        procurementCase.getTitle(),
        		        procurementCase.getCreatedBy().getFirstName(),
        		        procurementCase.getCreatedBy().getLastName(),
        		        procurementCase.getProcurementCaseId());
        System.out.println("sendReviewEmail called");
        sendEmail(
                approver.getEmail(),
                subject,
                body);
    }
}