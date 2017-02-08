/*
 * Copyright (c) 2017. Aleksey Eremin
 * 28.01.17 18:06
 */

package ae;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by ae on 28.01.2017.
 * Базовый класс для работы с БД.
 */
public class Database
{
    protected Connection f_connection = null;
    private Statement  f_statement  = null;
    
    /**
     * Возвращает соединение с БД
     * @return - соединение с БД
     */
    public synchronized Connection getDbConnection()
    {
        return f_connection;
    }

    /**
     * Возвращает запрос к БД
     * про synchronized - http://java-course.ru/begin/multithread_02/
     * @return запрос
     */
    public synchronized Statement getDbStatement()
    {
        if(f_statement == null) {
            Connection conn = getDbConnection();
            if(conn != null) {
                try {
                    f_statement = conn.createStatement();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return f_statement;
    }

    /**
     * Выполнить оператор SQL
     * @param sql   SQL выражение
     * @return      возвращает кол-во обработанных строк
     */
    public int ExecSql(String sql)
    {
        int a = 0;
        Statement stm = getDbStatement();
        if(stm != null) {
            try {
                stm.execute(sql);
                a = stm.getUpdateCount();
            } catch (SQLException e) {
                //e.printStackTrace();
            }
        }
        return a;
    }
    
    /**
     *  Возвращает значение первого столбца, первой строки указанного запроса
     *  (аналогично Dlookup в MS Access
     * @param strSql - строка SQL запроса
     * @return - значение 1 столбца 1-ой строки запроса
     */
    public String Dlookup(String strSql)
    {
        String result = null;
        Statement stm = getDbStatement();
        if(stm != null) {
            try {
                ResultSet rst = stm.executeQuery(strSql);
                if(rst.next()) {
                  result = rst.getString(1);  // взять первый столбец
                }
                rst.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
  
    /**
     * Возвращает коллекцию (список) 1 колонки всех строк запроса
     * @param strSql - SQL запрос
     * @return список значений 1 колонки запроса
     */
    public ArrayList<String> DlookupArray(String strSql)
    {
        ArrayList<String>  result = null;
        Statement stm = getDbStatement();
        if(stm != null) {
            try {
                ResultSet rst = stm.executeQuery(strSql);
                result = new ArrayList<>();
                while (rst.next()) {
                    String str = rst.getString(1);  // взять первый столбец
                    result.add(str);
                }
                rst.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
  
  
    /**
      * Закрыть все ресурсы к БД
      */
    public synchronized void close()
    {
        if(f_statement != null) {
            try {
                f_statement.close();
                f_statement=null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(f_connection != null) {
            try {
                f_connection.close();
                f_connection=null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
    
} // end of class
