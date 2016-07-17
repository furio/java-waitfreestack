package me.furio.waitfreestack.operations;

import me.furio.waitfreestack.node.AbstractNode;

/**
 * Created by furione on 17/07/16.
 */
public class DeleteOperation<T> extends StackOperation<T> {
    private int threadId;

    public DeleteOperation(long phase, boolean pushed, AbstractNode<T> node) {
        super(phase, pushed, node);
    }

    public DeleteOperation(long phase, boolean pushed, AbstractNode<T> node, int threadid) {
        this(phase, pushed, node);
        this.threadId = threadid;
    }

    public int getThreadId() {
        return this.threadId;
    }

    public boolean isPending() {
        return this.isPushed();
    }

    public void setPending(boolean pend) {
        this.setPushed(pend);
    }
}
