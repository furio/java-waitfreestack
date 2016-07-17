package me.furio.waitfreestack;

import me.furio.waitfreestack.node.AbstractNode;
import me.furio.waitfreestack.node.StackNode;
import me.furio.waitfreestack.node.StackSentinelNode;
import me.furio.waitfreestack.operations.PushOperation;
import org.javatuples.Pair;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Created by furione on 17/07/16.
 */
public class WaitFreeStack<T> {
    // This is a linked list
    private AbstractNode<T> listOfNodes;
    private AtomicReference<AbstractNode<T>> stackTop;
    // ---------------------

    private AtomicReferenceArray<PushOperation<T>> announce;
    private AtomicLong globalPhase;

    public WaitFreeStack(int maximumThreads) {
        this.listOfNodes = new StackSentinelNode<T>();
        this.stackTop.set(this.listOfNodes);

        this.globalPhase = new AtomicLong(0);
        this.announce = new AtomicReferenceArray<PushOperation<T>>(maximumThreads);
    }

    public void push(T value, int tid) {
        long phase = globalPhase.getAndIncrement();
        PushOperation<T> request = new PushOperation<T>(phase, false, new StackNode<T>(value, tid));
        announce.set(tid, request);
        help(request);
    }

    private void help(PushOperation<T> request) {
        long minGlobalPhase = Integer.MAX_VALUE;
        PushOperation<T> minValidRequest = null;

        for (int i = 0; i < announce.length(); i++) {
            PushOperation<T> op = announce.get(i);
            if ((op.getPhase() < minGlobalPhase) && !op.isPushed()) {
                minValidRequest = op;
                minGlobalPhase = minValidRequest.getPhase();
            }
        }

        // Do nothing
        if ((minValidRequest == null) || (minValidRequest.getPhase() > request.getPhase())) {
            return;
        }
        attachNode(request);

        // Need to check in the paper if we're talking of reference equals or object equals
        if (minValidRequest != request) {
            attachNode(request);
        }
    }

    private void attachNode(PushOperation<T> request) {
        while (!request.isPushed()) {
            AbstractNode<T> last = this.stackTop.get();
            Pair<AbstractNode<T>,Boolean> nodeWithStatus = last.getNextNodeWithMark();
            // Need to check in the paper if we're talking of reference equals or object equals
            if (last == this.stackTop.get()) {
                if ((nodeWithStatus.getValue0() == null) && !nodeWithStatus.getValue1()) {
                    if (!request.isPushed()) {
                        AbstractNode<T> myNode = request.getNode();
                        boolean result = last.putNextNode(myNode);
                        if (result) {
                            updateTop();

                            last.putEndNode(myNode);
                            return;
                        }
                    }
                }
            }

            updateTop();
        }
    }

    private void updateTop() {
        throw new RuntimeException();
    }
}
