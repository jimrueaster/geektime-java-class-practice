package multithreading01;

public class WaitAndNotify {
    public static void main(String[] args) {
        MethodClass methodClass = new MethodClass();
        Thread t1 = new Thread(() -> {
            try {
                methodClass.product();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            try {
                methodClass.customer();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2");

        t1.start();
        t2.start();

    }
}

class MethodClass {
    // 定义生产最大量
    private final int MAX_COUNT = 20;
    
    int productCount = 0;
    
    public synchronized void product() throws InterruptedException {
        while (true) {
            System.out.println(Thread.currentThread().getName() + ":::produce:::" + productCount);
            Thread.sleep(10);
            if (productCount >= MAX_COUNT) {
                System.out.println("货舱已满,,.不必再生产");
                System.out.println("produce notifyAll");
                notifyAll();
                wait();
            }else {
                productCount++;
            }
        }
    }
    
    public synchronized void customer() throws InterruptedException {
        while (true) {
            System.out.println(Thread.currentThread().getName() + ":::consume:::" + productCount);
            Thread.sleep(10);
            if (productCount <= 0) {
                System.out.println("货舱已无货...无法消费");
                System.out.println("consume notifyAll");
                notifyAll();
                wait();
            }else {
                productCount--;
            }
        }
    }
}