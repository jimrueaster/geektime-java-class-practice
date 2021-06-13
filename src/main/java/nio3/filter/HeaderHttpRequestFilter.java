package nio3.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public class HeaderHttpRequestFilter implements HttpRequestFilter {
    @Override
    public void filter(FullHttpRequest aFullHttpRequest, ChannelHandlerContext ctx) {
        aFullHttpRequest.headers().set("mao", "soul");
    }
}
