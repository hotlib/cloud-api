package io.frinx;

import io.frinx.datareceiver.DataReceiverGrpc;
import io.frinx.datareceiver.DataRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple client that requests a greeting from the {@link DataReceiverServer}.
 */
public class DataReceiverClient {
    private static final Logger logger = Logger.getLogger(DataReceiverClient.class.getName());

    private final ManagedChannel channel;
    private final DataReceiverGrpc.DataReceiverBlockingStub blockingStub;

    /** Construct client connecting to HelloWorld server at {@code host:port}. */
    public DataReceiverClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
            // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
            // needing certificates.
            .usePlaintext()
            .build());
    }

    /** Construct client for accessing HelloWorld server using the existing channel. */
    DataReceiverClient(ManagedChannel channel) {
        this.channel = channel;
        blockingStub = DataReceiverGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    /** Say hello to server. */
    public void sendData() {
        DataRequest request = DataRequest.newBuilder().setDeviceData("{\"bbb\":\"1112\"}").setDeviceName("{\"bbb\":\"11112\"}").build();
        try {
            blockingStub.sendData(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
    }

    /**
     * Greet server. If provided, the first element of {@code args} is the name to use in the
     * greeting.
     */
    public static void main(String[] args) throws Exception {
        // Access a service running on the local machine on port 50051
        DataReceiverClient client = new DataReceiverClient("localhost", 50051);
        try {
            client.sendData();
        } finally {
            client.shutdown();
        }
    }
}
