package com.jp.xgpush;

import com.jp.xgpush.action.UpdateController;
import com.jp.xgpush.dao.DBDao;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;

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


    /**
     * 新增一个连接器 将8080的http请求转发到8895端口的tomcat的https请求中
     * @return
     */
    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(createStandardConnector());
        return tomcat;
    }

    private Connector createStandardConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(8895);
        return connector;
    }
}
