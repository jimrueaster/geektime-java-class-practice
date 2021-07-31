package multithreading01.future;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class FutureTaskThreadVersion {

    public static void main(String[] args) {
        var futureTask = new FutureTask<Integer>(()->{
            var rnd = new Random();
            var result = rnd.nextInt();
            return result;
        });

        new Thread(futureTask).start();

        try{
            System.out.format("result: %d", futureTask.get());
        } catch (ExecutionException | InterruptedException aE) {
            aE.printStackTrace();
        }
    }
}
