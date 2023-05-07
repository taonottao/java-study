package ThreadStudy;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/7 20:47
 */
class MyBlockingQueue1{
    private int[] items = new int[1000];
    private int head = 0;
    private int tail = 0;
    private int size = 0;

    public void put(int value) throws InterruptedException {
        synchronized (this) {
            while (size == items.length){
                this.wait();
            }
            items[tail++] = value;
            if(tail >= items.length){
                tail = 0;
            }
            size++;
            this.notify();
        }

    }

    public Integer take() throws InterruptedException {
        Integer ret;
        synchronized (this) {
            if(size == 0){
                this.wait();
            }
            ret = items[head++];
            if(head >= items.length){
                head = 0;
            }
            size--;
            this.notify();
        }
        return ret;
    }
}

public class Demo25 {
}
