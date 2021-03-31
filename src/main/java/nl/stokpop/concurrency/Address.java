package nl.stokpop.concurrency;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class Address {

    private String name;
    private static String address;
    private String city = "Amsterdam";

    public synchronized void setName(String name) {
        this.name = name;
        sleep(100);
        this.address = name + " " + city;
    }

    public synchronized String getName() {
        return name;
    }

    public String createAddress() {
        synchronized (Address.class) {
            return address;
        }
    }

    private void sleep(int durationMillis) {
        try {
            Thread.sleep(durationMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
