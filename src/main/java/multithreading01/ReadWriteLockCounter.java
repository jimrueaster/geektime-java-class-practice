package multithreading01;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.IntStream;

public class ReadWriteLockCounter {

    private int sum = 0;

    private ReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock(true);

    public static void main(String[] args) {

        var loopNum = 100_0000;
        var counter = new ReadWriteLockCounter();
        IntStream.range(0, loopNum).parallel().forEach(i -> counter.addAndGet());

        System.out.println(counter.getSum());
    }

    public int addAndGet() {
        try {
            reentrantReadWriteLock.writeLock().lock();
            return ++sum;
        } finally {
            reentrantReadWriteLock.writeLock().unlock();
        }
    }

    public int getSum() {
        try {
            reentrantReadWriteLock.readLock().lock();
            return sum;
        } finally {
            reentrantReadWriteLock.readLock().unlock();
        }
    }
}
