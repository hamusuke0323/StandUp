package com.hamusuke.standup.util;

import java.util.function.Consumer;

public class TickTimer<T> {
    protected final T t;
    protected final Consumer<T> onFinished;
    protected int tickLeft;
    protected boolean ticking;

    public TickTimer(T t, Consumer<T> onFinished) {
        this.t = t;
        this.onFinished = onFinished;
    }

    public void tick() {
        if (this.ticking) {
            --this.tickLeft;
            if (this.tickLeft <= 0) {
                this.finish();
                this.ticking = false;
            }
        }
    }

    public void start(int finishTicks) {
        if (this.ticking) {
            return;
        }

        this.tickLeft = finishTicks;
        this.ticking = true;
    }

    protected void finish() {
        if (!this.ticking) {
            return;
        }

        this.ticking = false;
        this.onFinished.accept(this.t);
    }

    public boolean isTimerTicking() {
        return this.ticking;
    }
}
