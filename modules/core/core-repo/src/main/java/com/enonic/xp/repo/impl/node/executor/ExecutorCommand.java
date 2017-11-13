package com.enonic.xp.repo.impl.node.executor;

public interface ExecutorCommand<T>
{
    long getTotalHits();

    ExecutorCommandResult<T> execute( final int from, final int size );
}
