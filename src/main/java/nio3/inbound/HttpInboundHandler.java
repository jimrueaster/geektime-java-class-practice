package nio3.inbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;
import nio3.filter.HeaderHttpRequestFilter;
import nio3.filter.HttpRequestFilter;
import nio3.outbound.HttpOutboundHandler;

import java.util.List;

public class HttpInboundHandler extends ChannelInboundHandlerAdapter {

    private final List<String> proxyServer;

    private final HttpOutboundHandler handler;

    private final HttpRequestFilter filter = new HeaderHttpRequestFilter();

    public HttpInboundHandler(List<String> aProxyServer) {
        proxyServer = aProxyServer;
        handler = new HttpOutboundHandler(proxyServer);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            var fullRequest = (FullHttpRequest) msg;

            handler.handle(fullRequest, ctx, filter);
        } catch (Exception aException) {
            aException.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
