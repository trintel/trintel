package sopro.model.util;

import java.time.ZonedDateTime;

/**
 * A simple custom id-handler, because it is easier to import data into the database, if we dont use auto generated ids.
 */
public class IdHandler {

    // Yes it is not good for runtime. Yes it is 2 in the morning. Yes you could do
    // it better.
    public static long generateId() {
        try {
            Thread.sleep(1); //still not ideal but at least functional
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long b = ZonedDateTime.now().toInstant().toEpochMilli();

        return b;
    }
}
