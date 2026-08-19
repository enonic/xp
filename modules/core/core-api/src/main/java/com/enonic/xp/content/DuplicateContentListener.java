package com.enonic.xp.content;

public interface DuplicateContentListener
{
    /**
     * The number of items resolved for processing so far — a running total, not an increment: each call replaces the value of the
     * previous one. May be called any number of times, including never; until the first call the amount of work is unknown, and a
     * call with {@code -1} makes it unknown again, so the total may move between known and unknown at any time.
     *
     * @since 8.1.0
     */
    default void resolved( int count )
    {
    }

    void contentDuplicated( int count );

    void contentReferencesUpdated( int count );
}
