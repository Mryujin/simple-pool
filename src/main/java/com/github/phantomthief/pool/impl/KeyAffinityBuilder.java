package com.github.phantomthief.pool.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Supplier;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.github.phantomthief.pool.KeyAffinity;
import com.github.phantomthief.util.ThrowableConsumer;

/**
 * @author w.vela
 * Created on 2018-02-09.
 */
@NotThreadSafe
public class KeyAffinityBuilder<V> {

    private static final int RANDOM_THRESHOLD = 20;

    private Supplier<V> factory;
    private int count;
    private ThrowableConsumer<V, Exception> depose;
    private Boolean usingRandom;

    public <K> KeyAffinity<K, V> build() {
        ensure();
        return new LazyKeyAffinity<>(this::buildInner);
    }

    <K> KeyAffinity<K, V> buildInner() {
        return new KeyAffinityImpl<>(factory, count, depose, usingRandom);
    }

    void ensure() {
        if (count <= 0) {
            throw new IllegalArgumentException("no count found.");
        }
        if (depose == null) {
            depose = it -> {};
        }
        if (usingRandom == null) {
            usingRandom = count > RANDOM_THRESHOLD;
        }
    }

    @SuppressWarnings("unchecked")
    @CheckReturnValue
    @Nonnull
    public <T extends KeyAffinityBuilder<V>> T factory(@Nonnull Supplier<V> factory) {
        this.factory = checkNotNull(factory);
        return (T) this;
    }

    /**
     * whether to use random strategy or less concurrency strategy
     * @param value {@code true} is random strategy and {@code false} is less concurrency strategy
     * default value is {@code true} is {@link #count} larger than {@link #RANDOM_THRESHOLD}
     */
    @SuppressWarnings("unchecked")
    @CheckReturnValue
    @Nonnull
    public <T extends KeyAffinityBuilder<V>> T usingRandom(boolean value) {
        this.usingRandom = value;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @CheckReturnValue
    @Nonnull
    public <T extends KeyAffinityBuilder<V>> T count(@Nonnegative int count) {
        checkArgument(count > 0);
        this.count = count;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @CheckReturnValue
    @Nonnull
    public <T extends KeyAffinityBuilder<V>> T
            depose(@Nonnegative ThrowableConsumer<V, Exception> depose) {
        this.depose = checkNotNull(depose);
        return (T) this;
    }
}
