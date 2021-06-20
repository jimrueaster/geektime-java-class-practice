package nio03.filter;

import io.netty.handler.codec.http.FullHttpResponse;

public class HeaderHttpResponseFilter implements HttpResponseFilter {
    @Override
    public void filter(FullHttpResponse aResponse) {
        aResponse.headers().set("kk", "java-1-nio");
    }
}
