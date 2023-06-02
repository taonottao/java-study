import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/2 14:39
 */

@WebServlet("/showRequest")
public class ShowRequest extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StringBuilder result = new StringBuilder();
        result.append(req.getProtocol());
        result.append("<br>");
        result.append(req.getMethod());
        result.append("<br>");
        result.append(req.getRequestURI());
        result.append("<br>");
        result.append(req.getQueryString());
        result.append("<br>");
        result.append(req.getContextPath());
        result.append("<br>");

        result.append("=======================<br>");

        Enumeration<String> headerNames =  req.getHeaderNames();
        while (headerNames.hasMoreElements()){
            String headerName = headerNames.nextElement();
            String headerValue = req.getHeader(headerName);
            result.append(headerName + ":" + headerValue + "<br>");
        }

        // 在响应中设置上 body 的类型, 方便浏览器进行解析
        resp.setContentType("text/html;charset=utf8");
        resp.getWriter().write(result.toString());
    }
}
