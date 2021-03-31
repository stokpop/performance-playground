package nl.stokpop.concurrency;

import java.util.Collections;
import java.util.Map;

class TestVolatile extends Thread {

    //volatile
    private volatile Map<String, Boolean> keepRunning = Collections.emptyMap();
    // private Map<String, Boolean> keepRunning = Collections.emptyMap();

    //volatile Boolean keepRunning = Boolean.TRUE;
    //Boolean keepRunning = Boolean.TRUE;
    //volatile boolean keepRunning = true;
    // boolean keepRunning = true;

    public void run() {
        long count = 0;

        while (keepRunning()) {
            count++;
        }

        System.out.println("Thread terminated. Count: " + count);
    }

    public boolean keepRunning() {
        return !keepRunning.containsKey("TRUE");
    }

    public void stopRunning() {
        // this.keepRunning = Boolean.FALSE;
        this.keepRunning = Collections.singletonMap("TRUE", Boolean.TRUE);
    }

    public static void main(String[] args) throws InterruptedException {

        TestVolatile t = new TestVolatile();
        t.start();

        Thread.sleep(1000);

        System.out.println("After sleeping in main, request thread to stop.");

        //t.keepRunning = Boolean.FALSE;
        t.stopRunning();
        t.join();

        System.out.println("KeepRunning set to " + t.keepRunning);
    }
}
