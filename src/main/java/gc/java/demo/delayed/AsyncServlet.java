package gc.java.demo.delayed;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;

/**
 * Created by bulejava on 6/12/17.
 */
@WebServlet(value = "/async",asyncSupported=true)
public class AsyncServlet extends HttpServlet {

    static DelayQueue<Delayed> queue = new DelayQueue<>();
    static Map<String,ConfigData>  map = new HashMap<>();

     static {
        new Thread(()->{
            Delayed item;
            try {
                while ((item = queue.take())!=null){

                    ConfigData config = (ConfigData)item;
                    PrintWriter printWriter = config.asyncContext.getResponse().getWriter();
                    printWriter.println("key:" + config.key+" no change in 5s,request released "+LocalDate.now() + "|" + LocalTime.now());
                    config.asyncContext.complete();
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        AsyncContext asyncContext = req.startAsync();

        asyncContext.setTimeout(10000);
        PrintWriter out = resp.getWriter();
        resp.setContentType("text/plain");
        String key = req.getParameter("key");
        ConfigData configData = new ConfigData(key,asyncContext);
        out.println("response " + LocalDate.now() + "|" + LocalTime.now());
        out.flush();

        map.put(key,configData);
        queue.put(configData);
        asyncContext.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent event) throws IOException {
                System.out.println("onComplete");
                map.remove(key);
                event.getSuppliedResponse().getWriter().close();
            }

            @Override
            public void onTimeout(AsyncEvent event) throws IOException {
                System.out.println("onTimeout");
                ServletResponse response = event.getAsyncContext().getResponse();
                PrintWriter out = response.getWriter();
                out.println("time out," + LocalDate.now() + "|" + LocalTime.now());
                out.flush();

                map.remove(key);
                event.getSuppliedResponse().getWriter().close();
            }

            @Override
            public void onError(AsyncEvent event) throws IOException {
                map.remove(key);
                System.out.println("onError");
            }

            @Override
            public void onStartAsync(AsyncEvent event) throws IOException {
                System.out.println("onStartAsync");
            }
        });

    }
}
