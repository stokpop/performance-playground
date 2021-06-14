package nl.stokpop.reactor;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Semaphore;

public class SimpleReactorStreams {

    private final Semaphore semaphore = new Semaphore(0);

    public static void main(String[] args) {
        new SimpleReactorStreams().steps();
    }

    /**
     * Run a Reactor stream, with delayed elements.
     * Wait main thread until the stream is finished, using a Semaphore.
     */
    private void steps() {

        long startTime = System.currentTimeMillis();

        Flux<Integer> steps = Flux.just(1,2,3,4,5,6);
        steps.map(i -> i * 2)
            .delayElements(Duration.ofSeconds(1)) // try this before and after the buffer
            .doOnEach(System.out::println)
            .buffer(3)
            .doOnEach(System.out::println)
            .doAfterTerminate(() -> { printDuration(startTime); semaphore.release(); })
            .subscribe(System.out::println);

        sleepTillFinished();
    }

    private void sleepTillFinished() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            System.out.println("Semaphore wait was interrupted!");
        }
    }

    private void printDuration(long startTime) {
        long durationInMillis = System.currentTimeMillis() - startTime;
        System.out.println("Duration of pipeline: " + Duration.of(durationInMillis, ChronoUnit.MILLIS));
    }
}
