package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExcelReader {
    private static final Logger logger = LogManager.getLogger(ExcelReader.class);

    /**
     * Reads Excel data and returns as List of Maps
     * Each Map represents a row with column headers as keys
     */
    public static List<Map<String, String>> readExcelData(String filePath, String sheetName) {
        List<Map<String, String>> data = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("Sheet not found: " + sheetName);
            }

            Row headerRow = sheet.getRow(0);
            int columnCount = headerRow.getLastCellNum();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Map<String, String> rowData = new LinkedHashMap<>();

                for (int j = 0; j < columnCount; j++) {
                    Cell headerCell = headerRow.getCell(j);
                    Cell dataCell = row.getCell(j);

                    String header = getCellValueAsString(headerCell);
                    String value = getCellValueAsString(dataCell);

                    rowData.put(header, value);
                }

                data.add(rowData);
            }

            logger.info("Successfully read " + data.size() + " rows from Excel");

        } catch (IOException e) {
            logger.error("Error reading Excel file: " + e.getMessage());
            throw new RuntimeException("Failed to read Excel file", e);
        }

        return data;
    }

    /**
     * Converts cell value to String based on cell type
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    /**
     * Returns Excel data as Object[][] for TestNG DataProvider
     * Each row contains one Map object
     */
    public static Object[][] getExcelDataAsArray(String filePath, String sheetName) {
        List<Map<String, String>> dataList = readExcelData(filePath, sheetName);
        Object[][] data = new Object[dataList.size()][1];

        for (int i = 0; i < dataList.size(); i++) {
            data[i][0] = dataList.get(i);
        }

        return data;
    }

    /**
     * Returns Excel data as Object[][] with separate columns
     * Each row contains individual column values
     */
    public static Object[][] getExcelDataAsColumns(String filePath, String sheetName) {
        List<Map<String, String>> dataList = readExcelData(filePath, sheetName);
        
        if (dataList.isEmpty()) {
            return new Object[0][0];
        }
        
        // Get column headers
        Map<String, String> firstRow = dataList.get(0);
        String[] headers = firstRow.keySet().toArray(new String[0]);
        
        Object[][] data = new Object[dataList.size()][headers.length];
        
        for (int i = 0; i < dataList.size(); i++) {
            Map<String, String> row = dataList.get(i);
            for (int j = 0; j < headers.length; j++) {
                data[i][j] = row.get(headers[j]);
            }
        }
        
        return data;
    }

    /**
     * Gets specific cell value from Excel
     */
    public static String getCellData(String filePath, String sheetName, int rowNum, int colNum) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            Row row = sheet.getRow(rowNum);
            Cell cell = row.getCell(colNum);

            return getCellValueAsString(cell);

        } catch (IOException e) {
            logger.error("Error reading cell data: " + e.getMessage());
            throw new RuntimeException("Failed to read cell data", e);
        }
    }

    /**
     * Gets total row count (excluding header)
     */
    public static int getRowCount(String filePath, String sheetName) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            return sheet.getLastRowNum();

        } catch (IOException e) {
            logger.error("Error getting row count: " + e.getMessage());
            throw new RuntimeException("Failed to get row count", e);
        }
    }

    /**
     * Gets total column count
     */
    public static int getColumnCount(String filePath, String sheetName) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            Row row = sheet.getRow(0);
            return row.getLastCellNum();

        } catch (IOException e) {
            logger.error("Error getting column count: " + e.getMessage());
            throw new RuntimeException("Failed to get column count", e);
        }
    }
}