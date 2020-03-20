package io.frinx;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

  private static final Logger logger
      = LoggerFactory.getLogger(App.class);
  public static final String HIKARI_PROPERTIES = "hikari.properties";

  private static CommandlineParser parseCommandLineArgs(String[] args) {
    CommandlineParser parser = new CommandlineParser();
    parser.parseArguments(args);
    return parser;
  }

  public static void main(String[] args) throws IOException, InterruptedException {

    CommandlineParser parser = parseCommandLineArgs(args);
    if (!parser.isArgumentsCorrect()) {
      return;
    }

    Properties properties = new Properties();
    InputStream stream = null;
    try {
      stream = App.class.getClassLoader().getResourceAsStream(HIKARI_PROPERTIES);
      properties.load(stream);
    } finally {
      if (stream != null) {
        stream.close();
      }
    }

    if (properties.isEmpty()) {
      logger.error(HIKARI_PROPERTIES + " not found, terminating");
      return;
    }

    HikariConfig config = new HikariConfig(properties);
    HikariDataSource ds = new HikariDataSource(config);
    DbAccess dbAccess = new DbAccess(ds);

    if (parser.isCreateDatabase()) {
        Flyway flyway = Flyway.configure().dataSource(ds).load();
        flyway.migrate();
    } else {
        DataReceiverServer.startDataReceiver(dbAccess);
    }
  }
}
