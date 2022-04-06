package nl.stokpop.network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Run DNS lookups from Java continuously to check for hick-ups over time.
 */
public class DnsLookup {

    public static void main(String[] args) {

        if (args.length != 3 && args.length != 4) {
            System.out.println("Provide: comma-separated-hostnames times delayInMs [parallel]");
            System.exit(100);
        }

        String hostnames = args[0];
        int times = Integer.parseInt(args[1]);
        long delayInMs = Long.parseLong(args[2]);
        boolean parallel = args.length == 4;

        Function<String, InetAddress[]> function = host -> {
            try {
                return InetAddress.getAllByName(host);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                return null;
            }
        };

        InfiniteList<String> infiniteList = new InfiniteList<>(Arrays.asList(hostnames.split(",")));

        Stream<String> hostGenerator = Stream.generate(infiniteList::next);

        if (parallel) {
            hostGenerator.limit(times)
                    .parallel()
                    .map(host -> delayed(Function.identity(), timed(function, host), delayInMs))
                    .map(DnsLookup::formatOutputLine)
                    .forEach(System.out::println);
        }
        else {
            hostGenerator.limit(times)
                    .map(host -> delayed(Function.identity(), timed(function, host), delayInMs))
                    .map(DnsLookup::formatOutputLine)
                    .forEach(System.out::println);
        }
    }

    private static String formatOutputLine(Timed<InetAddress[]> t) {
        return Instant.now() + " Duration: " + Duration.ofNanos(t.getDurationInNanos()) + " (" + t.getDurationInNanos() + " ns) [" + Thread.currentThread().getName() + "] Result: " + Arrays.toString(t.getResult());
    }

    public static <T,U> Timed<U> timed(Function<T, U> function, T in) {
        long startTime = System.nanoTime();
        U out = function.apply(in);
        long endTime = System.nanoTime();
        return new Timed<>(out, endTime - startTime);
    }

    public static class Timed<R> {
        public Timed(R result, long durationInNanos) {
            this.result = result;
            this.durationInNanos = durationInNanos;
        }

        private final R result;
        private final long durationInNanos;

        public R getResult() {
            return result;
        }

        public long getDurationInNanos() {
            return durationInNanos;
        }
    }

    public static <T,U> U delayed(Function<T, U> function, T in, long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return function.apply(in);
    }

    private static class InfiniteList<T> {

        final List<T> list;
        final int size;
        final AtomicInteger count = new AtomicInteger(0);

        public InfiniteList(List<T> list) {
            this.list = new ArrayList<>(list);
            this.size = list.size();
        }

        public T next() {
            return list.get(count.incrementAndGet() % size);
        }
    }
}
