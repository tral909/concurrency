package course.concurrency.m3_shared.immutable;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public final class OrderImmutable {

    public enum Status {NEW, IN_PROGRESS, DELIVERED}

    private static final AtomicLong NEXT_ID = new AtomicLong();

    private final Long id;
    private final List<Item> items;
    private final PaymentInfo paymentInfo;
    private final boolean isPacked;
    private final Status status;

    public OrderImmutable(List<Item> items) {
        this(NEXT_ID.incrementAndGet(), items, null, false, Status.NEW);
    }

    public Long getId() {
        return id;
    }

    private OrderImmutable(Long id, List<Item> items, PaymentInfo paymentInfo, boolean isPacked, Status status) {
        this.id = id;
        this.items = Collections.unmodifiableList(items);
        this.paymentInfo = paymentInfo;
        this.isPacked = isPacked;
        this.status = status;
    }

    public OrderImmutable withStatus(Status status) {
        return new OrderImmutable(this.id, this.items, this.paymentInfo, this.isPacked, status);
    }

    public OrderImmutable withPaymentInfo(PaymentInfo paymentInfo) {
        return new OrderImmutable(this.id, this.items, paymentInfo, this.isPacked, Status.IN_PROGRESS);
    }

    public OrderImmutable packed() {
        return new OrderImmutable(this.id, this.items, this.paymentInfo, true, Status.IN_PROGRESS);
    }

    public boolean checkStatus() {
        return paymentInfo != null && isPacked;
    }

    public List<Item> getItems() {
        return items;
    }

    public Status getStatus() {
        return status;
    }
}