package io.frinx;

import com.google.protobuf.Empty;
import com.zaxxer.hikari.HikariDataSource;
import io.frinx.datareceiver.DataRequest;
import io.frinx.datareceiver.DataReceiverGrpc;
import io.frinx.db.tables.Devicedata;
import io.frinx.db.tables.records.DevicedataRecord;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.LoggerFactory;

public class DataReceiverServer {

    private static final org.slf4j.Logger logger
        = LoggerFactory.getLogger(DataReceiverServer.class);
    private Server server;

    private void start(HikariDataSource ds) throws IOException {
        server = ServerBuilder.forPort(Constants.PORT)
            .addService(new DataReceiverImpl(ds))
            .intercept(new AuthorizationInterceptor(ds))
            .build()
            .start();
        logger.info("Server started, listening on " + Constants.PORT);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                DataReceiverServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

   public static void startDataReceiver(HikariDataSource ds) throws IOException, InterruptedException {
       final DataReceiverServer server = new DataReceiverServer();
       server.start(ds);
       server.blockUntilShutdown();
   }

    static class DataReceiverImpl extends DataReceiverGrpc.DataReceiverImplBase {
        private final HikariDataSource hikariDataSource;

        public DataReceiverImpl(HikariDataSource ds) {
            this.hikariDataSource = ds;
        }

        @Override
        public void sendData(DataRequest req, StreamObserver<Empty> responseObserver) {
            responseObserver.onNext(Empty.getDefaultInstance());
            try  {
                DSLContext context = DSL.using(hikariDataSource.getConnection(), SQLDialect.POSTGRES);
                Result<Record1<Integer>> fetch = context.select(Devicedata.DEVICEDATA.ID)
                    .from(Devicedata.DEVICEDATA)
                    .where(Devicedata.DEVICEDATA.DEVICENAME.eq(req.getDeviceName())).fetch();

                DevicedataRecord record = new DevicedataRecord();
                record.setDevicename(req.getDeviceName());
                record.setDevicedata(JSONB.valueOf(req.getDeviceData()));

                if(fetch.isEmpty()) { //new entry
                    context.executeInsert(record);
                } else { //update entry
                    record.setId(fetch.get(0).value1());
                    context.executeUpdate(record);
                }

            } catch (Exception e) {
                logger.error("Unable to insert data to postgres", e);
            }
            responseObserver.onCompleted();
        }
    }
}
