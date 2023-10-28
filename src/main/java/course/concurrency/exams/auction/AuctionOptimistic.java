package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private final Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private final AtomicReference<Bid> latestBid = new AtomicReference<>();

    {
        latestBid.set(new Bid(0L, 0L, 0L));
    }

    public boolean propose(Bid bid) {
        Bid oldBid;
        do {
            oldBid = latestBid.get();
            if (bid.getPrice() <= oldBid.getPrice()) {
                return false;
            }
        } while (!latestBid.compareAndSet(oldBid, bid));
        notifier.sendOutdatedMessage(oldBid);
        return true;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }
}
