package io.frinx;

import com.zaxxer.hikari.HikariDataSource;
import io.frinx.db.tables.Devicedata;
import io.frinx.db.tables.Organizations;
import io.frinx.db.tables.records.DevicedataRecord;
import java.sql.SQLException;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.LoggerFactory;

public class DbAccess {
  private static final org.slf4j.Logger logger
      = LoggerFactory.getLogger(DbAccess.class);

  private final DSLContext context;

  public DbAccess(HikariDataSource hikariDataSource) {
    context = DSL.using(hikariDataSource, SQLDialect.POSTGRES);
  }

  public void storeDeviceData(String deviceName, String deviceData){
    try  {
      Result<Record1<Integer>> fetch = context.select(Devicedata.DEVICEDATA.ID)
          .from(Devicedata.DEVICEDATA)
          .where(Devicedata.DEVICEDATA.DEVICENAME.eq(deviceName)).fetch();

      DevicedataRecord record = new DevicedataRecord();
      record.setDevicename(deviceName);
      record.setDevicedata(JSONB.valueOf(deviceData));

      if(fetch.isEmpty()) { //new entry
        context.executeInsert(record);
      } else { //update entry
        record.setId(fetch.get(0).value1());
        context.executeUpdate(record);
      }
    } catch (Exception e) {
      logger.error("Unable to insert data to postgres", e);
    }
  }

  public boolean isApiKeyValid(String authStringValue) throws SQLException {
    if (authStringValue == null) {
      return false;
    }
    Integer id = context.selectCount().from(Organizations.ORGANIZATIONS)
        .where(Organizations.ORGANIZATIONS.API_KEY.eq(authStringValue)).fetchOneInto(Integer.class);
    return id != 0;
  }
}
