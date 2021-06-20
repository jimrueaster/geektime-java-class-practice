package nio03.inbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;
import nio03.filter.HeaderHttpRequestFilter;
import nio03.filter.HttpRequestFilter;
import nio03.outbound.HttpOutboundHandler;

import java.util.List;

public class HttpInboundHandler extends ChannelInboundHandlerAdapter {

    private final List<String> proxyServer;

    private final HttpOutboundHandler outboundHandler;

    private final HttpRequestFilter filter = new HeaderHttpRequestFilter();

    public HttpInboundHandler(List<String> aProxyServer) {
        System.out.println("construct http inbound handler");
        proxyServer = aProxyServer;
        outboundHandler = new HttpOutboundHandler(proxyServer);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel read complete");
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            var fullRequest = (FullHttpRequest) msg;

            System.out.println("outbound handler");
            outboundHandler.handle(fullRequest, ctx, filter);
        } catch (Exception aException) {
            aException.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        var channel = ctx.channel();
        if(channel.isActive()){

            ctx.close();
        }
    }
}
