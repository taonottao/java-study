import com.demo.component.ArticleController;
import com.demo.component.BController;
import com.demo.component.UserComponent;
import com.demo.component.aController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;

import java.beans.Introspector;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/14 13:06
 */
public class APP {
    public static void main(String[] args) {
        // 1. 先得到 Spring 对象
        ApplicationContext context = new ClassPathXmlApplicationContext("spring_config.xml");
        // 2. 从 Spring 中取出 Bean 对象
//        User user = (User) context.getBean("user");
//        User user = context.getBean(User.class);
//        User user = context.getBean("user", User.class);
//        // 3. 使用 Bean (可选)
//        System.out.println(user.sayHi());

//        ArticleController articleController = context.getBean("articleController", ArticleController.class);
//        System.out.println(articleController.sayHello());

//        aController aController = context.getBean("aController", aController.class);
//        System.out.println(aController.sayHi());

//        BController bController = context.getBean("BController", BController.class);
//        System.out.println(bController.sayF());

//        String s1 = "UserInfo";
//        System.out.println("s1:" + Introspector.decapitalize(s1));
//
//        String s2 = "userInfo";
//        System.out.println("s2:" + Introspector.decapitalize(s2));
//
//        String s3 = "UInfo";
//        System.out.println("s3:" + Introspector.decapitalize(s3));
        UserComponent userComponent = context.getBean("userComponent", UserComponent.class);
        System.out.println(userComponent.sayHi());

    }
}
