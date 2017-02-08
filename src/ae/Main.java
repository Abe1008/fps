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
        // int a = 123;
        R r = new R();  // ресурсный класс
        int l = args.length;
        if(l < 1) {
            String msg = r.readRes("res/hello.txt");
            System.out.println(msg);
            return;
        }
        String sinp = args[0];  // входной файл
        if(l>1) {
            R.outputFile = args[1];
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
