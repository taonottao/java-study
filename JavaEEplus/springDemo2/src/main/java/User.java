import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/14 13:49
 */
@Configuration
public class User {
    public String sayHi(){
        return "hello world";
    }
}
