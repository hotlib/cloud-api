package io.frinx;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.frinx.db.tables.Devicedata;
import io.frinx.db.tables.records.DevicedataRecord;
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
    public static void main( String[] args ) {
        HikariConfig config = new HikariConfig("/home/palo/work/projects/cloud-api/src/main/resources/hikari.properties");
        HikariDataSource ds = new HikariDataSource(config);
        try  {
            DSLContext context = DSL.using(ds.getConnection(), SQLDialect.POSTGRES);
            DevicedataRecord record = new DevicedataRecord();
            JSONB test = JSONB.valueOf("{\"bbb\":\"12\"}");
            record.setDevicename(test);
            record.setDevicedata(test);
            context.executeInsert(record);
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
