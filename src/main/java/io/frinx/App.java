package io.frinx;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class App
{
    public static void main( String[] args ) throws IOException, InterruptedException {

        Properties properties = new Properties();
        InputStream stream = null;
        try {
            stream = App.class.getClassLoader().getResourceAsStream("hikari.properties");
            properties.load(stream);
        } finally {
            if(stream != null){
                stream.close();
            }
        }

        if(properties.isEmpty()){
            System.err.println("property file not found");
            return;
        }

        HikariConfig config = new HikariConfig(properties);
        HikariDataSource ds = new HikariDataSource(config);
        DataReceiverServer.startDataReceiver(ds);

    }
}
