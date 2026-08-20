package com.enonic.xp.content;

public interface PushContentListener
{
    void contentPushed( int count );

    /**
     * @deprecated Renamed to {@link #resolved(int)}, so that every listener names the work it has resolved the same way. Still called
     * where it is the only one implemented: {@link #resolved(int)} forwards to this method and not the other way round, which is what
     * keeps an implementation of this one alone hearing the total, since that is the method producers call. Scheduled for removal.
     */
    @Deprecated
    default void contentResolved( int count )
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
        contentResolved( count );
    }
}
