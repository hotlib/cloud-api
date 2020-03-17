package io.frinx;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.flywaydb.core.Flyway;

public class App {

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
      stream = App.class.getClassLoader().getResourceAsStream("hikari.properties");
      properties.load(stream);
    } finally {
      if (stream != null) {
        stream.close();
      }
    }

    if (properties.isEmpty()) {
      System.err.println("property file not found");
      return;
    }

    HikariConfig config = new HikariConfig(properties);
    HikariDataSource ds = new HikariDataSource(config);

    if (parser.isCreateDatabase()) {
        Flyway flyway = Flyway.configure().dataSource(ds).load();
        flyway.migrate();
    } else {
        DataReceiverServer.startDataReceiver(ds);
    }

  }
}
