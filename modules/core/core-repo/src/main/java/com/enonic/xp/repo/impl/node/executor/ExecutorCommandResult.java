package com.enonic.xp.repo.impl.node.executor;

public interface ExecutorCommandResult<T>
{
    boolean isEmpty();

    T get();

}
