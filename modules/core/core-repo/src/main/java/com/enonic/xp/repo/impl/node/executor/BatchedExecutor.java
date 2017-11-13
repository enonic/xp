package com.enonic.xp.repo.impl.node.executor;

public class BatchedExecutor<T>
{
    private final int batchSize;

    private int currentFrom = 0;

    private boolean hasMore = true;

    private final ExecutorCommand<T> executorCommand;

    private final long totalHits;

    public BatchedExecutor( final ExecutorCommand<T> executorCommand, final int batchSize )
    {
        this.batchSize = batchSize;
        this.executorCommand = executorCommand;
        this.totalHits = executorCommand.getTotalHits();
    }

    public T execute()
    {
        final ExecutorCommandResult<T> result = this.executorCommand.execute( this.currentFrom, this.batchSize );

        if ( result.isEmpty() )
        {
            this.hasMore = false;
        }
        else
        {
            this.currentFrom += this.batchSize;

            this.hasMore = currentFrom < totalHits;
        }

        return result.get();
    }

    public long getTotalHits()
    {
        return totalHits;
    }

    public boolean hasMore()
    {
        return this.hasMore;
    }


}
