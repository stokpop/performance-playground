package nl.stokpop.jersey;

import com.sun.jersey.api.client.Client;
import nl.stokpop.WatskeburtException;

public class LotsOfJersey {
    public static void main(String[] args) {
        new LotsOfJersey().createLotsOfJersey();
    }

    private void createLotsOfJersey() {
        println("Start create clients");
        final long startTimeMillis = System.currentTimeMillis();
        int i = 0;
        while (i < 10_000) {
            if (i++ % 1000 == 0) {
                println("Clients created " + i + " times in " + (System.currentTimeMillis() - startTimeMillis) + " ms");
            }
            Client client = Client.create();
            client.setReadTimeout(100);
            client.setConnectTimeout(100);
            client.viewResource("http://localhost:8080/jersey");
            //sleepMillis(10);
        }
    }

    private static void println(String message) {
        System.out.println(message);
    }

    private static void sleepMillis(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            println("Interrupted!" + e.getMessage());
        }
    }
}
