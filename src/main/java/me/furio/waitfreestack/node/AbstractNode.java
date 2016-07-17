package me.furio.waitfreestack.node;

import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by furione on 17/07/16.
 */
public abstract class AbstractNode<T> {
    // Basic Node
    private T value;
    private AtomicMarkableReference<AbstractNode<T>> nextNode;
    private AtomicReference<AbstractNode<T>> prevNode;

    public T getValue() {
        return this.getValue();
    }

    void setValue(T value) {
        this.value = value;
    }

    public AbstractNode<T> getNextNode() {
        return this.nextNode.getReference();
    }

    public AbstractNode<T> getPrevNode() {
        return this.prevNode.get();
    }


    public abstract boolean isSentinel();
}
