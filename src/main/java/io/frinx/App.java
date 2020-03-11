package io.frinx;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.frinx.db.tables.Devicedata;
import io.frinx.db.tables.records.DevicedataRecord;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, InterruptedException {
        HikariConfig config = new HikariConfig("/home/palo/work/projects/cloud-api/src/main/resources/hikari.properties");
        HikariDataSource ds = new HikariDataSource(config);
        DataReceiverServer.startDataReceiver(ds);

    }
}
