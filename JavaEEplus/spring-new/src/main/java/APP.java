import com.wt.demo.Student;
import com.wt.demo.UserService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/10/6 17:29
 */
public class APP {
    public static void main(String[] args) {
        // 1. 获取 Spring 上下文对象
        BeanFactory context =
                new XmlBeanFactory(new ClassPathResource("spring-config.xml"));
        // 2. 获取 Bean
        UserService user = (UserService) context.getBean("user");
        Student student = (Student) context.getBean("stu");
//
//        user.sayHi();
    }
}
