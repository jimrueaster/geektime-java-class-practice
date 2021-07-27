package multithreading01;

import java.text.MessageFormat;
import java.util.concurrent.Semaphore;

public class SemaphoreDemo {

    public static void main(String[] args) {

        var semaphore = new Semaphore(3, true);
        var totalWorkers = 10;
        for (var i = 0; i < totalWorkers; i++) {
            (new Worker(i, semaphore)).start();
        }
    }
}

class Worker extends Thread {
    private final int num;
    private final Semaphore semaphore;

    public Worker(int aNum, Semaphore aSemaphore) {
        num = aNum;
        semaphore = aSemaphore;
    }

    @Override
    public void run() {
        try {
            semaphore.acquire();
            System.out.println(MessageFormat.format("Worker {0} is working on ONE machine", num));
            Thread.sleep(1000);
            System.out.println(MessageFormat.format("Worker {0} release the machine", num));
            semaphore.release();
        } catch (InterruptedException aE) {
            aE.printStackTrace();
        }
    }
}