package multithreading01.future;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;

public class FutureTaskThreadPoolVersion {

    public static void main(String[] args) {
        var futureTask = new FutureTask<Integer>(()->{
            var rnd = new Random();
            var result = rnd.nextInt();

            Thread.sleep(1500);
            return result;
        });

        var threadPool = Executors.newSingleThreadExecutor();
        threadPool.submit(futureTask);

        try{
            System.out.format("result: %d", futureTask.get());
        } catch (ExecutionException | InterruptedException aE) {
            aE.printStackTrace();
        }
        threadPool.shutdown();
    }
}
