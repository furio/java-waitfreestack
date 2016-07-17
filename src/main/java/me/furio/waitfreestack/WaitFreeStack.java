package me.furio.waitfreestack;

import me.furio.waitfreestack.node.AbstractNode;
import me.furio.waitfreestack.node.StackNode;
import me.furio.waitfreestack.node.StackSentinelNode;
import me.furio.waitfreestack.operations.DeleteOperation;
import me.furio.waitfreestack.operations.PushOperation;
import org.javatuples.Pair;

import java.util.EmptyStackException;
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
    private static long W = 11111111; // Randomly set
    private AtomicReferenceArray<PushOperation<T>> announce;
    private AtomicReferenceArray<DeleteOperation<T>> allDeleteRequests;
    private AtomicLong globalPhase;
    private AtomicLong deletePhase;
    private AtomicReference<DeleteOperation<T>> uniqueRequest;

    public WaitFreeStack() {
        this.listOfNodes = new StackSentinelNode<T>();
        this.stackTop.set(this.listOfNodes);

        this.globalPhase = new AtomicLong(0);
        this.deletePhase = new AtomicLong(0);
        this.announce = new AtomicReferenceArray<PushOperation<T>>(Integer.MAX_VALUE);
        this.allDeleteRequests = new AtomicReferenceArray<DeleteOperation<T>>(Integer.MAX_VALUE);
        this.uniqueRequest = new AtomicReference<DeleteOperation<T>>(null);
    }

    public void push(T value, int tid) {
        long phase = globalPhase.getAndIncrement();
        PushOperation<T> request = new PushOperation<T>(phase, false, new StackNode<T>(value, tid));
        announce.set(tid, request);
        help(request);
    }

    public T pop() {
        AbstractNode<T> mytop = this.stackTop.get();
        AbstractNode<T> curr = mytop;
        while(!curr.isSentinel()) {
            boolean mark = curr.getAndSetMark(true);
            if (!mark) {
                break;
            }

            curr = curr.getPrevNode();
        }

        if (curr.isSentinel()) {
            throw new EmptyStackException();
        }

        tryCleanUp(curr);
        return curr.getValue();
    }

    private void help(PushOperation<T> request) {
        long minGlobalPhase = Long.MAX_VALUE;
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
        attachNode(minValidRequest);

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
        AbstractNode<T> last = this.stackTop.get();
        Pair<AbstractNode<T>, Boolean> nodeWithStatus = last.getNextNodeWithMark();
        if (nodeWithStatus.getValue0() != null) {
            PushOperation<T> request = this.announce.get(nodeWithStatus.getValue0().getTid());
            if ((last == this.stackTop.get()) && (request.getNode() == nodeWithStatus.getValue0())) {
                nodeWithStatus.getValue0().setPrevNode(last);
                nodeWithStatus.getValue0().increaseIndex();
                request.setPushed(true);
                boolean stat = this.stackTop.compareAndSet(last, nodeWithStatus.getValue0());
                if ((nodeWithStatus.getValue0().getIndex() % W == 0) && stat) {
                    tryCleanUp(nodeWithStatus.getValue0());
                }
            }
        }
    }

    private void tryCleanUp(AbstractNode<T> node) {
        AbstractNode<T> temp = node.getPrevNode();
        while (!temp.isSentinel()) {
            if (temp.getIndex() % W == 0) {
                if (temp.increaseIndex() == (W+1)) {
                    clean((int)Thread.currentThread().getId(), temp);
                }

                break;
            }

            temp = temp.getPrevNode();
        }
    }

    private void clean(int tid, AbstractNode<T> node) {
        long phase = this.deletePhase.getAndIncrement();
        DeleteOperation<T> request = new DeleteOperation<T>(phase, true, node, tid);

        this.allDeleteRequests.set(tid, request);
        helpDelete(request);
    }

    private void helpDelete(DeleteOperation<T> request) {
        long minGlobalPhase = Long.MAX_VALUE;
        DeleteOperation<T> minValidRequest = null;

        for (int i = 0; i < this.allDeleteRequests.length(); i++) {
            DeleteOperation<T> op = this.allDeleteRequests.get(i);
            op.setPending(true);
            if (op.getPhase() < minGlobalPhase) {
                minValidRequest = op;
                minGlobalPhase = minValidRequest.getPhase();
            }
        }

        // Do nothing
        if ((minValidRequest == null) || (minValidRequest.getPhase() > request.getPhase())) {
            return;
        }
        uniqueDelete(minValidRequest);

        // Need to check in the paper if we're talking of reference equals or object equals
        if (minValidRequest != request) {
            uniqueDelete(request);
        }
    }

    private void uniqueDelete(DeleteOperation<T> request) {
        while (request.isPending()) {
            DeleteOperation<T> currReq = this.uniqueRequest.get();
            if (currReq == null || !currReq.isPending()) {
                if (request.isPending()) {
                    boolean stat = true;
                    if (request != currReq) {
                        stat = this.uniqueRequest.compareAndSet(currReq, request);
                    }

                    helpFinishDelete();
                    if (stat) {
                        return;
                    }
                }
            }

            helpFinishDelete();
        }
    }

    private void helpFinishDelete() {
        throw new RuntimeException();
    }
}
