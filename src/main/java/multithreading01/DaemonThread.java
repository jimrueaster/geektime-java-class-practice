package multithreading01;

public class DaemonThread {

    public static void main(String[] args) {
        var task = new Runnable(){

            @Override
            public void run() {
                try{
                    Thread.sleep(2500);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                var t = Thread.currentThread();
                System.out.println("current thread:" + t.getName());
            }
        };

        var thread = new Thread(task);
        thread.setName("t-t-1");
        thread.setDaemon(true);
        thread.start();
    }
}
