/*
 * Copyright (c) 2017. Aleksey Eremin
 * 28.01.17 21:52
 */

package ae;

import java.io.*;
import java.util.Properties;

/**
 * Created by ae on 28.01.2017.
 * Ресурсный класс
 */
public class R {
    public final static String sep = System.getProperty("file.separator"); // разделитель имени каталогов
    public final static String tmpDir = System.getProperty("java.io.tmpdir"); // временный каталог (завершается обратным слэшем)

    public static String databaseName = "fps";   // имя базы данных
    public static String outputFile = tmpDir + "a.xls"; // выходное имя файла Excel

    public static double KT = 1800 ; //1800;                     // коэффициент трансформации

    //
    // параметры для внесения данных в лист Excel
    final static int Data_base_row = 15;       // базовая строка, для вставки данных расхода
    final static int Date_base_col = 1;        // базовая колонка для вставки данных расхода
    final static int Data_row_interval = 30;   // интервал между блоками данных в таблице Excel
    final static int ListDate_row = 9;         // строка для вставки даты месяца листа
    final static int ListDate_col = 2;         // колонка для вставки даты месяца листа
    
    /**
     * загрузка значений параметров по-умолчанию из файла res/default.properties
     */
    public void loadDefault()
    {
        // http://stackoverflow.com/questions/2815404/load-properties-file-in-jar
        /*
        Properties props = new Properties();
        try {
            
            props.load(R.class.getResourceAsStream("res/default.properties"));
            this.workDir = props.getProperty("workDir");
            this.screenDir = props.getProperty("screenDir");
            this.databaseName = props.getProperty("databaseName");
            this.addressSmtpServer = props.getProperty("addressSmtpServer");
            this.timeStore = Long.parseLong( props.getProperty("timeStore") );
            
            //
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }

    /**
     * прочитать ресурсный файл
     * by novel  http://skipy-ru.livejournal.com/5343.html
     * https://docs.oracle.com/javase/tutorial/deployment/webstart/retrievingResources.html
     * @param nameRes - имя ресурсного файла
     * @return -содержимое ресурсного файла
     */
    public String readRes(String nameRes)
    {
        String str = null;
        ByteArrayOutputStream buf = readResB(nameRes);
        if(buf != null) {
            str = buf.toString();
        }
        return str;
    }
    
    /**
     * Загружает текстовый ресурс в заданной кодировке
     * @param name      имя ресурса
     * @param code_page кодировка, например "Cp1251"
     * @return          строка ресурса
     */
    public String getText(String name, String code_page)
    {
        StringBuilder sb = new StringBuilder();
        try {
            InputStream is = this.getClass().getResourceAsStream(name);  // Имя ресурса
            BufferedReader br = new BufferedReader(new InputStreamReader(is, code_page));
            String line;
            while ((line = br.readLine()) !=null) {
                sb.append(line);  sb.append("\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return sb.toString();
    }
    
    /**
     * Поместить ресурс в байтовый массив
     * @param nameRes - название ресурса (относительно каталога пакета)
     * @return - байтовый массив
     */
    public ByteArrayOutputStream readResB(String nameRes)
    {
        try {
            // Get current classloader
            InputStream is = getClass().getResourceAsStream(nameRes);
            if(is == null) {
                System.out.println("Not found resource: " + nameRes);
                return null;
            }
            // https://habrahabr.ru/company/luxoft/blog/278233/ п.8
            BufferedInputStream bin = new BufferedInputStream(is);
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            int len;
            byte[] buf = new byte[512];
            while((len=bin.read(buf)) != -1) {
                bout.write(buf,0,len);
            }
            return bout;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Записать в файл текст из строки
     * @param strTxt    строка текста
     * @param fileName  имя файла
     * @return  true - файл записан, false - файл не записан
     */
    public boolean writeStr2File(String strTxt, String fileName)
    {
        File f = new File(fileName);
        try {
            // сформируем командный файл BAT
            PrintWriter out = new PrintWriter(f);
            out.write(strTxt);
            out.close();
        } catch(IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     *  Записать в файл ресурсный файл
     * @param nameRes   название ресурса
     * @param fileName  имя выходного файла
     * @return  true - файл записан, false - файл не записан
     */
    public boolean writeRes2File(String nameRes, String fileName)
    {
        boolean b = false;
        ByteArrayOutputStream buf = readResB(nameRes);
        if(buf != null) {
            try {
                FileOutputStream fout = new FileOutputStream(fileName);
                buf.writeTo(fout);
                fout.close();
                b = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return b;
    }

    /**
     * Пауза выполнения программы (потока)
     * @param msec - задержка, мсек
     */
    public static void Sleep(int msec)
    {
        try {
            Thread.sleep(msec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}  // end of class
