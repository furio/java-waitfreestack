package me.furio.waitfreestack.node;

/**
 * Created by furione on 17/07/16.
 */
public class StackSentinelNode<T> extends AbstractNode<T> {
    public StackSentinelNode() {
        setValue(null);
    }

    @Override
    public boolean isSentinel() {
        return true;
    }
}