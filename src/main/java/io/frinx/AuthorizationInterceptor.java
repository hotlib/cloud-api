package io.frinx;

import com.zaxxer.hikari.HikariDataSource;
import io.frinx.db.tables.Organizations;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.sql.SQLException;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.LoggerFactory;

public class AuthorizationInterceptor implements ServerInterceptor {
  private static final org.slf4j.Logger logger
      = LoggerFactory.getLogger(AuthorizationInterceptor.class);
  private final HikariDataSource hikariDataSource;

  public AuthorizationInterceptor(HikariDataSource ds) {
    this.hikariDataSource = ds;
  }

  private void throwException(String authString, String authStringValue) {
    String description =
        authStringValue == null ? authString + " not present, cannot authenticate!"
            : "invalid " + authString + ": " + authStringValue;
    Status status = Status.PERMISSION_DENIED.augmentDescription(description);
    logger.warn(description);
    throw new StatusRuntimeException(status);
  }

  public <ReqT, RespT> Listener<ReqT> interceptCall(final ServerCall<ReqT, RespT> serverCall,
      final Metadata metadata, final ServerCallHandler<ReqT, RespT> serverCallHandler) {

    try {
      final String authStringValue = getApiKey(metadata);
      if (checkIfApiKeyValid(authStringValue) == 0) {
        throwException(Constants.authString, authStringValue);
      }
    } catch (SQLException e) {
      String description = "Unable to verify " + Constants.authString
          + " error: " + e.getMessage();
      logger.error(description, e);

    }
    return serverCallHandler.startCall(serverCall, metadata);
  }

  private String getApiKey(Metadata metadata) {
    return metadata
        .get(Key.of(Constants.authString, Metadata.ASCII_STRING_MARSHALLER));
  }

  private Integer checkIfApiKeyValid(String authStringValue) throws SQLException {
    if (authStringValue == null) {
      return 0;
    }
    DSLContext context = DSL.using(hikariDataSource.getConnection(), SQLDialect.POSTGRES);
    return context.selectCount().from(Organizations.ORGANIZATIONS)
        .where(Organizations.ORGANIZATIONS.API_KEY.eq(authStringValue)).fetchOneInto(Integer.class);
  }
}