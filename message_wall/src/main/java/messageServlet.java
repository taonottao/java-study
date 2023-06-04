import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
//    private List<Message> messageList = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 通过这个方法来处理 "获取所有留言消息"
        // 需要返回一个 json 字符串数组. jackson 直接帮我们处理好了格式
        List<Message> messageList = load();
        String respString = objectMapper.writeValueAsString(messageList);
        resp.setContentType("application/json;charset=utf8");
        resp.getWriter().write(respString);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 通过这个方法来处理 "提交新消息"
        Message message = objectMapper.readValue(req.getInputStream(), Message.class);
//        messageList.add(message);
        try {
            save(message);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // 响应只返回 200 报文, body 为空, 此时不需要额外处理, 默认就是返回 200
    }

    // 这个方法用来王数据库中存一条数据
    private void save(Message message) {
        DataSource dataSource = new MysqlDataSource();
        ((MysqlDataSource)dataSource).setUrl("jdbc:mysql://127.0.1:3306/java107?characterEncoding=utf8&useSSL=false");
        ((MysqlDataSource)dataSource).setUser("root");
        ((MysqlDataSource)dataSource).setPassword("wangtao");

        try {
            Connection connection = dataSource.getConnection();
            String sql = "insert into message values(?, ?, ?);";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, message.from);
            statement.setString(2, message.to);
            statement.setString(3, message.message);
            statement.executeUpdate();

            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //  这个方法用来从数据库查询所有记录
    private List<Message> load(){
        List<Message> messageList = new ArrayList<>();
        DataSource dataSource = new MysqlDataSource();
        ((MysqlDataSource)dataSource).setUrl("jdbc:mysql://127.0.1:3306/java107?characterEncoding=utf8&useSSL=false");
        ((MysqlDataSource)dataSource).setUser("root");
        ((MysqlDataSource)dataSource).setPassword("wangtao");

        try {
            Connection connection = dataSource.getConnection();
            String sql = "select* from message";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                Message message = new Message();
                message.from = resultSet.getString("from");
                message.to = resultSet.getString("to");
                message.message = resultSet.getString("message");
                messageList.add(message);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messageList;
    }

}
