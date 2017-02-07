/*
 * Copyright (c) 2017. Aleksey Eremin
 * 07.02.17 17:28
 */

package ae;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.microsoft.schemas.office.visio.x2012.main.CellType.*;

/**
 * Created by ae on 07.02.2017.
 * заполняет данными лист Excel для одного месяца
 */
public class ExcelList {
    Database f_db;

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
        R r = new R();
        File f = new File(fileNameOut);
        String fp = f.getParent();
        String fn = f.getName();
        String ss = String.format("%04d-%02d-", year, month);
        String sout = fp + R.sep + ss + fn;
        if(!r.writeRes2File("res/srcout.xlsx", fileNameOut)) {
            System.out.println("?ERROR-can't write file: " + sout);
            return 0;
        }
        sout = "C:\\tmp\\abc.xls";
        String sql = "SELECT v1 FROM rep;";
        //
        try {
            FileInputStream inp = new FileInputStream(sout);
            //InputStream inp = new FileInputStream("workbook.xlsx");

//            Workbook wb = WorkbookFactory.create(inp);

            HSSFWorkbook wb = new HSSFWorkbook(inp); //Access the workbook

            // Read more: http://www.techartifact.com/blogs/2013/10/update-or-edit-existing-excel-files-in-java-using-apache-poi.html#ixzz4Y23Vf1eR

//            Sheet sheet = wb.getSheetAt(01);
            HSSFSheet worksheet = wb.getSheetAt(1); //Access the worksheet, so that we can update / modify it.

            Statement stm = f_db.getDbStatement();
            ResultSet rst = stm.executeQuery(sql);
            int i = 0;
            while (rst.next() && i<100) {
                double d = rst.getDouble(1);  // взять первый столбец
                //String str = rst.getString(1);  // взять первый столбец
//                Row row = sheet.getRow(i++);
//                Cell cell = row.getCell(0);
                Cell cell = null;
                cell = worksheet.getRow(i++).getCell(0);   // Access the cell
                if (cell != null) {
                    cell.setCellValue(d);
                }

            }
            rst.close();

            // Write the output to a file
            FileOutputStream fileOut = new FileOutputStream("workbook.xls");
            wb.write(fileOut);
            fileOut.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
    
}
