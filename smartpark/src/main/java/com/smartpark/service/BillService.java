package com.smartpark.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.smartpark.model.ParkingLog;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class BillService {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public byte[] generateBillPdf(ParkingLog log) throws Exception {
        Document doc = new Document(PageSize.A5);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(doc, out);
        doc.open();

        Font titleFont   = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
        Font headerFont  = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
        Font normalFont  = new Font(Font.FontFamily.HELVETICA, 11);
        Font totalFont   = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);

        // Title
        Paragraph title = new Paragraph("SMARTPARK", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);

        Paragraph subtitle = new Paragraph("AI Vehicle Parking System", normalFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        doc.add(subtitle);
        doc.add(Chunk.NEWLINE);

        // Divider
        doc.add(new Paragraph("----------------------------------------", normalFont));
        doc.add(new Paragraph("PARKING BILL", headerFont));
        doc.add(new Paragraph("----------------------------------------", normalFont));
        doc.add(Chunk.NEWLINE);

        // Bill details table
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{40f, 60f});

        addRow(table, "Bill ID",       String.valueOf(log.getId()),        headerFont, normalFont);
        addRow(table, "Plate Number",  log.getPlateNumber(),               headerFont, normalFont);
        addRow(table, "Entry Time",    log.getEntryTime().format(FMT),     headerFont, normalFont);
        addRow(table, "Exit Time",     log.getExitTime() != null ?
                log.getExitTime().format(FMT) : "-",headerFont, normalFont);
        addRow(table, "Duration",      log.getDurationMinutes() + " minutes", headerFont, normalFont);
        addRow(table, "Rate",          "Rs. 10.00 / hour",                 headerFont, normalFont);

        doc.add(table);
        doc.add(Chunk.NEWLINE);

        // Total amount
        doc.add(new Paragraph("----------------------------------------", normalFont));
        Paragraph total = new Paragraph(
                "TOTAL AMOUNT:  Rs. " + String.format("%.2f", log.getFeeAmount()), totalFont);
        total.setAlignment(Element.ALIGN_RIGHT);
        doc.add(total);
        doc.add(new Paragraph("----------------------------------------", normalFont));
        doc.add(Chunk.NEWLINE);

        Paragraph footer = new Paragraph("Thank you for using SmartPark!\nPowered by Java + Spring Boot + AI", normalFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        doc.add(footer);

        doc.close();
        return out.toByteArray();
    }

    private void addRow(PdfPTable table, String label, String value,
                        Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(6);
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(6);
        table.addCell(labelCell);
        table.addCell(valueCell);
    }
}