package com.sioeye.youle.run.order.domain.common;

public class ConcurrencySafeEntity extends Entity{

    private int concurrencyVersion;

    protected ConcurrencySafeEntity(String id) {
        super(id);
    }

    public int concurrencyVersion() {
        return this.concurrencyVersion;
    }

    public void setConcurrencyVersion(int version) {
        this.failWhenConcurrencyViolation(version);
        this.concurrencyVersion = version;
    }

    public void failWhenConcurrencyViolation(int version) {
        if (version != this.concurrencyVersion()) {
            throw new IllegalStateException(
                    "Concurrency Violation: Stale data detected. Entity was already modified.");
        }
    }
}
