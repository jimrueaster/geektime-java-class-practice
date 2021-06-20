package nio03.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public interface HttpRequestFilter {

    void filter(FullHttpRequest aFullHttpRequest, ChannelHandlerContext ctx);
}
