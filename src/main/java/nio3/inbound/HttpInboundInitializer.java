package nio3.inbound;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

import java.util.List;

public class HttpInboundInitializer extends ChannelInitializer<SocketChannel> {

    private final List<String> proxyServer;

    public HttpInboundInitializer(List<String> aProxyServer) {
        proxyServer = aProxyServer;
    }

    @Override
    protected void initChannel(SocketChannel aSocketChannel) throws Exception {
        var p = aSocketChannel.pipeline();
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpObjectAggregator(1024 * 1024));
        p.addLast(new HttpInboundHandler(proxyServer));
    }
}
