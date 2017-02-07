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

    public static String databaseName = "fps.db";   // имя базы данных
    public static String outputFile = tmpDir + "a.xlsx"; // выходное имя файла Excel

    public static double KT = 1800;                     // коэффициент трансформации

    public static String workDir = "C:\\TMP\\WORK";     // рабочий каталог (из-за replaceAll удваиваем \)
    public static String screenDir = "C:\\TMP\\SCR";     // корневой каталог храненения скриншотов сайтов
    ////public final static String template_workdir = "@dir@";     // шаблон рабочего каталога в файле (будет заменяться)" +

    //

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
     * Поместить ресурс в байтовый массив
     * @param nameRes - название ресурса (относительно каталога пакета)
     * @return - байтовый массив
     */
    public ByteArrayOutputStream readResB(String nameRes)
    {
        String str = null;
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
