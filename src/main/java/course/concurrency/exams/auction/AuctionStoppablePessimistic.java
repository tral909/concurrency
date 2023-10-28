package course.concurrency.exams.auction;

public class AuctionStoppablePessimistic implements AuctionStoppable {

    private Notifier notifier;

    public AuctionStoppablePessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid = new Bid(0L, 0L, 0L);

    private volatile boolean isOpen = true;

    private final Object lock = new Object();

    public boolean propose(Bid bid) {
        if (isOpen && bid.getPrice() > latestBid.getPrice()) {
            synchronized (lock) {
                if (isOpen && bid.getPrice() > latestBid.getPrice()) {
                    notifier.sendOutdatedMessage(latestBid);
                    latestBid = bid;
                    return true;
                }
            }
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid;
    }

    public Bid stopAuction() {
        synchronized (lock) {
            isOpen = false;
            return latestBid;
        }
    }
}
