package me.furio.waitfreestack.node;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by furione on 17/07/16.
 */

public class StackNode<T> extends AbstractNode<T> {
    // Helpers
    private AtomicBoolean mark;
    private AtomicLong index;
    private AtomicLong counter; // Used for stack cleanup

    public StackNode(T value, int tid) {
        super();
        setValue(value);
        setTid(tid);
        this.counter = new AtomicLong(0);
    }

    @Override
    public boolean isSentinel() {
        return false;
    }

    @Override
    public boolean getAndSetMark(boolean mark) {
        return this.mark.getAndSet(mark);
    }
}


