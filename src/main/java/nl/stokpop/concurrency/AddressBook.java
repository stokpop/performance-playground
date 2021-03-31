package nl.stokpop.concurrency;

import net.jcip.annotations.ThreadSafe;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ThreadSafe
public class AddressBook {

    private AtomicInteger threadCount = new AtomicInteger(0);

    private Address address;

    public AddressBook(Address sharedAddress) {
        this.address = sharedAddress;
    }

    public static void main(String[] args) {
        new AddressBook(new Address()).mutateAddressWithMultipleThreads();
    }

    public void mutateAddressWithMultipleThreads() {

        ThreadFactory threadFactory = r -> new Thread(r, "MyThread-" + threadCount.incrementAndGet());
        ExecutorService threadPool = Executors.newFixedThreadPool(9, threadFactory);

        Supplier<Callable<Void>> mutatorSupplier = () -> (Callable<Void>) () -> {
            String threadName = Thread.currentThread().getName();
            String addressLine;
            //synchronized (address) {
                address.setName(threadName);
                //sleep(100);
                addressLine = this.address.createAddress();
            //}
            if (!addressLine.contains(threadName)) {
                System.out.println("Oops! " + threadName + ":" + addressLine);
            }

            return null;
        };

        List<Callable<Void>> mutators = Stream.generate(mutatorSupplier).limit(1000).collect(Collectors.toList());

        try {
            threadPool.invokeAll(mutators);
            //threadPool.awaitTermination(10, TimeUnit.SECONDS);
            threadPool.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
