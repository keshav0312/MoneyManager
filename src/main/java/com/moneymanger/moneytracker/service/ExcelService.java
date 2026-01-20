package com.moneymanger.moneytracker.service;

import com.moneymanger.moneytracker.dto.ExpenseDTO;
import com.moneymanger.moneytracker.dto.IncomeDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class ExcelService {

    // ===================== Income Excel =====================
    public void writeIncomesToExcel(OutputStream os, List<IncomeDTO> incomeDTOS) {
        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Incomes");

            // Header row
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Sno");
            header.createCell(1).setCellValue("Name");

            header.createCell(2).setCellValue("Category");
            header.createCell(3).setCellValue("Amount");
            header.createCell(4).setCellValue("Date");

            // Optional: make header bold
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            for (int i = 0; i < 5; i++) {
                header.getCell(i).setCellStyle(headerStyle);
            }

            // Data rows
            IntStream.range(0, incomeDTOS.size()).forEach(i -> {
                IncomeDTO dto = incomeDTOS.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(dto.getName() != null ? dto.getName() : "");
                row.createCell(2).setCellValue(dto.getCategoryId() != null ? dto.getCategoryName() : "");
                row.createCell(3).setCellValue(dto.getAmount() != null ? dto.getAmount().doubleValue() : 0);
                row.createCell(4).setCellValue(dto.getDate() != null ? dto.getDate().toString() : "");
            });

            // Auto-size columns
            for (int i = 0; i < 5; i++) sheet.autoSizeColumn(i);

            workbook.write(os);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to write Incomes Excel", e);
        }
    }

    // ===================== Expense Excel =====================
    public void writeExpensesToExcel(OutputStream os, List<ExpenseDTO> expenseDTOS) {
        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Expenses");

            // Header row
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Sno");
            header.createCell(1).setCellValue("Name");
            header.createCell(2).setCellValue("Category");
            header.createCell(3).setCellValue("Amount");
            header.createCell(4).setCellValue("Date");

            // Optional: make header bold
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            for (int i = 0; i < 5; i++) {
                header.getCell(i).setCellStyle(headerStyle);
            }

            // Data rows
            IntStream.range(0, expenseDTOS.size()).forEach(i -> {
                ExpenseDTO dto = expenseDTOS.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(dto.getName() != null ? dto.getName() : "");
                row.createCell(2).setCellValue(dto.getCategoryId() != null ? dto.getCategoryName() : "");
                row.createCell(3).setCellValue(dto.getAmount() != null ? dto.getAmount().doubleValue() : 0);
                row.createCell(4).setCellValue(dto.getDate() != null ? dto.getDate().toString() : "");
            });

            // Auto-size columns
            for (int i = 0; i < 5; i++) sheet.autoSizeColumn(i);

            workbook.write(os);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to write Expenses Excel", e);
        }
    }
}
