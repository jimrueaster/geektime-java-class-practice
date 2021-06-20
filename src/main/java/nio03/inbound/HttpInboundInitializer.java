package nio03.inbound;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

import java.util.List;

public class HttpInboundInitializer extends ChannelInitializer<SocketChannel> {

    private final List<String> proxyServer;

    public HttpInboundInitializer(List<String> aProxyServer) {
        System.out.println("construct http inbound initializer");
        proxyServer = aProxyServer;
    }

    @Override
    protected void initChannel(SocketChannel aSocketChannel) throws Exception {
        var p = aSocketChannel.pipeline();
        System.out.println("http server codec");
        // HTTP 编解码器
        p.addLast(new HttpServerCodec());
        System.out.println("http object aggregator");
        // 粘包拆包
        p.addLast(new HttpObjectAggregator(1024 * 1024));
        System.out.println("http inbound handler");
        p.addLast(new HttpInboundHandler(proxyServer));
    }
}
