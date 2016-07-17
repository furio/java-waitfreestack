package me.furio.waitfreestack.node;

import org.javatuples.Pair;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by furione on 17/07/16.
 */
public abstract class AbstractNode<T> {
    // Basic Node
    private T value;
    private int pushTid;  // Id of the thread that created the request

    private AtomicLong index;
    private AtomicMarkableReference<AbstractNode<T>> nextNode;
    private AtomicReference<AbstractNode<T>> prevNode;

    protected AbstractNode() {
        this.index = new AtomicLong(0);
        this.nextNode = new AtomicMarkableReference<AbstractNode<T>>(null, false);
        this.prevNode = new AtomicReference<AbstractNode<T>>(null);
    }

    public T getValue() {
        return this.value;
    }
    public int getTid() { return this.pushTid; }
    public long getIndex() {
        return this.index.get();
    }

    void setValue(T val) {
        this.value = val;
    }
    void setTid(int tid) {
        this.pushTid = tid;
    }
    public long increaseIndex() {
        return this.index.incrementAndGet();
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
    public boolean setPrevNode(AbstractNode<T> prevNode) {
        return this.setPrevNode(null, prevNode);
    }

    public boolean setPrevNode(AbstractNode<T> expectedNode, AbstractNode<T> prevNode) {
        return this.prevNode.compareAndSet(expectedNode, prevNode);
    }

    public abstract boolean isSentinel();
    public abstract boolean getAndSetMark(boolean mark);
}
