package nl.stokpop.commonpool;

import java.util.concurrent.*;

public class CommonPoolExhaustion {
    private static final long BLOCK_MILLIS = 60_000;

    public static void main(String[] args) {
        int tasks = 1000; // far beyond spare limit

        CompletableFuture<?>[] futures = new CompletableFuture[tasks];

        for (int i = 0; i < tasks; i++) {
            futures[i] = CompletableFuture.runAsync(() -> {
                System.out.println(Thread.currentThread().getName() + " started");

                managedSleep();

                System.out.println(Thread.currentThread().getName() + " finished");
            });
        }

        // Force observation of failures
        for (CompletableFuture<?> future : futures) {
            try {
                future.get(1, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                System.out.println("One task did not finish in time, skipping it");
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private static void managedSleep() {
        try {
            ForkJoinPool.managedBlock(new ForkJoinPool.ManagedBlocker() {
                private boolean done;

                @Override
                public boolean block() throws InterruptedException {
                    if (!done) {
                        Thread.sleep(BLOCK_MILLIS);
                        done = true;
                    }
                    return true;
                }

                @Override
                public boolean isReleasable() {
                    return done;
                }
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}