package com.enonic.xp.node;

public interface ApplyNodePermissionsListener
{
    /**
     * @deprecated Use {@link #resolved(int)}. A single upfront total cannot say the amount of work is unknown, and obliges the
     * operation to resolve all of it before starting. Scheduled for removal.
     */
    @Deprecated
    default void setTotal( int count )
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

    void permissionsApplied( int count );

    void notEnoughRights( int count );
}
