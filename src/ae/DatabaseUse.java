/*
 * Copyright (c) 2017. Aleksey Eremin
 * 03.02.17 9:21
 */

package ae;

import java.io.File;
import java.sql.Statement;

/**
 * Created by ae on 03.02.2017.
 * База данных, используемая в проекте
 * наследуется от какой-то конкретной Database*** и используется в вызовах,
 * чтобы не переделывать код
 * В этом проекте это временная база данных, создается во временном каталоге fps.db
 * Таблица rep
 * CREATE TABLE [rep](year INT, mon INT, day INT, hh INT, mm INT, Delta INT, V1 DOUBLE, V2 DOUBLE, H INT);
 *
 */
public class DatabaseUse extends DatabaseSqlite {

    /**
     *  Конструктор - создает БД во временном каталоге с заданным именем
     */
    public DatabaseUse()
    {
        R r = new R();
        String  dbn = R.tmpDir + R.sep + R.databaseName;
        // TODO: сделать временное имя (потом)
        if(!r.writeRes2File("res/fps.db", dbn)) {
            System.out.println("?ERROR-can't create temporary database: " + dbn);
            System.exit(3); // прервать выполнение программы с кодом 3
        }
        //
        this.f_databaseName = dbn;
        Statement stm = this.getDbStatement();
        if(stm == null) {
            System.out.println("?-ERROR-can't open temporary database: " + dbn);
            System.exit(4); // прервать выполнение программы с кодом 4
        }
    }

    public String getDatabaseName()
    {
        return this.f_databaseName;
    }

    public void deleteDatabase()
    {
        if(f_databaseName != null) {
            this.close();
            File f = new File(this.f_databaseName);
            f.delete();
        }
    }

}
