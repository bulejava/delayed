package gc.java.demo.delayed;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by bulejava on 6/14/17.
 */
@WebServlet(value = "/change")
public class DataChangeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String key = req.getParameter("key");
        String value = req.getParameter("value");
        PrintWriter currentWrite = resp.getWriter();
        resp.setContentType("text/plain");

        ConfigData keyData = AsyncServlet.map.get(key);
        if(keyData !=null) {
            AsyncContext context = keyData.asyncContext;

            PrintWriter printWriter = context.getResponse().getWriter();
            printWriter.println("value:" + value);
            printWriter.flush();
            context.complete();
            currentWrite.print("change ok");
        }else{
            currentWrite.print("no request holding");
        }

        currentWrite.flush();
        currentWrite.close();
    }
}
