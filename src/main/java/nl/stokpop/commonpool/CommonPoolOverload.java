package nl.stokpop.commonpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class CommonPoolOverload {

    public static final int TIMEOUT_MILLIS = 2500;
    public static final int NUMBER_OF_THREADS = 200;
    public static final int BLOCK_TIME_MILLIS = 200;
    public static final String TIMEOUT_MESSAGE = "timeout!";

    public static void main(String[] args) {
        new CommonPoolOverload().start();
    }

    private void start() {

        // this task simulates a task that does some blocking IO
        // like getting a value from a remote cache
        Supplier<?> taskWithLittleBlockingIO = () -> {
            try {
                Thread.sleep(BLOCK_TIME_MILLIS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName() + " Found item in cache, returning result.");
            return "found item in cache!";
        };

        List<String> answers = new ArrayList<>();

        // create custom executor with 200 threads, like a default
        // tomcat threadpool in Spring Boot
        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        long startTimeMillis = System.currentTimeMillis();

        // now call findResult 200 times in parallel, using the custom executor
        // and collect all results in the List
        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> findResult(taskWithLittleBlockingIO), executor);
            if (i % 10 == 0) {
                long count = futures.parallelStream().filter(CompletableFuture::isDone).count();
                System.out.println(Thread.currentThread().getName() + " Submitted " + (i + 1) + " tasks, " + count + " are done.");
            }
            futures.add(future);
        }
        for (CompletableFuture<String> future : futures) {
            try {
                answers.add(future.get(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }
        long durationMillis = System.currentTimeMillis() - startTimeMillis;
        System.out.println("Hooray, all answers: " + answers.size());
        long count = answers.parallelStream().filter(a -> a.equals(TIMEOUT_MESSAGE)).peek(m -> System.out.println(Thread.currentThread().getName() + " Found: " + m)).count();
        System.out.println("Found " + count + " timeouts");
        System.out.println("Duration: " + durationMillis + " ms");

        executor.shutdown();
    }

    private static String findResult(Supplier<?> taskWithLittleBlockingIO) {
        System.out.println(Thread.currentThread().getName() + " Starting findResult");
        // notice there is no explicit executor specified here, so it will use the common pool,
        // which has a default of CPUs-1 threads
        CompletableFuture<?> future = CompletableFuture.supplyAsync(taskWithLittleBlockingIO);
        try {
            return (String) future.get(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            return TIMEOUT_MESSAGE;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
