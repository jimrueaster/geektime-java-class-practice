package nio01;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer02 {

    public static void main(String[] args) throws IOException {
        var serverSocket = new ServerSocket(8082);
        while (true) {
            try {
                System.out.println("before accept");
                var socket = serverSocket.accept();
                System.out.println("after accept");
                new Thread(
                        () -> service(socket)
                ).start();
                System.out.println("after service socket");
            } catch (IOException aException) {
                aException.printStackTrace();
            }
        }
    }

    private static void service(Socket aSocket) {
        try {
            Thread.sleep(20);
            var printWriter = new PrintWriter(aSocket.getOutputStream(), true);
            printWriter.println("HTTP/1.1 200 OK");
            printWriter.println("Content-Type:text/html;charset=utf-8");
            String body = "<h2>hello world</h2>";
            printWriter.println("Content-Length: " + body.getBytes().length);
            printWriter.println();
            printWriter.write(body);
            printWriter.close();
            aSocket.close();
        } catch (IOException | InterruptedException aException) {
            aException.printStackTrace();
        }
    }
}
