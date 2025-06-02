package nl.stokpop.async;

import lombok.extern.slf4j.Slf4j;
import nl.stokpop.WatskeburtException;
import nl.stokpop.money.Amount;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
@SpringBootApplication
@EnableAsync
public class Futures {

    private final FutureService futureService;

    public Futures(FutureService futureService) {
        this.futureService = futureService;
    }

    public static void main(final String[] args) {
        SpringApplication.run(Futures.class, args);
    }

    @PostConstruct
    public void doSomething() {

        CompletableFuture<List<Amount>> amountsFuture1 = decorateCompletableFuture(
                futureService.calculateAmounts("Netherlands"),
                List.of(new Amount("0 EUR")));

        CompletableFuture<List<Amount>> amountsFuture2 = decorateCompletableFuture(
                futureService.calculateAmounts("USA"),
                List.of(new Amount("-1 USD")));

        CompletableFuture<List<Amount>> amountsFuture3 = decorateCompletableFuture(
                futureService.calculateAmounts("United Kingdom"),
                List.of(new Amount("0 GBP")));

        log.info("Main thread is doing something else.");

        log.info("Waiting for all amounts to be calculated.");

        List<Amount> combinedAmounts = new ArrayList<>(amountsFuture1.join());
        log.info("Waiting for second amounts to be calculated.");
        combinedAmounts.addAll(amountsFuture2.join());
        combinedAmounts.addAll(amountsFuture3.join());

        log.info("Combined amounts are: {}", combinedAmounts);

        log.info("Main thread is done.");
    }

    private static CompletableFuture<List<Amount>> decorateCompletableFuture(CompletableFuture<List<Amount>> calculateAmounts, List<Amount> defaults) {
        return calculateAmounts
                .orTimeout(2, SECONDS)
                .exceptionally(throwable -> {
                    String simpleName = throwable.getClass().getSimpleName();
                    String message = throwable.getMessage();
                    sleepInMillis(1_000);
                    log.error("Exception occurred {}: {}",
                            simpleName,
                            message == null ? "<no message>" : message);
                    return defaults;
                });
    }

    private static void sleepInMillis(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WatskeburtException("Interrupted!" + e.getMessage());
        }
    }

}
