package com.smartprocure.services.impl;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.smartprocure.custom_exceptions.ResourceNotFoundException;
import com.smartprocure.entities.Action;
import com.smartprocure.entities.ApprovalHistory;
import com.smartprocure.entities.Company;
import com.smartprocure.entities.ProcurementCase;
import com.smartprocure.entities.VendorQuote;
import com.smartprocure.repositories.ApprovalHistoryRepository;
import com.smartprocure.repositories.ProcurementCaseRepository;
import com.smartprocure.repositories.VendorQuoteRepository;
import com.smartprocure.services.PdfService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfServiceImpl implements PdfService {

    private final ProcurementCaseRepository procurementCaseRepository;
    private final VendorQuoteRepository vendorQuoteRepository;
    private final ApprovalHistoryRepository approvalHistoryRepository;

    // --- COLOR PALETTE (CLEAN PRINT-FRIENDLY ENTERPRISE) ---
    private static final Color COLOR_PRIMARY_NAVY = new Color(30, 58, 138);     // #1E3A8A (Navy Typography & Rules)
    private static final Color COLOR_TEXT_MUTED = new Color(100, 116, 139);     // #64748B (Gray Subtitles)
    private static final Color COLOR_TEXT_DARK = new Color(15, 23, 42);         // #0F172A (Body Content)
    
    private static final Color COLOR_BG_WHITE = Color.WHITE;
    private static final Color COLOR_BG_LIGHT_GRAY = new Color(248, 250, 252);  // #F8FAFC (Subtle Table Header Fill)
    private static final Color COLOR_BORDER_GRAY = new Color(203, 213, 225);    // #CBD5E1 (0.5pt Borders)
    
    // L1 Highlighting (Mint Tint & Green Border)
    private static final Color COLOR_L1_BG = new Color(240, 253, 244);          // #F0FDF4 (Light mint green fill)
    private static final Color COLOR_L1_BORDER = new Color(22, 163, 74);        // #16A34A (Green border)
    private static final Color COLOR_L1_GREEN = new Color(21, 128, 61);         // #15803D (Green text)
    private static final Color COLOR_BADGE_GREEN = new Color(22, 163, 74);      // #16A34A (Badge fill)
    
    private static final Color COLOR_RED_TEXT = new Color(220, 38, 38);          // #DC2626
    private static final Color COLOR_AMBER_TEXT = new Color(217, 119, 6);        // #D97706

    // --- TYPOGRAPHY HIERARCHY ---
    private static final Font FONT_BRAND_NAVY = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16f, COLOR_PRIMARY_NAVY);
    private static final Font FONT_HEADER_SUB = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10.5f, COLOR_PRIMARY_NAVY);
    private static final Font FONT_COMPANY_NAME = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10f, COLOR_PRIMARY_NAVY);
    private static final Font FONT_COMPANY_DETAILS = FontFactory.getFont(FontFactory.HELVETICA, 8f, COLOR_TEXT_DARK);
    
    private static final Font FONT_SECTION_HEADING = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10f, COLOR_PRIMARY_NAVY);
    private static final Font FONT_TABLE_HEADER = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, COLOR_TEXT_DARK);
    
    private static final Font FONT_LABEL_BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, COLOR_TEXT_DARK);
    private static final Font FONT_VALUE = FontFactory.getFont(FontFactory.HELVETICA, 8.5f, COLOR_TEXT_DARK);
    private static final Font FONT_VALUE_BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, COLOR_TEXT_DARK);
    
    private static final Font FONT_PRICE_LARGE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15f, COLOR_L1_GREEN);
    private static final Font FONT_PRICE_GREEN = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10f, COLOR_L1_GREEN);
    private static final Font FONT_BADGE_WHITE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8f, Color.WHITE);
    
    private static final Font FONT_SMALL_MUTED = FontFactory.getFont(FontFactory.HELVETICA, 7.5f, COLOR_TEXT_MUTED);
    private static final Font FONT_SMALL_ITALIC = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 7.5f, COLOR_TEXT_MUTED);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a");

    @Override
    @Transactional
    public String generateComparativeStatementPdf(Long procurementCaseId) {
        log.info("Generating Final Minimalist Enterprise Comparative Statement PDF for Case ID: {}", procurementCaseId);

        ProcurementCase procurementCase = procurementCaseRepository.findById(procurementCaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Procurement Case not found with ID: " + procurementCaseId));

        List<VendorQuote> quotes = vendorQuoteRepository.findByProcurementCaseProcurementCaseIdOrderByEffectiveCostAsc(procurementCaseId);
        List<ApprovalHistory> historyList = approvalHistoryRepository.findByProcurementCaseProcurementCaseIdOrderByApprovalCycleAscApprovalLevelAsc(procurementCaseId);

        String uploadDir = "uploads/cs_pdfs/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = "CS_" + procurementCase.getProcurementCode() + "_v" + procurementCase.getDraftNumber() + ".pdf";
        String filePath = uploadDir + fileName;

        // A4 Landscape Document (842 x 595 pt) with 20pt margins
        Document document = new Document(PageSize.A4.rotate(), 20, 20, 20, 20);

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            PdfWriter.getInstance(document, fos);
            document.open();

            Company company = procurementCase.getCreatedBy() != null ? procurementCase.getCreatedBy().getCompany() : null;
            createDocumentHeader(document, company);
            document.add(createSpacing(8f));

            VendorQuote l1Quote = quotes.isEmpty() ? null : quotes.get(0);
            createProcurementSummaryAndRecommendedVendor(document, procurementCase, l1Quote);
            document.add(createSpacing(10f));

            createVendorComparisonSection(document, quotes);
            document.add(createSpacing(10f));

            createApprovalHistorySection(document, historyList);
            document.add(createSpacing(12f));

            ApprovalHistory finalApproval = historyList.stream()
                    .filter(h -> h.getAction() == Action.APPROVED)
                    .reduce((first, second) -> second)
                    .orElse(null);

            createDocumentFooter(document, procurementCase, finalApproval);

            document.close();
            log.info("PDF generated successfully at: {}", filePath);

            procurementCase.setCsPdfPath(filePath);
            procurementCaseRepository.save(procurementCase);

            return filePath;
        } catch (Exception e) {
            log.error("Failed to generate Comparative Statement PDF for case ID: {}", procurementCaseId, e);
            throw new RuntimeException("Error generating Comparative Statement PDF: " + e.getMessage(), e);
        }
    }

    // =========================================================================
    // SECTION IMPLEMENTATIONS WITH NAVY UNDERLINE SECTION HEADERS (NO FILL)
    // =========================================================================

    private void createDocumentHeader(Document doc, Company company) throws Exception {
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{55f, 45f});

        // Left Branding
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(PdfPCell.NO_BORDER);
        leftCell.setPadding(0f);
        leftCell.addElement(new Paragraph("SMARTPROCURE", FONT_BRAND_NAVY));
        
        Paragraph subTitle = new Paragraph("Comparative Statement (CS)", FONT_HEADER_SUB);
        subTitle.setSpacingBefore(2f);
        leftCell.addElement(subTitle);
        headerTable.addCell(leftCell);

        // Right Company Info
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(PdfPCell.NO_BORDER);
        rightCell.setPadding(0f);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Paragraph compName = new Paragraph(company != null ? company.getCompanyName() : "SmartProcure Platform", FONT_COMPANY_NAME);
        compName.setAlignment(Element.ALIGN_RIGHT);
        rightCell.addElement(compName);

        if (company != null) {
            if (company.getAddress() != null) {
                Paragraph addr = new Paragraph(company.getAddress(), FONT_COMPANY_DETAILS);
                addr.setAlignment(Element.ALIGN_RIGHT);
                addr.setSpacingBefore(1f);
                rightCell.addElement(addr);
            }
            String contactInfo = "";
            if (company.getEmail() != null) contactInfo += "Email: " + company.getEmail();
            if (company.getPhone() != null) contactInfo += (contactInfo.isEmpty() ? "" : " | ") + "Phone: " + company.getPhone();
            
            if (!contactInfo.isEmpty()) {
                Paragraph contact = new Paragraph(contactInfo, FONT_COMPANY_DETAILS);
                contact.setAlignment(Element.ALIGN_RIGHT);
                contact.setSpacingBefore(1f);
                rightCell.addElement(contact);
            }
        }
        headerTable.addCell(rightCell);

        doc.add(headerTable);

        // Thin Full-width Navy Separator Line
        PdfPTable lineTable = new PdfPTable(1);
        lineTable.setWidthPercentage(100);
        lineTable.setSpacingBefore(4f);
        PdfPCell lineCell = new PdfPCell();
        lineCell.setBorder(PdfPCell.BOTTOM);
        lineCell.setBorderColor(COLOR_PRIMARY_NAVY);
        lineCell.setBorderWidth(1.2f);
        lineTable.addCell(lineCell);
        doc.add(lineTable);
    }

    private void createProcurementSummaryAndRecommendedVendor(Document doc, ProcurementCase cs, VendorQuote l1Quote) throws Exception {
        PdfPTable containerTable = new PdfPTable(2);
        containerTable.setWidthPercentage(100);
        containerTable.setWidths(new float[]{68f, 32f});

        // --- LEFT SUMMARY DETAILS ---
        PdfPTable summaryTable = new PdfPTable(6);
        summaryTable.setWidthPercentage(100);
        summaryTable.setWidths(new float[]{22f, 3f, 25f, 22f, 3f, 25f});

        addSummaryPair(summaryTable, "Procurement Code", cs.getProcurementCode(), true);
        addSummaryPair(summaryTable, "Date Created", formatDate(cs.getCreatedAt()), false);

        addSummaryPair(summaryTable, "Title", cs.getTitle(), true);
        addSummaryPair(summaryTable, "Quantity", cs.getQuantity() + " " + (cs.getUnit() != null ? cs.getUnit() : ""), false);

        addSummaryPair(summaryTable, "Description", cs.getDescription(), false);
        addSummaryPair(summaryTable, "Unit", cs.getUnit() != null ? cs.getUnit() : "Nos", false);

        BigDecimal pkgAmt = l1Quote != null ? l1Quote.getEffectiveCost() : cs.getPackageAmount();
        addSummaryPair(summaryTable, "Created By", formatAuthor(cs), false);
        addSummaryPair(summaryTable, "Package Amount (L1)", formatCurrencyWithSymbol(pkgAmt), true);

        addSummaryPair(summaryTable, "Draft Version", "Draft #" + (cs.getDraftNumber() != null ? cs.getDraftNumber() : 1), false);
        addSummaryStatusPair(summaryTable, "Status", cs.getStatus() != null ? cs.getStatus().name() : "DRAFT");

        containerTable.addCell(createCellNoBorder(summaryTable));

        // --- RIGHT RECOMMENDED VENDOR CARD ---
        PdfPTable vendorCard = new PdfPTable(1);
        vendorCard.setWidthPercentage(100);

        // Header Line: "Recommended Vendor" label + "L1" Badge
        PdfPTable badgeHeader = new PdfPTable(2);
        badgeHeader.setWidthPercentage(100);
        badgeHeader.setWidths(new float[]{75f, 25f});

        PdfPCell recLblCell = new PdfPCell(new Phrase("Recommended Vendor", FONT_PRICE_GREEN));
        recLblCell.setBorder(PdfPCell.NO_BORDER);
        badgeHeader.addCell(recLblCell);

        PdfPCell badgeCell = new PdfPCell(new Phrase("L1", FONT_BADGE_WHITE));
        badgeCell.setBackgroundColor(COLOR_BADGE_GREEN);
        badgeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        badgeCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        badgeCell.setPadding(2f);
        badgeCell.setBorder(PdfPCell.NO_BORDER);
        badgeHeader.addCell(badgeCell);

        vendorCard.addCell(createCellNoBorder(badgeHeader));

        // Vendor Name
        String vendorName = l1Quote != null 
                ? l1Quote.getVendorName() 
                : (cs.getRecommendedVendor() != null ? cs.getRecommendedVendor() : "Pending Evaluation");
        Paragraph vNamePara = new Paragraph(vendorName, FONT_COMPANY_NAME);
        vNamePara.setSpacingBefore(3f);
        vendorCard.addCell(createCellNoBorder(vNamePara));

        // Dotted Separator Line
        PdfPTable dottedTable = new PdfPTable(1);
        dottedTable.setWidthPercentage(100);
        dottedTable.setSpacingBefore(4f);
        dottedTable.setSpacingAfter(4f);
        PdfPCell dCell = new PdfPCell();
        dCell.setBorder(PdfPCell.BOTTOM);
        dCell.setBorderColor(COLOR_BORDER_GRAY);
        dCell.setBorderWidth(0.5f);
        dottedTable.addCell(dCell);
        vendorCard.addCell(createCellNoBorder(dottedTable));

        // Total Price Label & Price
        Paragraph costLbl = new Paragraph("Recommended Total Cost", FONT_SMALL_MUTED);
        vendorCard.addCell(createCellNoBorder(costLbl));

        BigDecimal totalCost = l1Quote != null ? l1Quote.getEffectiveCost() : cs.getPackageAmount();
        Paragraph priceVal = new Paragraph(formatCurrencyWithSymbol(totalCost), FONT_PRICE_LARGE);
        vendorCard.addCell(createCellNoBorder(priceVal));

        Paragraph incTrans = new Paragraph("(Including Transport)", FONT_SMALL_MUTED);
        vendorCard.addCell(createCellNoBorder(incTrans));

        PdfPCell rightCardCell = new PdfPCell(vendorCard);
        rightCardCell.setBorder(PdfPCell.LEFT);
        rightCardCell.setBorderColor(COLOR_BORDER_GRAY);
        rightCardCell.setBorderWidth(0.5f);
        rightCardCell.setPaddingLeft(10f);
        containerTable.addCell(rightCardCell);

        // Put container inside single outer border box
        PdfPTable outerWrapper = new PdfPTable(1);
        outerWrapper.setWidthPercentage(100);
        PdfPCell wrapCell = new PdfPCell(containerTable);
        wrapCell.setBorderColor(COLOR_BORDER_GRAY);
        wrapCell.setBorderWidth(0.75f);
        wrapCell.setPadding(6f);
        outerWrapper.addCell(wrapCell);

        doc.add(outerWrapper);
    }

    private void createVendorComparisonSection(Document doc, List<VendorQuote> quotes) throws Exception {
        doc.add(createSectionHeaderWithLine("VENDOR COMPARISON"));

        if (quotes == null || quotes.isEmpty()) {
            PdfPTable emptyTable = new PdfPTable(1);
            emptyTable.setWidthPercentage(100);
            PdfPCell cell = new PdfPCell(new Paragraph("No vendor quotes submitted for this procurement case.", FONT_SMALL_MUTED));
            cell.setPadding(10f);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderColor(COLOR_BORDER_GRAY);
            emptyTable.addCell(cell);
            doc.add(emptyTable);
            return;
        }

        int numVendors = quotes.size();
        float[] widths = new float[1 + numVendors];
        widths[0] = 18f;
        float vendorColWidth = 82f / numVendors;
        for (int i = 1; i <= numVendors; i++) {
            widths[i] = vendorColWidth;
        }

        PdfPTable table = new PdfPTable(widths);
        table.setWidthPercentage(100);

        // Header Row: Vendor Titles & Addresses
        table.addCell(createGridHeaderCell("Vendor Details"));
        for (int i = 0; i < numVendors; i++) {
            VendorQuote q = quotes.get(i);
            boolean isL1 = (i == 0);
            table.addCell(createVendorDetailCell(q, i + 1, isL1));
        }

        // Quoted Amount
        table.addCell(createGridLabelCell("Quoted Amount\n(Excluding Transport)"));
        for (int i = 0; i < numVendors; i++) {
            table.addCell(createVendorAmountCell(formatCurrencyWithSymbol(quotes.get(i).getQuotedAmount()), i == 0));
        }

        // Transport Cost
        table.addCell(createGridLabelCell("Transport Cost"));
        for (int i = 0; i < numVendors; i++) {
            table.addCell(createVendorAmountCell(formatCurrencyWithSymbol(quotes.get(i).getTransportationCost()), i == 0));
        }

        // Total Cost (Quoted Amount + Transport)
        table.addCell(createGridLabelCellBold("Total Cost\n(Quoted Amount + Transport)"));
        for (int i = 0; i < numVendors; i++) {
            table.addCell(createVendorTotalAmountCell(formatCurrencyWithSymbol(quotes.get(i).getEffectiveCost()), i == 0));
        }

        // Rank Badge Row
        table.addCell(createGridLabelCell("Rank"));
        for (int i = 0; i < numVendors; i++) {
            table.addCell(createRankBadgeCell(i + 1, i == 0));
        }

        // Terms & Conditions Block
        table.addCell(createGridLabelCell("Terms & Conditions"));
        for (int i = 0; i < numVendors; i++) {
            VendorQuote q = quotes.get(i);
            table.addCell(createVendorTermsCell(q, i == 0));
        }

        doc.add(table);
    }

    private void createApprovalHistorySection(Document doc, List<ApprovalHistory> historyList) throws Exception {
        doc.add(createSectionHeaderWithLine("APPROVAL HISTORY"));

        if (historyList == null || historyList.isEmpty()) {
            PdfPTable emptyTable = new PdfPTable(1);
            emptyTable.setWidthPercentage(100);
            PdfPCell cell = new PdfPCell(new Paragraph("No approval audit records found.", FONT_SMALL_MUTED));
            cell.setPadding(8f);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderColor(COLOR_BORDER_GRAY);
            emptyTable.addCell(cell);
            doc.add(emptyTable);
            return;
        }

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{10f, 25f, 25f, 15f, 25f, 20f});

        // Header Row
        addAuditHeaderCell(table, "Level");
        addAuditHeaderCell(table, "Approver Name");
        addAuditHeaderCell(table, "Designation");
        addAuditHeaderCell(table, "Action");
        addAuditHeaderCell(table, "Date & Time");
        addAuditHeaderCell(table, "Remarks");

        for (int i = 0; i < historyList.size(); i++) {
            ApprovalHistory h = historyList.get(i);

            // Level (Centered)
            PdfPCell lvlCell = new PdfPCell(new Phrase(String.valueOf(h.getApprovalLevel() != null ? h.getApprovalLevel() : 1), FONT_VALUE));
            lvlCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            lvlCell.setBorderColor(COLOR_BORDER_GRAY);
            lvlCell.setBorderWidth(0.5f);
            lvlCell.setPadding(5f);
            table.addCell(lvlCell);

            String approverName = h.getApprover() != null 
                    ? h.getApprover().getFirstName() + " " + h.getApprover().getLastName() 
                    : "N/A";
            table.addCell(createGridCell(approverName));

            String desig = h.getApprover() != null && h.getApprover().getDesignation() != null 
                    ? formatEnumString(h.getApprover().getDesignation().name()) 
                    : "N/A";
            table.addCell(createGridCell(desig));

            // Action Status
            Font actionFont = FONT_VALUE_BOLD;
            if (h.getAction() == Action.APPROVED) {
                actionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, COLOR_L1_GREEN);
            } else if (h.getAction() == Action.REJECTED) {
                actionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, COLOR_RED_TEXT);
            } else if (h.getAction() == Action.PENDING) {
                actionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, COLOR_AMBER_TEXT);
            }
            PdfPCell actCell = new PdfPCell(new Phrase(h.getAction() != null ? h.getAction().name() : "PENDING", actionFont));
            actCell.setBorderColor(COLOR_BORDER_GRAY);
            actCell.setBorderWidth(0.5f);
            actCell.setPadding(5f);
            table.addCell(actCell);

            table.addCell(createGridCell(formatDateTime(h.getCreatedAt())));
            table.addCell(createGridCell(h.getRemarks() != null ? h.getRemarks() : "-"));
        }

        doc.add(table);
    }

    private void createDocumentFooter(Document doc, ProcurementCase cs, ApprovalHistory finalApproval) throws Exception {
        PdfPTable footerTable = new PdfPTable(3);
        footerTable.setWidthPercentage(100);
        footerTable.setWidths(new float[]{38f, 38f, 24f});

        // Prepared By Box
        PdfPCell prepCell = new PdfPCell();
        prepCell.setBorderColor(COLOR_BORDER_GRAY);
        prepCell.setBorderWidth(0.75f);
        prepCell.setPadding(6f);
        prepCell.addElement(new Paragraph("Prepared By", FONT_LABEL_BOLD));
        String prepName = cs.getCreatedBy() != null ? cs.getCreatedBy().getFirstName() + " " + cs.getCreatedBy().getLastName() : "System Author";
        prepCell.addElement(new Paragraph(prepName, FONT_VALUE_BOLD));
        if (cs.getCreatedBy() != null && cs.getCreatedBy().getDesignation() != null) {
            prepCell.addElement(new Paragraph(formatEnumString(cs.getCreatedBy().getDesignation().name()), FONT_SMALL_MUTED));
        }
        prepCell.addElement(new Paragraph("Date: " + formatDate(cs.getCreatedAt()), FONT_SMALL_MUTED));
        footerTable.addCell(prepCell);

        // Final Approved By Box
        PdfPCell appCell = new PdfPCell();
        appCell.setBorderColor(COLOR_BORDER_GRAY);
        appCell.setBorderWidth(0.75f);
        appCell.setPadding(6f);
        appCell.addElement(new Paragraph("Final Approved By", FONT_LABEL_BOLD));
        if (finalApproval != null && finalApproval.getApprover() != null) {
            String appName = finalApproval.getApprover().getFirstName() + " " + finalApproval.getApprover().getLastName();
            appCell.addElement(new Paragraph(appName, FONT_VALUE_BOLD));
            if (finalApproval.getApprover().getDesignation() != null) {
                appCell.addElement(new Paragraph(formatEnumString(finalApproval.getApprover().getDesignation().name()), FONT_SMALL_MUTED));
            }
            appCell.addElement(new Paragraph("Date: " + formatDate(finalApproval.getCreatedAt()), FONT_SMALL_MUTED));
        } else {
            appCell.addElement(new Paragraph("Pending Final Approval", FONT_SMALL_ITALIC));
        }
        footerTable.addCell(appCell);

        // System Generation Metadata
        PdfPCell metaCell = new PdfPCell();
        metaCell.setBorder(PdfPCell.NO_BORDER);
        metaCell.setPadding(6f);
        metaCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        Paragraph p1 = new Paragraph("This is a system generated document.", FONT_SMALL_ITALIC);
        p1.setAlignment(Element.ALIGN_RIGHT);
        metaCell.addElement(p1);
        
        Paragraph p2 = new Paragraph("Generated on: " + formatDateTime(LocalDateTime.now()), FONT_SMALL_MUTED);
        p2.setAlignment(Element.ALIGN_RIGHT);
        metaCell.addElement(p2);
        
        Paragraph p3 = new Paragraph("Page 1 of 1", FONT_SMALL_MUTED);
        p3.setAlignment(Element.ALIGN_RIGHT);
        p3.setSpacingBefore(4f);
        metaCell.addElement(p3);
        
        footerTable.addCell(metaCell);

        doc.add(footerTable);
    }

    // =========================================================================
    // HELPER & UTILITY METHODS (NO SOLID BACKGROUND RECTANGLES)
    // =========================================================================

    private Paragraph createSpacing(float space) {
        Paragraph p = new Paragraph(" ");
        p.setSpacingBefore(space);
        return p;
    }

    private PdfPTable createSectionHeaderWithLine(String title) {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(6f);

        Paragraph p = new Paragraph(title, FONT_SECTION_HEADING);
        p.setAlignment(Element.ALIGN_CENTER);
        
        PdfPCell cell = new PdfPCell(p);
        cell.setBackgroundColor(COLOR_BG_WHITE);
        cell.setBorder(PdfPCell.BOTTOM);
        cell.setBorderColor(COLOR_PRIMARY_NAVY);
        cell.setBorderWidth(1.0f);
        cell.setPaddingBottom(3f);

        table.addCell(cell);
        return table;
    }

    private PdfPCell createCellNoBorder(Object content) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setPadding(0f);
        if (content instanceof PdfPTable t) cell.addElement(t);
        else if (content instanceof Paragraph p) cell.addElement(p);
        return cell;
    }

    private void addSummaryPair(PdfPTable table, String label, String val, boolean boldVal) {
        PdfPCell lCell = new PdfPCell(new Phrase(label, FONT_LABEL_BOLD));
        lCell.setBorder(PdfPCell.NO_BORDER);
        lCell.setPadding(2.5f);
        table.addCell(lCell);

        PdfPCell colCell = new PdfPCell(new Phrase(":", FONT_LABEL_BOLD));
        colCell.setBorder(PdfPCell.NO_BORDER);
        colCell.setPadding(2.5f);
        table.addCell(colCell);

        PdfPCell vCell = new PdfPCell(new Phrase(val != null ? val : "N/A", boldVal ? FONT_VALUE_BOLD : FONT_VALUE));
        vCell.setBorder(PdfPCell.NO_BORDER);
        vCell.setPadding(2.5f);
        table.addCell(vCell);
    }

    private void addSummaryStatusPair(PdfPTable table, String label, String statusStr) {
        PdfPCell lCell = new PdfPCell(new Phrase(label, FONT_LABEL_BOLD));
        lCell.setBorder(PdfPCell.NO_BORDER);
        lCell.setPadding(2.5f);
        table.addCell(lCell);

        PdfPCell colCell = new PdfPCell(new Phrase(":", FONT_LABEL_BOLD));
        colCell.setBorder(PdfPCell.NO_BORDER);
        colCell.setPadding(2.5f);
        table.addCell(colCell);

        Font statusFont = FONT_VALUE_BOLD;
        if ("APPROVED".equalsIgnoreCase(statusStr)) {
            statusFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, COLOR_L1_GREEN);
        } else if ("REJECTED".equalsIgnoreCase(statusStr)) {
            statusFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, COLOR_RED_TEXT);
        } else if ("UNDER_REVIEW".equalsIgnoreCase(statusStr) || "SUBMITTED".equalsIgnoreCase(statusStr)) {
            statusFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, COLOR_AMBER_TEXT);
        }
        PdfPCell vCell = new PdfPCell(new Phrase(statusStr, statusFont));
        vCell.setBorder(PdfPCell.NO_BORDER);
        vCell.setPadding(2.5f);
        table.addCell(vCell);
    }

    private PdfPCell createVendorDetailCell(VendorQuote q, int rankNum, boolean isL1) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(isL1 ? COLOR_L1_BG : COLOR_BG_WHITE);
        cell.setBorderColor(isL1 ? COLOR_L1_BORDER : COLOR_BORDER_GRAY);
        cell.setBorderWidth(isL1 ? 1.0f : 0.5f);
        cell.setPadding(5f);

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9f, isL1 ? COLOR_L1_GREEN : COLOR_PRIMARY_NAVY);
        cell.addElement(new Paragraph(rankNum + ". " + q.getVendorName(), titleFont));

        if (q.getRemarks() != null && !q.getRemarks().isBlank()) {
            Paragraph p = new Paragraph(q.getRemarks(), FONT_SMALL_MUTED);
            p.setSpacingBefore(2f);
            cell.addElement(p);
        }
        return cell;
    }

    private PdfPCell createVendorAmountCell(String amountStr, boolean isL1) {
        Font font = isL1 ? FONT_PRICE_GREEN : FONT_VALUE;
        PdfPCell cell = new PdfPCell(new Phrase(amountStr, font));
        cell.setBackgroundColor(isL1 ? COLOR_L1_BG : COLOR_BG_WHITE);
        cell.setBorderColor(isL1 ? COLOR_L1_BORDER : COLOR_BORDER_GRAY);
        cell.setBorderWidth(isL1 ? 1.0f : 0.5f);
        cell.setPadding(5f);
        return cell;
    }

    private PdfPCell createVendorTotalAmountCell(String amountStr, boolean isL1) {
        Font font = isL1 ? FONT_PRICE_GREEN : FONT_VALUE_BOLD;
        PdfPCell cell = new PdfPCell(new Phrase(amountStr, font));
        cell.setBackgroundColor(isL1 ? COLOR_L1_BG : COLOR_BG_WHITE);
        cell.setBorderColor(isL1 ? COLOR_L1_BORDER : COLOR_BORDER_GRAY);
        cell.setBorderWidth(isL1 ? 1.0f : 0.5f);
        cell.setPadding(5f);
        return cell;
    }

    private PdfPCell createRankBadgeCell(int rankNum, boolean isL1) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(isL1 ? COLOR_L1_BG : COLOR_BG_WHITE);
        cell.setBorderColor(isL1 ? COLOR_L1_BORDER : COLOR_BORDER_GRAY);
        cell.setBorderWidth(isL1 ? 1.0f : 0.5f);
        cell.setPadding(5f);

        String badgeText = isL1 ? "L1 (Recommended)" : "L" + rankNum;
        Font badgeFont = isL1 ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, COLOR_L1_GREEN) : FONT_VALUE_BOLD;
        cell.addElement(new Paragraph(badgeText, badgeFont));
        return cell;
    }

    private PdfPCell createVendorTermsCell(VendorQuote q, boolean isL1) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(isL1 ? COLOR_L1_BG : COLOR_BG_WHITE);
        cell.setBorderColor(isL1 ? COLOR_L1_BORDER : COLOR_BORDER_GRAY);
        cell.setBorderWidth(isL1 ? 1.0f : 0.5f);
        cell.setPadding(4f);

        addAlignedBulletPair(cell, "Delivery", q.getDeliveryPeriod());
        addAlignedBulletPair(cell, "Warranty", q.getWarranty());
        addAlignedBulletPair(cell, "Validity", q.getValidity());
        addAlignedBulletPair(cell, "Payment Terms", q.getPaymentTerms());
        if (q.getRemarks() != null && !q.getRemarks().isBlank()) {
            addAlignedBulletPair(cell, "Remarks", q.getRemarks());
        }

        return cell;
    }

    private void addAlignedBulletPair(PdfPCell parentCell, String key, String val) {
        String displayVal = (val != null && !val.isBlank()) ? val : "N/A";
        Paragraph p = new Paragraph("• " + key + " : " + displayVal, FONT_SMALL_MUTED);
        p.setLeading(9.5f);
        parentCell.addElement(p);
    }

    private PdfPCell createGridHeaderCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_LABEL_BOLD));
        cell.setBackgroundColor(COLOR_BG_WHITE);
        cell.setBorderColor(COLOR_BORDER_GRAY);
        cell.setBorderWidth(0.5f);
        cell.setPadding(5f);
        return cell;
    }

    private PdfPCell createGridLabelCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_LABEL_BOLD));
        cell.setBackgroundColor(COLOR_BG_WHITE);
        cell.setBorderColor(COLOR_BORDER_GRAY);
        cell.setBorderWidth(0.5f);
        cell.setPadding(5f);
        return cell;
    }

    private PdfPCell createGridLabelCellBold(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, COLOR_PRIMARY_NAVY)));
        cell.setBackgroundColor(COLOR_BG_WHITE);
        cell.setBorderColor(COLOR_BORDER_GRAY);
        cell.setBorderWidth(0.5f);
        cell.setPadding(5f);
        return cell;
    }

    private void addAuditHeaderCell(PdfPTable table, String title) {
        PdfPCell cell = new PdfPCell(new Phrase(title, FONT_TABLE_HEADER));
        cell.setBackgroundColor(COLOR_BG_LIGHT_GRAY);
        cell.setBorderColor(COLOR_BORDER_GRAY);
        cell.setBorderWidth(0.5f);
        cell.setPadding(5f);
        table.addCell(cell);
    }

    private PdfPCell createGridCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "-", FONT_VALUE));
        cell.setBackgroundColor(COLOR_BG_WHITE);
        cell.setBorderColor(COLOR_BORDER_GRAY);
        cell.setBorderWidth(0.5f);
        cell.setPadding(5f);
        return cell;
    }

    private String formatAuthor(ProcurementCase cs) {
        if (cs.getCreatedBy() == null) return "System";
        String name = cs.getCreatedBy().getFirstName() + " " + cs.getCreatedBy().getLastName();
        if (cs.getCreatedBy().getDesignation() != null) {
            name += " (" + formatEnumString(cs.getCreatedBy().getDesignation().name()) + ")";
        }
        return name;
    }

    private String formatCurrencyWithSymbol(BigDecimal amount) {
        if (amount == null) return "₹0.00";
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.of("en", "IN"));
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setMaximumFractionDigits(2);
        return "₹" + currencyFormat.format(amount);
    }

    private String formatDate(Object dateObj) {
        if (dateObj == null) return "N/A";
        if (dateObj instanceof LocalDate ld) return ld.format(DATE_FORMATTER);
        if (dateObj instanceof LocalDateTime ldt) return ldt.format(DATE_FORMATTER);
        return dateObj.toString();
    }

    private String formatDateTime(LocalDateTime dt) {
        if (dt == null) return "N/A";
        return dt.format(DATETIME_FORMATTER);
    }

    private String formatEnumString(String enumStr) {
        if (enumStr == null) return "";
        String[] words = enumStr.split("_");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) {
                sb.append(Character.toUpperCase(w.charAt(0)))
                  .append(w.substring(1).toLowerCase())
                  .append(" ");
            }
        }
        return sb.toString().trim();
    }

    @Override
    public Resource downloadComparativeStatementPdf(Long procurementCaseId) {

        ProcurementCase procurementCase =
                procurementCaseRepository.findById(procurementCaseId)
                .orElseThrow(() ->
                new ResourceNotFoundException(
                        "Procurement Case not found."));

        if (procurementCase.getCsPdfPath() == null) {
            throw new ResourceNotFoundException(
                    "Comparative Statement PDF not generated yet.");
        }

        try {

            Path path = Paths.get(procurementCase.getCsPdfPath());

            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists() || !resource.isReadable()) {

                throw new RuntimeException(
                        "Unable to read PDF file.");

            }

            return resource;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error reading PDF.", e);

        }
    }
}
