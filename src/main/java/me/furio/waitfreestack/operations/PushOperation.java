package me.furio.waitfreestack.operations;

import me.furio.waitfreestack.node.StackNode;
/**
 * Created by furione on 17/07/16.
 */
public class PushOperation<T> implements StackOperation {
    private long phase;
    private boolean pushed;
    private StackNode<T> node;

    public PushOperation(long phase, boolean pushed, StackNode<T> node) {
        this.phase = phase;
        this.pushed = pushed;
        this.node = node;
    }

    public long getPhase() {
        return phase;
    }

    public boolean isPushed() {
        return pushed;
    }

    public void setPushed(boolean pushed) {
        this.pushed = pushed;
    }

    public StackNode<T> getNode() {
        return node;
    }
}
