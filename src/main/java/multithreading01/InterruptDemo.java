package multithreading01;


class RunnableItem implements Runnable{
    public void run(){
        System.out.println("run th1");
//            Thread.sleep(1000000); //这个线程将被阻塞1000秒
    }
}
public class InterruptDemo {
    public static void main(String[] args) {
        Runnable tr=new RunnableItem();
        Thread th1=new Thread(tr);
        th1.start(); //开始执行分线程
        System.out.println("step 2");
        th1.interrupt();  //中断这个分线程
        System.out.println("step 3");
        try{
            th1.wait(1000);
            System.out.println("step 4");
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }
}
