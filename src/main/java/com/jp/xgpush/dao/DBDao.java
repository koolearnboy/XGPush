package com.jp.xgpush.dao;

import com.jp.xgpush.entity.TokenEntity;
import com.jp.xgpush.entity.VersionInfo;
import org.springframework.util.StringUtils;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

public class DBDao {
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static String driver = "org.apache.derby.jdbc.EmbeddedDriver";
//    private static String driver = "org.apache.derby.jdbc.ClientDriver";
    /**
     * 在工程目录下创建数据库
     */
    private static String protocol = "jdbc:derby:C:/db_xg;create=true";
    //    private static String protocol = "jdbc:derby://localhost:1527/db_xg;create=true";
    private static String tokenTableName = "T_TOKEN_LIST";
    private static String versionTableName = "T_VERSION_PROP";

    /**
     * 加载数据库驱动
     */
    public static void loadDriver() {
        try {
            Class.forName(driver).newInstance();
            System.out.println("Loaded the appropriate driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接
     *
     * @return
     */
    public static Connection getDBConnection() {
        try {
            return DriverManager.getConnection(protocol);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 创建本服务所需要的表
     *
     * @return
     */
    public static boolean createTable() {
        try (Connection conn = getDBConnection();
             Statement stmt = conn.createStatement()) {
            DatabaseMetaData metaData = conn.getMetaData();
            //查询所有的表
            ResultSet ret = metaData.getTables(null, null, null, new String[]{"TABLE"});
            HashSet<String> tableSet = new HashSet<>();
            while (ret.next()) {
                tableSet.add(ret.getString("TABLE_NAME"));
            }
            //判断表是否存在,存在则不创建,不存在则创建
            if (!tableSet.contains(tokenTableName.toUpperCase())) {
                stmt.executeUpdate(" create table " + tokenTableName + "( " +
                        " ID int generated always as identity constraint T_TOKEN_LIST_pk primary key," +
                        " TOKEN VARCHAR(100) not null," +
                        " IEMI VARCHAR(30) not null," +
                        " PROJECT VARCHAR(10)," +
                        " ADD_TIME timestamp" +
                        ")");
            }
            if (!tableSet.contains(versionTableName.toUpperCase())) {
                stmt.executeUpdate(" create table " + versionTableName + "( " +
                        " ID int generated always as identity constraint T_VERSION_PROP_pk primary key," +
                        " versionCode int," +
                        " versionName varchar(10)," +
                        " apkURL varchar(200)," +
                        " PROJECT VARCHAR(10)," +
                        " ADD_TIME timestamp" +
                        ")");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 获取IEMI对于的信鸽token
     *
     * @param iemi
     * @return
     */
    public static String queryToken(String iemi) {
        try (Connection conn = getDBConnection();
             Statement statement = conn.createStatement()) {
            ResultSet set = statement.executeQuery("select * from APP.T_TOKEN_LIST t where t.IEMI = '" + iemi + "'");
            while (set.next()) {
                return set.getString("TOKEN");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 向T_TOKEN_LIST表中插入数据
     *
     * @param token
     */
    public static void addToken(TokenEntity token) {
        try (Connection conn = getDBConnection();
             Statement statement = conn.createStatement()) {
            //先查询是否已经存在数据
            String oldToken = queryToken(token.getIemi());
            if (!StringUtils.isEmpty(oldToken)) {
                String sql = "UPDATE APP.t_token_list t SET t.TOKEN = '" + token.getToken() + "'"
                        + ", t.ADD_TIME = '" + format.format(token.getAddTime()) + "'"
                        + " WHERE t.IEMI = '" + token.getIemi() + "'";
                statement.executeUpdate(sql);
            } else {
                String sql = "insert into t_token_list(token,iemi,project,ADD_TIME) " +
                        "values('" + token.getToken() + "','" + token.getIemi()
                        + "','" + token.getProject() + "','" + format.format(token.getAddTime()) + "')";
                statement.addBatch(sql);
                statement.executeBatch();
            }
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询apk最新的版本信息
     *
     * @param project apk所属项目名称
     * @return
     */
    public static VersionInfo queryLastestVersion(String project) {
        try (Connection conn = getDBConnection();
             Statement statement = conn.createStatement()) {
            ResultSet set = statement.executeQuery("select * from APP.T_VERSION_PROP t where t.PROJECT = '" + project + "'");
            while (set.next()) {
                VersionInfo info = new VersionInfo()
                        .setVersionCode(set.getInt("versionCode"))
                        .setVersionName(set.getString("versionName"))
                        .setApkURL(set.getString("apkURL"));
                return info;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 更新apk最新的版本信息
     *
     * @param info
     * @return
     */
    public static boolean updateVersionInfo(VersionInfo info) {
        try (Connection conn = getDBConnection();
             Statement statement = conn.createStatement()) {
            //先查询是否已经存在数据
            VersionInfo oldVersion = queryLastestVersion(info.getProject());
            if (oldVersion != null) {
                String sql = "UPDATE APP.T_VERSION_PROP t SET t.versioncode = " + info.getVersionCode()
                        + ", t.versionName = '" + info.getVersionName() + "'";
                if (!StringUtils.isEmpty(info.getApkURL())) {
                    sql += ", t.apkURL = '" + info.getApkURL() + "'";
                }
                sql += ", t.add_time = '" + format.format(new Date()) + "'"
                        + " WHERE t.project = '" + info.getProject() + "'";
                statement.executeUpdate(sql);
            } else {
                String sql = "insert into APP.T_VERSION_PROP(versioncode, versionname, apkurl, project, add_time) " +
                        "values(" + info.getVersionCode() + ",'" + info.getVersionName()
                        + "','" + info.getApkURL() + "','" + info.getProject()+ "','" + format.format(new Date()) + "')";
                statement.addBatch(sql);
                statement.executeBatch();
            }
            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void main(String[] args) {
        try {
            loadDriver();
            createTable();
            Connection conn = getDBConnection();
            Statement stmt = conn.createStatement();
            TokenEntity token = TokenEntity.of("123", "ABCSWEQR");
            addToken(token);
            System.out.println(queryToken("ABCSWEQR"));

            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
