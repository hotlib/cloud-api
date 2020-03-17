package io.frinx;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class CommandlineParser {
  @Option(name = "-c", usage = "create database schema and tables")
  private boolean createDatabase;

  private boolean argumentsCorrect = true;

  public void parseArguments(String[] args) {
    CmdLineParser parser = new CmdLineParser(this);
    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      argumentsCorrect = false;
      parser.printUsage(System.err);
    }
  }

  public boolean isCreateDatabase() {
    return createDatabase;
  }

  public void setCreateDatabase(boolean createDatabase) {
    this.createDatabase = createDatabase;
  }

  public boolean isArgumentsCorrect() {
    return argumentsCorrect;
  }
}

