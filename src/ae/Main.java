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
        if(l < 2) {
            String msg = r.readRes("res/hello.txt");
            System.out.println(msg);
            return;
        }
        String sinp = args[0];  // входной файл
        String sout = args[1];  // выходной файл
        if(!r.writeRes2File("res/srcout.xlsx", sout)) {
            System.out.println("?ERROR-can't write file: " + sout);
            return;
        }
        //
        DatabaseUse db = new DatabaseUse();  // открыть временную базу данных
        String  dbn = db.getDatabaseName();
        Grafik  grf = new Grafik();
        int a;
        a=grf.load(sinp, db);

        System.out.println("Load strings: " + a);

        db.close();
        // db.deleteDatabase();



    }
}
