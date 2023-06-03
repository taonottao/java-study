import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @Author T-WANG
 * @Date 2023/6/2 22:48
 */

class Message{
    // 这几个属性必须设置 public !!!
    // 如果设置 private, 必须生成 public 的 getter 和 setter !!!
    public String from;
    public String to;
    public String message;

    @Override
    public String toString() {
        return "Message{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}

@WebServlet("/message")
public class messageServlet extends HttpServlet {
    private ObjectMapper objectMapper = new ObjectMapper();
    private List<Message> messageList = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 通过这个方法来处理 "获取所有留言消息"
        // 需要返回一个 json 字符串数组. jackson 直接帮我们处理好了格式
        String respString = objectMapper.writeValueAsString(messageList);
        resp.setContentType("application/json;charset=utf8");
        resp.getWriter().write(respString);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 通过这个方法来处理 "提交新消息"
        Message message = objectMapper.readValue(req.getInputStream(), Message.class);
        messageList.add(message);

        // 响应只返回 200 报文, body 为空, 此时不需要额外处理, 默认就是返回 200
    }
}
