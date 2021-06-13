package nio01;

import java.io.IOException;
import java.net.Socket;

public class Util {
    public static void printRequest(Socket aSocket) throws IOException, InterruptedException {
        System.out.println("Request data:");
        var socketIn = aSocket.getInputStream();
        Thread.sleep(500);
        int size = socketIn.available();
        byte[] buffer = new byte[size];
        socketIn.read(buffer);
        String request = new String(buffer);
        System.out.println(request);
    }
}
