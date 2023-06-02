import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/2 16:44
 */

class User{
    public String username;
    public String password;
}

@WebServlet("/json")
public class JsonServlet extends HttpServlet {
    // 使用 Jackson, 最核心的对象就是 ObjectMapper
    //  通过这个对象, 就可以把 json 字符串解析成 java 对象; 也可以把一个java 对象转成一个 json 格式字符串
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 通过 post 请求的 body 传递过来一个 json 格式的字符串
        User user = objectMapper.readValue(req.getInputStream(), User.class);
        System.out.println("username=" + user.username + ", password=" + user.password);

        resp.getWriter().write("ok");
    }
}
