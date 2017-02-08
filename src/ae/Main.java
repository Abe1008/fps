/*
 * Copyright (c) 2017. Aleksey Eremin
 * 04.02.17 23:25
 */

package ae;

/*
 Чтение файла счетчика и формирование Excel-файла

 */
public class Main {

    public static void main(String[] args) {
        // write your code here
        R r = new R();  // ресурсный класс
        int l = args.length;
        if(l < 1) {
            String msg = r.getText("res/hello.txt", "Cp1251");
            System.out.println(msg);
            return;
        }
        String sinp = args[0];  // входной файл
        // указан выходной файл
        if(l > 1) {
            R.outputFile = args[1];
        }
        // указан коэффициент трансформации
        if(l > 2) {
            R.KT = Double.parseDouble(args[2]);
        }

        //
        DatabaseUse db = new DatabaseUse();  // открыть временную базу данных
        String  dbn = db.getDatabaseName();
        Grafik  grf = new Grafik();
        int a;
        a=grf.load(sinp, db);

        System.out.println("Load strings: " + a);

        ExcelList ex  = new ExcelList(db);
        //
        a = ex.writeAllList(R.outputFile);

        System.out.println("Write to Excel rows: " + a);

        db.close();
        // db.deleteDatabase();



    }
}
