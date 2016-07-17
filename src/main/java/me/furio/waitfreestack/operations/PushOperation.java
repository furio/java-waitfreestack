package me.furio.waitfreestack.operations;

import me.furio.waitfreestack.node.AbstractNode;
import me.furio.waitfreestack.node.StackNode;
/**
 * Created by furione on 17/07/16.
 */
public class PushOperation<T> extends StackOperation<T> {
    public PushOperation(long phase, boolean pushed, AbstractNode<T> node) {
        super(phase, pushed, node);
    }
}
