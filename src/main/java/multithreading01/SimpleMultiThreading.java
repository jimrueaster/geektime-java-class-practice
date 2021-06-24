package multithreading01;

public class SimpleMultiThreading {

    public static void main(String[] args) {

        System.out.println("start");

        new SleepAWhile().start();

        System.out.println("main thread");

        System.out.println("end");
    }
}

class SleepAWhile extends Thread {
    @Override
    public void run() {
        try {
            var sleepTime = 1000;
            Thread.sleep(sleepTime);
            System.out.println("sub thread: print after sleeping " + sleepTime + "ms");
        } catch (InterruptedException aE) {
            aE.printStackTrace();
        }
    }
}
