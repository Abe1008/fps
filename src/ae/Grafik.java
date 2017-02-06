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

    /**
     * Загрузить график из файла в БД
     *
     * @param fileName  имя текстового файла с графиком нагрузки счетчика
     * @param db        база данных с таблицей rep(Dat,Tim,Delta,V1,V2)
     * @return          кол-во загружнных записей
     */
    public int load(String fileName, Database db) {
        int cnt = 0;
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileName), StandardCharsets.US_ASCII)
            ); // .UTF_8
            String line, str, sql;
            int a;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                String[] t = line.split("[\\s\"/:]+");
                String sdat;
                sdat = String.format("20%s-%s-%s", t[3],t[2],t[1]);
                sdat = "20" + t[3] + "-" + t[2] + "-" + t[1];  // 2016-10-18
                String stim;
                stim = t[4] + ":" + t[5];  // 00:30
                int h, m;
                h = Integer.parseInt(t[4]);
                m = Integer.parseInt(t[5]);
                m = (30 + m) / 60;
                h= h + m; // 02:00 -> 2
                // '2016-10-18', '00:30', 30, 0.0300, 0.0040, 00
                str = "'" + sdat + "', '" + stim + "', " + t[6] + ", " + t[7] + ", " + t[8] + ", " + h;
                //  INSERT INTO rep (Dat,Tim,Delta,V1,V2,H) VALUES ('2016-10-18', '00:30', 30, 0.0300, 0.0040, 00);
                sql = " INSERT INTO rep (Dat,Tim,Delta,V1,V2,H) VALUES (" + str + ");";
                a = db.ExecSql(sql);
                cnt += a;






            }
        } catch (IOException e) {
            e.printStackTrace();    // log error
        }


        return cnt;

    }

}
