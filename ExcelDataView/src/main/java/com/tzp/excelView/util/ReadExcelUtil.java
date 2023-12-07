package com.tzp.excelView.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 读取Excel工具类
 *
 * @author Dong
 */
public class ReadExcelUtil {

    /**
     * 读取Excel文件
     *
     * @param excelFilePath Excel文件路径
     * @return 每一列内容
     */
    public static List<List<Object>> readExcel(String excelFilePath) {
        List<List<Object>> excelData = new ArrayList<>();

        try (FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = sheet.iterator();

            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();

                List<Object> rowData = new ArrayList<>();

                while (cellIterator.hasNext()) {
                    Cell currentCell = cellIterator.next();
                    rowData.add(getCellValue(currentCell));
                }

                excelData.add(rowData);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return excelData;
    }

    private static Object getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return cell.getNumericCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            default:
                return "";
        }
    }

}
