package io.frinx;

import io.grpc.CallOptions;
    import io.grpc.Channel;
    import io.grpc.ClientCall;
    import io.grpc.ClientInterceptor;
    import io.grpc.ForwardingClientCall;
    import io.grpc.Metadata;
    import io.grpc.Metadata.Key;
    import io.grpc.MethodDescriptor;

public class AppendApiKeyInterceptor implements ClientInterceptor {

  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(final MethodDescriptor<ReqT, RespT> methodDescriptor, final CallOptions callOptions, final Channel channel) {
    return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(channel.newCall(methodDescriptor, callOptions)) {
      @Override
      public void start(final Listener<RespT> responseListener, final Metadata headers) {
        headers.put(Key.of(Constants.authString, Metadata.ASCII_STRING_MARSHALLER), Constants.demoApiKey);
        super.start(responseListener, headers);
      }
    };
  }
}