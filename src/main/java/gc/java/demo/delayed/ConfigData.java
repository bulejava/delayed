package gc.java.demo.delayed;

import javax.servlet.AsyncContext;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by bulejava on 6/13/17.
 */
public class ConfigData implements Delayed {

     AsyncContext asyncContext;

     String key;

     long trigger = 0;


    public ConfigData(String key,AsyncContext context){
        this.asyncContext = context;
        this.key = key;
        trigger=System.nanoTime()+java.util.concurrent.TimeUnit.NANOSECONDS.convert(5000,java.util.concurrent.TimeUnit.MILLISECONDS);
    }


    @Override
    public long getDelay(TimeUnit unit) {
        System.out.println(LocalDate.now() + "|" + LocalTime.now());
        return unit.convert(trigger-System.nanoTime(), java.util.concurrent.TimeUnit.NANOSECONDS);
    }


    @Override
    public int compareTo(Delayed o) {
        return 0;
    }

}
