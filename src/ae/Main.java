/*
 * Copyright (c) 2017. Aleksey Eremin
 * 04.02.17 23:25
 */

package ae;

import java.io.File;

/*
 Чтение файла счетчика и формирование Excel-файла

 */
public class Main {

    public static void main(String[] args) {
        // write your code here
        R r = new R();  // ресурсный класс
        int l = args.length;
        int i_out = l - 1;
        int a;
        /*
         * Если в качестве входного аргумента указать маску файла GRAFIK*.PRN, то кол-во аргументов
         * возрастет на кол-во реально существующих маске файлов, например будет
         * args[0] grafik1.prn
         * args[1] grafik2.prn
         * args[2] grafik3.prn
         * args[3] 1000.0 (Кт - действительное число)
         * Поэтому начинаем разбор с конца аргументов:
         * самый последний аргумент - число, если это число или входной файл
         * все остальные аргументы входные файлы
         *
         */
        if(l < 1) {
            String msg = r.getText("res/hello.txt", "Cp1251");
            System.out.println(msg);
            return;
        }
        String sout;
        // сформируем выходное имя файла
        File f = new File(args[0]);
        String path = f.getParent(); // название каталога
        if(path == null) { path = "."; }
        sout = path + R.sep + R.outputFileName;
        
        // указан коэффициент трансформации
        // он самый последний аргумент
        if(l > 1) {
            double d = R.KT;
            try {
                d = Double.parseDouble(args[l-1]);
                R.KT = d;
                l = l - 1;  // кол-во входных файлов
            } catch (NumberFormatException e) {
                ;// System.out.println("ошибка преобразования double, значит это не число, а файл");
            }
        }
        //
        DatabaseUse db = new DatabaseUse();  // открыть временную базу данных
        Grafik  grf = new Grafik(db);
        // прочитать репорт со счетчика
        for(int i = 0; i < l; i++) {
            a = grf.load(args[i]);
            System.out.println(args[i] + " - load strings: " + a);
        }
        //
        ExcelList ex  = new ExcelList(db);
        // записать данные в Excel
        a = ex.writeAllList(sout);
        System.out.println("Write to Excel rows: " + a);
        //
        db.close();
        // db.deleteDatabase();
    }

}
