import com.wt.demo.Articles;
import com.wt.demo.Student;
import com.wt.demo.UConfig;
import com.wt.demo.UserService;
import com.wt.demo.entity.ArticleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/10/6 15:32
 */

public class Application {
    @Autowired
    private UserService userService;
    public static void main(String[] args) {
        // 1. 先得到 spring 上下文对象
        ApplicationContext context =
                new ClassPathXmlApplicationContext("spring-config.xml");
        // 2. 得到 Bean
//        UserService userService = (UserService) context.getBean("user");
//        UserService user = context.getBean(UserService.class);
//        UserService user = context.getBean("user",UserService.class);
//        UserService user = context.getBean("userInfo",UserService.class);
//        UserService user = context.getBean("userService",UserService.class);
//        user.sayHi();
//        // 3. 使用 Bean
//        userService.sayHi();

//        UConfig uConfig = context.getBean("UConfig", UConfig.class);
//        uConfig.sayHi();

//        ArticleInfo articleInfo1 = context.getBean("getArticle", ArticleInfo.class);
//        ArticleInfo articleInfo = context.getBean("article", ArticleInfo.class);
        ArticleInfo articleInfo = context.getBean("getBean", ArticleInfo.class);
        System.out.println(articleInfo.toString());

//        Articles articles = context.getBean("articles", Articles.class);
//        articles.sayHi();
    }
}
