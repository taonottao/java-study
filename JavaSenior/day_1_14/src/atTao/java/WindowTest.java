package atTao.java;

/**
 *
 * 例子：创建3个窗口卖票，总票数为100张,使用继承Thread类的方式
 *
 * 存在线程安全问题，待解决。
 *
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/1/15 23:57
 */
class Window extends  Thread{

    private static int ticket = 100;

    @Override
    public void run() {

        while(true){

            if(ticket > 0){
                System.out.println(getName() + ":卖票，票号为：" + ticket);
                ticket--;
            }else{
                break;
            }

        }

    }
}



public class WindowTest {
    public static void main(String[] args) {
        Window t1 = new Window();
        Window t2 = new Window();
        Window t3 = new Window();

        t1.setName("窗口一");
        t2.setName("窗口二");
        t3.setName("窗口三");

        t1.start();
        t2.start();
        t3.start();

    }
}
