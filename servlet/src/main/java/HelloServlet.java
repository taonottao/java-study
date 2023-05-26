import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/5/24 0:06
 */
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doGet(req, resp);
        // 这是在服务器的控制台中, 打印了字符串. (服务器看到了, 客户端看不到)
        System.out.println("hello world");
        // 这个是给 resp 的 body 中写入 hello world 字符串, 这个内容就会被 HTTP 响应返回给浏览器, 显示在浏览器页面上
        resp.getWriter().write("hello world");
    }
}
