package com.enonic.xp.audit;

public interface CleanUpAuditLogListener
{
    /**
     * @deprecated The batch size is not part of the clean-up's contract, and {@link #resolved(int)} already announces the work before
     * the first record is deleted. Scheduled for removal.
     */
    @Deprecated
    default void start( int batchSize )
    {
    }

    /**
     * The producer's best current knowledge of the number of items to process — a running total, not an increment: each call
     * replaces the value of the previous one, correcting it in either direction as resolution proceeds, so a producer that can
     * cheaply foresee the whole amount may report it ahead of resolving every item. May be called any number of times, including
     * never; until the first call the amount of work is unknown, and a call with {@code -1} makes it unknown again, so the total
     * may move between known and unknown at any time.
     *
     * @since 8.1.0
     */
    default void resolved( int count )
    {
    }

    /**
     * @deprecated Renamed to {@link #recordsDeleted(int)}, so that this listener names the work it has done the way every other
     * listener does. An implementation of this method alone still hears every record: the default {@link #recordsDeleted(int)} calls
     * it once per record. Scheduled for removal.
     */
    @Deprecated
    default void processed()
    {
    }

    /**
     * The records deleted since the previous call. Sums to the number of records the clean-up deleted.
     *
     * @since 8.1.0
     */
    default void recordsDeleted( int count )
    {
        for ( int i = 0; i < count; i++ )
        {
            processed();
        }
    }

    /**
     * @deprecated The clean-up is over when it returns, which the caller that started it already knows. Scheduled for removal.
     */
    @Deprecated
    default void finished()
    {
    }
}
