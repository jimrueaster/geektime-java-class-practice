package nio3.outbound;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import nio3.filter.HeaderHttpResponseFilter;
import nio3.filter.HttpRequestFilter;
import nio3.filter.HttpResponseFilter;
import nio3.router.HttpEndpointRouter;
import nio3.router.RandomHttpEndpointRouter;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class HttpOutboundHandler {

    private final CloseableHttpAsyncClient httpAsyncClient;
    private final ExecutorService proxyService;
    private final List<String> backendUrls;
    HttpResponseFilter responseFilter = new HeaderHttpResponseFilter();
    HttpEndpointRouter router = new RandomHttpEndpointRouter();

    public HttpOutboundHandler(List<String> aBackendUrls) {
        System.out.println("http outbound handler");
        backendUrls = aBackendUrls.stream().map(this::formatUrl)
                .collect(Collectors.toList());

        int cores = Runtime.getRuntime().availableProcessors();
        long keepAliveTime = 1000;
        int queueSize = 2048;
        var handler = new ThreadPoolExecutor.CallerRunsPolicy();
        proxyService = new ThreadPoolExecutor(cores, cores,
                keepAliveTime, TimeUnit.MICROSECONDS, new ArrayBlockingQueue<>(queueSize),
                new NamedThreadFactory("proxyService"), handler);

        var ioConfig = IOReactorConfig.custom()
                .setConnectTimeout(1000)
                .setSoTimeout(1000)
                .setIoThreadCount(cores)
                .setRcvBufSize(32 * 1024)
                .build();

        httpAsyncClient = HttpAsyncClients.custom().setMaxConnTotal(40)
                .setMaxConnPerRoute(8)
                .setDefaultIOReactorConfig(ioConfig)
                .setKeepAliveStrategy(((aHttpResponse, aHttpContext) -> 6000))
                .build();

        httpAsyncClient.start();
    }

    private String formatUrl(String backend) {
        return backend.endsWith("/") ? backend.substring(0, backend.length() - 1) : backend;
    }

    public void handle(final FullHttpRequest aFullHttpRequest, final ChannelHandlerContext ctx,
                       HttpRequestFilter aFilter) {
        System.out.println("handle outbound start");

        System.out.println("get a route url");
        var backendUrl = router.route(this.backendUrls);

        System.out.println("full request uri:" + aFullHttpRequest.uri());
        final String url = backendUrl + aFullHttpRequest.uri();

        System.out.println("request filter, will header will add 'mao'");
        aFilter.filter(aFullHttpRequest, ctx);

        System.out.println("fetch Get");
        proxyService.submit(() -> fetchGet(aFullHttpRequest, ctx, url));
    }

    private void fetchGet(final FullHttpRequest inbound, final ChannelHandlerContext ctx,
                          final String url) {
        final HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);

        String headerMaoField = inbound.headers().get("mao");
        httpGet.setHeader("mao", headerMaoField);

        httpAsyncClient.execute(httpGet, new FutureCallback<>() {
            @Override
            public void completed(HttpResponse endpointResponse) {
                System.out.println("fetch Get complete");
                try {
                    handleResponse(inbound, ctx, endpointResponse);
                } catch (Exception aException) {
                    aException.printStackTrace();
                } finally {

                }
            }

            @Override
            public void failed(Exception aE) {
                System.out.println("fetch Get failed");
                httpGet.abort();
                aE.printStackTrace();
            }

            @Override
            public void cancelled() {
                System.out.println("fetch Get cancelled");
                httpGet.abort();
            }
        });
    }

    private void handleResponse(final FullHttpRequest aFullHttpRequest, final ChannelHandlerContext ctx,
                                final HttpResponse endpointResponse) {
        FullHttpResponse response = null;

        try {
            byte[] body = EntityUtils.toByteArray(endpointResponse.getEntity());
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(body));

            response.headers().set("Content-Type", "application/json");
            response.headers().setInt("Content-Length",
                    Integer.parseInt(endpointResponse.getFirstHeader("Content-Length").getValue()));

            responseFilter.filter(response);
        } catch (IOException aException) {
            aException.printStackTrace();

            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
            exceptionCaught(ctx, aException);
        } finally {
            if (aFullHttpRequest != null) {
                if (!HttpUtil.isKeepAlive(aFullHttpRequest)) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    ctx.write(response);
                }
            }
            ctx.flush();
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
