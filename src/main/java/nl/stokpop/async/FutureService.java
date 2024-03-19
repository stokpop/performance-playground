package nl.stokpop.async;

import lombok.extern.slf4j.Slf4j;
import nl.stokpop.money.Amount;
import org.apache.commons.logging.impl.WeakHashtable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class FutureService {
    private static final Map<String, String> CURRENCIES =
            Map.of("netherlands", "EUR", "usa", "USD", "united kingdom", "GBP");

    @Async
    public CompletableFuture<List<Amount>> calculateAmounts(String country) {
        log.info("Calculate amounts for {}", country);

        int multiplier = country.length();

        String ccy = CURRENCIES.getOrDefault(country.trim().toLowerCase(), "UNK");

        if (multiplier == 3) {
            throw new WatskeburtException("Simulated exception for " + country);
        }

        try {
            Thread.sleep(multiplier * 500L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WatskeburtException("Sleep interrupted");
        }

        Amount amount1 = new Amount(multiplier + " " + ccy);
        Amount amount2 = new Amount(multiplier * 2 + " " + ccy);
        List<Amount> amounts = List.of(amount1, amount2);

        log.info("Calculated amounts for {} are {}", country, amounts);

        return CompletableFuture.completedFuture(amounts);
    }
}
