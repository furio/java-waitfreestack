package me.furio.waitfreestack.operations;

import me.furio.waitfreestack.node.AbstractNode;

/**
 * Created by furione on 17/07/16.
 */
public abstract class StackOperation<T> {
    private long phase;
    private boolean pushed;
    private AbstractNode<T> node;

    public StackOperation(long phase, boolean pushed, AbstractNode<T> node) {
        this.phase = phase;
        this.pushed = pushed;
        this.node = node;
    }

    public long getPhase() {
        return this.phase;
    }

    public boolean isPushed() {
        return this.pushed;
    }

    public void setPushed(boolean pushed) {
        this.pushed = pushed;
    }

    public AbstractNode<T> getNode() {
        return this.node;
    }
}
