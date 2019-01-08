package com.jp.xgpush;

import com.jp.xgpush.action.UpdateController;
import com.jp.xgpush.dao.DBDao;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.InputStream;
import java.util.Properties;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class App
{
    /**
     * 获取GPS信息的消息标志位,需要和服务端App.class的标志位一致
     */
    public static final String GPS_FLAG = "41701b96a69f402c9d23d6b917bcc6a7";

    public static void main( String[] args )
    {
        //加载属性文件
        loadProperties();
        //加载Derby数据库
        DBDao.loadDriver();
        //创建业务表
        DBDao.createTable();

        SpringApplication.run(App.class,args);

    }

    public static Properties prop;

    public static void loadProperties() {
        InputStream inputStream = UpdateController.class.getClassLoader().getResourceAsStream("update.properties");
        System.out.println(UpdateController.class.getClassLoader().getResource(""));
        prop = new Properties();
        try {
            prop.load(inputStream);
            System.out.println("Loaded the properties !!!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
