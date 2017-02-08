/*
 * Copyright (c) 2017. Aleksey Eremin
 * 07.02.17 17:28
 */

package ae;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
     * Записывает в выходной файл данные из БД для указанного года и месяца
     * @param year      год
     * @param month     месяц
     * @param nameOut   выходное имя файла (к нему будет добавлен год и месяц)
     * @return      кол-во записей для указанного месяца
     */
    public int writeList(int year, int month, String nameOut)
    {
        final int Data_base_row = R.Data_base_row;       // базовая строка, для вставки данных расхода
        final int Date_base_col = R.Date_base_col;        // базовая колонка для вставки данных расхода
        final int Data_row_interval = R.Data_row_interval;   // интервал между блоками данных в таблице Excel
        final int ListDate_row = R.ListDate_row;         // строка для вставки даты месяца листа
        final int ListDate_col = R.ListDate_col;         // колонка для вставки даты месяца листа
        int cnt = 0;
        R rr = new R();
        File f = new File(nameOut);
        String fp = f.getParent();
        String fn = f.getName();
        String ss = String.format("%04d-%02d-", year, month);
        String sout = fp + R.sep + ss + fn;
        if(!rr.writeRes2File("res/srcout3.xls", sout)) {    // srcout.xlsx Excel 2010 srcout3.xls Excel 2003
            System.out.println("?ERROR-can't write file: " + sout);
            return 0;
        }
        //sout = "C:\\tmp\\abc7.xlsx";
        
        //
        try {
            FileInputStream inp = new FileInputStream(sout);
            // получим рабочую книгу Excel
            //Workbook wb = new XSSFWorkbook(inp); // прочитать файл с Excel 2010
            Workbook wb = new HSSFWorkbook(inp); // прочитать файл с Excel 2003
            inp.close();
            // Read more: http://www.techartifact.com/blogs/2013/10/update-or-edit-existing-excel-files-in-java-using-apache-poi.html#ixzz4Y23Vf1eR
            // получим первый лист
            Sheet wks = wb.getSheetAt(0); //Access the worksheet, so that we can update / modify it.
            // заполним лист данными
            Statement stm = f_db.getDbStatement();
            String sql = "SELECT day, H, SUM(v1) FROM rep " +
                    " WHERE year=" + year + " AND mon=" + month + " GROUP BY day,H;";
            ResultSet rst = stm.executeQuery(sql);
            int day0 = -1; // для проставления даты в столбце один раз
            while (rst.next()) {
                int day = rst.getInt(1) - 1 ;   // взять день
                int h = rst.getInt(2) - 1 ;     // взять час
                double d = rst.getDouble(3);    // взять сумму
                int j = day % 15;   // номер столбца дат
                int l = day / 15;   // номер блока дат
                if(l > 1) { // 31 число
                    l = 1;
                    j = 15;
                }
                int br = Data_base_row + l * Data_row_interval;   // базовая строка для вставки данных для данного числа месяца
                Row row = wks.getRow(br + h);
                if(row == null) {
                    row = wks.createRow(br + h);
                }
                // -------------------------
                // Вставим дату над колонкой 1 раз на дату
                if(day0 != day) {
                    day0 = day;
                    Cell c = wks.getRow(br - 1).getCell(Date_base_col + j);
                    c.setCellValue(new Date(year - 1900, month - 1, day + 1));
                }
                // -------------------------
                Cell c = row.getCell(Date_base_col + j);   // Access the cell
                if (c == null) {
                    c = row.createCell(Date_base_col + j); // создадим ячейку
                }
                if(c != null) {
                    c.setCellValue(d*R.KT);
                    cnt++;
                }
            }
            rst.close();
            // установить дату на листе
            Cell cell = wks.getRow(ListDate_row).getCell(ListDate_col);
            cell.setCellValue(new Date(year-1900, month-1, 1));
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
    
    /**
     * Записать файлы для месяцев, которые есть во входном файле PRN
     * @param nameOut   выходное имя файла (к нему будет добавлен год-месяц)
     * @return          количество записанных показаний
     */
    public int writeAllList(String nameOut) {
        int cnt = 0;
        int a;
        String sql = "SELECT DISTINCT year||'#'||mon FROM rep ORDER BY year, mon;";
        ArrayList<String> ars = f_db.DlookupArray(sql);
        // Вложенные операторы ResultSet rst = stm.executeQuery(sql);
        // приводят к тому что ResultSet сбрасывается вложенным обращением.
        for(String ss: ars) {
            String[] val = ss.split("#");
            int y = Integer.parseInt(val[0]);  // год
            int m = Integer.parseInt(val[1]);  // месяц
            a = writeList(y, m, nameOut);
            cnt += a;
        }
        return cnt;
    }
    
}
