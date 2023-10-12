package course.concurrency.m2_async.cf.min_price;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();
    private Executor executor = Executors.newCachedThreadPool();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        var cfList = shopIds.stream()
                .map(sId -> CompletableFuture.supplyAsync(
                        () -> priceRetriever.getPrice(itemId, sId), executor)
                        .completeOnTimeout(Double.POSITIVE_INFINITY, 2950, TimeUnit.MILLISECONDS)
                        .exceptionally(e -> Double.POSITIVE_INFINITY))
                .collect(Collectors.toList());

        /* Это не нужно и так работает
        CompletableFuture
                .allOf(cfList.toArray(CompletableFuture[]::new))
                .join();
        */

        return cfList
                .stream()
                .mapToDouble(CompletableFuture::join)
                .filter(Double::isFinite)
                .min()
                .orElse(Double.NaN);
    }

    // какие-то попытки
    double test(long itemId) {
        List<CompletableFuture<Double>> cfs = new ArrayList<>();

        for (Long sId : shopIds) {
            cfs.add(CompletableFuture.supplyAsync(
                            () -> priceRetriever.getPrice(itemId, sId))
                    .completeOnTimeout(-1d, 2950, TimeUnit.MILLISECONDS));
        }

        int shopsNotRespond = 0;
        var min = Double.MAX_VALUE;
        for (CompletableFuture<Double> cf : cfs) {
            try {
                var p = cf.get();
                if (p == -1) {
                    shopsNotRespond++;
                } else if (p < min) {
                    min = p;
                }
            } catch (Exception ignore) {}
        }
        if (shopsNotRespond == shopIds.size()) {
            return Double.NaN;
        }
        return min;

    }
}
