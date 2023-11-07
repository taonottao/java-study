package demo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 用来创建一个代理
 */
public class ProxyUtil {

    /**
     * 给一个明星创建一个代理
     *
     * @param bigStar，被代理的明星
     * @return 代理
     */
    public static Star createProxy(BigStar bigStar) {
        /*
        java.lang.reflect.Proxy类:提供了为对象产生代理对象的方法:
        public static 0bject newProxyInstance(ClassLoader loader, class<?>[]interfaces,InvocationHandler h)
        参数一:用于指定用哪个类加载器去加载生成的代理类
        参数二:指定接口，这些接口用于指定生成的代理长什么样，也就是有哪些方法
        参数三:用来指定生成的代理对象要干什么事情
         */
        Star star = (Star) Proxy.newProxyInstance(
                ProxyUtil.class.getClassLoader(), // 参数一:用于指定用哪个类加载器去加载生成的代理类
                new Class[]{Star.class}, // 参数二:指定接口，这些接口用于指定生成的代理长什么样，也就是有哪些方法
                new InvocationHandler() { // 参数三:用来指定生成的代理对象要干什么事情
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        /*
                        参数一：代理的对象
                        参数二：要调用的方法
                        参数三：要调用的方法所需的参数
                         */
                        if ("sing".equals(method.getName())) {
                            System.out.println("准备话筒，收钱");
                        } else if ("dance".equals(method.getName())) {
                            System.out.println("准备舞台，收钱");
                        }
                        // 去找大明星开始唱歌跳舞
                        // 代码的表新形式：调用大明星的 唱歌或跳舞方法

                        return method.invoke(bigStar, args);
                    }
                });
        return star;
    }

}
