/*
 * Copyright (c) 2017. Aleksey Eremin
 * 05.02.17 17:17
 */

package ae;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Created by ae on 05.02.2017.
 * График нагрузки счетчика
 * "              " "18/10/16" "00:30" 30  0.0300  0.0040       0       0
 * "              " "18/10/16" "01:00" 30  0.0295  0.0035       0       0
 * "              " "18/10/16" "01:30" 30  0.0300  0.0035       0       0
 * "              " "18/10/16" "02:00" 30  0.0305  0.0040       0       0
 *
 */
public class Grafik {

    // данные 1-ой строки графика из счетчика
    class rowgraf {
        String  dat;
        int     year;
        int     mon;
        int     day;
        int     hh;
        int     mm;
        int     delta;
        double  v1;
        double  v2;
        int     h;
    }
    
    /**
     * Загрузить график из файла в БД
     *
     * @param fileName  имя текстового файла с графиком нагрузки счетчика
     * @param db        база данных с таблицей rep(Dat,Tim,Delta,V1,V2)
     * @return          кол-во загружнных записей
     */
    public int load(String fileName, Database db) {
        int cnt = 0;
        int a;
        String line;
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileName), StandardCharsets.US_ASCII)
            );
            // для увеличения скорости вставки (http://www.sql.ru/forum/688069/kak-uvelichit-skorost-vstavki-bolshogo-chisla-insert-v-sqlite)
            db.ExecSql("BEGIN TRANSACTION;");
            while((line = reader.readLine()) != null) {
                rowgraf rg = getRow(line); // строка графика
                a = putRow(rg, db);
                cnt += a;
            }
            db.ExecSql("COMMIT;");
        } catch (IOException e) {
            e.printStackTrace();    // log error
        }
        return cnt;
    }
    
    /**
     * Разобрать входную строку графика на значения для вставки в БД
     * @param inputStr  входная строка графитка
     * @return          объект "строка данных", null если ошибка
     */
    private rowgraf getRow(String inputStr)
    {
        String[] t = inputStr.split("[\\s\"/:]+");
        rowgraf rg = new rowgraf();
        rg.year = Integer.parseInt("20"+t[3]); // год
        rg.mon = Integer.parseInt(t[2]);  // месяц
        rg.day = Integer.parseInt(t[1]);  // день месяца
        rg.hh = Integer.parseInt(t[4]);     // час
        rg.mm = Integer.parseInt(t[5]);     // минута 00:30
        rg.delta = Integer.parseInt(t[6]);
        rg.v1 = Double.parseDouble(t[7]);
        rg.v2 = Double.parseDouble(t[8]);
        rg.h = rg.hh;
        if(rg.mm > 0) rg.h++; // несколько минут относит нас в следующий часовой интервал
        //
        return rg;
    }
    
    /**
     * Положить строку графика в БД
     * @param rg    строка графика
     * @param db    база данных
     * @return      1-записана строка данных, 0-не записана
     */
    private int putRow(rowgraf rg, Database db)
    {
        int a = 0;
        int h, m;
        String str, sql;
        
        if(rg != null) {
            str = rg.year + ", " + rg.mon + ", " + rg.day +   ", " +
                    rg.hh + ", " + rg.mm +  ", " + rg.delta + ", " +
                    rg.v1 + ", " + rg.v2 +  ", " + rg.h;
            // INSERT INTO rep (year,mon,day,hh,mm,Delta,V1,V2,H) VALUES (2016, 10, 18, 0, 30, 30, 0.03, 0.004, 1);
            sql = " INSERT INTO rep (year,mon,day,hh,mm,Delta,V1,V2,H) VALUES (" + str + ");";
            a = db.ExecSql(sql);
            return a;
        }
        return 0;
    }
    

}
