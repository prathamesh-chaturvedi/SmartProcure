package com.smartprocure.services;

import com.smartprocure.entities.ProcurementCase;
import com.smartprocure.entities.User;

public interface EmailService {

    void sendEmail(String to, String subject, String body);

    void sendApprovalEmail(ProcurementCase procurementCase);

    void sendRejectionEmail(ProcurementCase procurementCase);

    void sendReviewEmail(ProcurementCase procurementCase, User approver);

}
