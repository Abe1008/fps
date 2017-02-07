/*
 * Copyright (c) 2017. Aleksey Eremin
 * 07.02.17 17:28
 */

package ae;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

/**
 * Created by ae on 07.02.2017.
 * заполняет данными лист Excel для одного месяца
 */
public class ExcelList {
    private Database f_db;

    public ExcelList(Database db)
    {
        f_db = db;
    }
    
    /**
     * Записывает в выходной файл данные из БД для указанного месяца
     * @param year  год
     * @param month месяц
     * @return      кол-во записей для указанного месяца
     */
    public int writeList(int year, int month, String fileNameOut)
    {
        int cnt = 0;
        R rr = new R();
        File f = new File(fileNameOut);
        String fp = f.getParent();
        String fn = f.getName();
        String ss = String.format("%04d-%02d-", year, month);
        String sout = fp + R.sep + ss + fn;
        if(!rr.writeRes2File("res/srcout.xlsx", sout)) {
            System.out.println("?ERROR-can't write file: " + sout);
            return 0;
        }
        //sout = "C:\\tmp\\abc7.xlsx";
        String sql = "SELECT day,H,SUM(v1) FROM rep " +
                " WHERE year=" + year + " AND mon=" + month +
                " GROUP BY day,H;";
        //
        try {
            FileInputStream inp = new FileInputStream(sout);
            // полуим рабочую книгу Excel
            XSSFWorkbook wb = new XSSFWorkbook(inp); //Access the workbook
            inp.close();
            // Read more: http://www.techartifact.com/blogs/2013/10/update-or-edit-existing-excel-files-in-java-using-apache-poi.html#ixzz4Y23Vf1eR
            // получим первый лист
            XSSFSheet worksheet = wb.getSheetAt(0); //Access the worksheet, so that we can update / modify it.
            // заполним лист данными
            Statement stm = f_db.getDbStatement();
            ResultSet rst = stm.executeQuery(sql);
            while (rst.next()) {
                int day = rst.getInt(1) - 1 ;  // взять день
                int h = rst.getInt(2) - 1 ;  // взять час
                double d = rst.getDouble(3);  // взять сумму
                int j = day % 15;   // номер столбца дат
                int l = day / 15;   // номер блока дат
                if(l>1) { // 31 число
                    l = 1;
                    j = 15;
                }
                int br = 15 + l * 30;
                Cell cell = worksheet.getRow(br + h).getCell(1 + j);   // Access the cell
                if (cell == null) {
                    cell = worksheet.createRow(br+h).createCell(1+j);
                }
                if(cell != null) {
                    cell.setCellValue(d*R.KT);
                    cnt++;
                }
            }
            rst.close();
            // установить дату на листе
            Cell cell = worksheet.getRow(9).getCell(2);
            cell.setCellValue(new Date(year-1900,month-1,1));
            //
            // перерасчет всех формул на листе
            // http://poi.apache.org/spreadsheet/eval.html#Re-calculating+all+formulas+in+a+Workbook
            FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
            for (Sheet sheet : wb) {
                for (Row row : sheet) {
                    for (Cell c : row) {
                        if (c.getCellType() == Cell.CELL_TYPE_FORMULA) {
                            evaluator.evaluateFormulaCell(c);
                        }
                    }
                }
            }
            // Write the output to a file
            FileOutputStream fileOut = new FileOutputStream(sout);
            wb.write(fileOut);
            fileOut.close();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        return cnt;
    }
    
}
