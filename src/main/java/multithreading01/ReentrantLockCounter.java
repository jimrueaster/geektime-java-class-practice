package multithreading01;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class ReentrantLockCounter {

    private int sum = 0;

    private Lock reentrantLock = new ReentrantLock(true);

    public static void main(String[] args) {

        var loopNum = 100_0000;
        var counter = new ReentrantLockCounter();
        IntStream.range(0, loopNum).parallel().forEach(i -> counter.addAndGet());

        System.out.println(counter.getSum());
    }

    public int addAndGet() {
        try {
            reentrantLock.lock();
            return ++sum;
        } finally {
            reentrantLock.unlock();
        }
    }

    public int getSum() {
        return sum;
    }
}
