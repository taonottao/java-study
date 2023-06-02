import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/2 17:42
 */
@WebServlet("/refresh")
public class RefreshServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 每隔 1s 自动刷新一次
        resp.setHeader("Refresh", "1");
        resp.getWriter().write("time=" + System.currentTimeMillis());
    }
}
