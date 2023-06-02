import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/2 17:47
 */
@WebServlet("/redirect")
public class RedirectServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 用户访问这个路径的时候, 自动重定向到搜狗主页
//        resp.setStatus(302);
//        resp.setHeader("Location", "https://www.sogou.com");
        resp.sendRedirect("https://www.sogou.com");
    }
}
