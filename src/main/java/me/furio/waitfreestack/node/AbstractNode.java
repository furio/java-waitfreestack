package me.furio.waitfreestack.node;

import org.javatuples.Pair;

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

    protected AbstractNode() {
        this.nextNode = new AtomicMarkableReference<AbstractNode<T>>(null, false);
        this.prevNode = new AtomicReference<AbstractNode<T>>(null);
    }

    public T getValue() {
        return this.getValue();
    }

    void setValue(T value) {
        this.value = value;
    }

    public Pair<AbstractNode<T>,Boolean> getNextNodeWithMark() {
        return new Pair<AbstractNode<T>,Boolean>(this.nextNode.getReference(), this.nextNode.isMarked());
    }

    public AbstractNode<T> getNextNode() {
        return this.nextNode.getReference();
    }

    public boolean putNextNode(AbstractNode<T> next) {
        return this.setNextNode(null, next, false, false);
    }

    public boolean putEndNode(AbstractNode<T> expected) {
        return this.setNextNode(expected, null, false, true);
    }

    private boolean setNextNode(AbstractNode<T> expected, AbstractNode<T> next, boolean expectedMark, boolean newMark) {
        return this.nextNode.compareAndSet(expected, next, expectedMark, newMark);
    }

    public AbstractNode<T> getPrevNode() {
        return this.prevNode.get();
    }


    public abstract boolean isSentinel();
}
