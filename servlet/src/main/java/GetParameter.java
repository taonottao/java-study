import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/2 15:02
 */
@WebServlet("/getParameter")
public class GetParameter extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 前端通过 url 的 query string 传递 username 和 password 两个属性.
        String username = req.getParameter("username");
        if(username == null){
            System.out.println("username 这个 key 在 query string 中不存在!");
        }
        String password = req.getParameter("password");
        if(password == null){
            System.out.println("password 这个 key 在 query string 中不存在!");
        }
        System.out.println("username="+username + ", password="+password);

        resp.getWriter().write("ok");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf8");
        // 前端通过 body, 以 form 表单的格式, 把 username 和 password 传给服务器.
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        if(username == null){
            System.out.println("username 这个 key 在 body 中 不存在!");
        }
        if(password == null){
            System.out.println("password  这个 key 在 body 中不存在!");
        }
        System.out.println("username="+username + ", password="+password);

        resp.getWriter().write("ok");
    }
}
