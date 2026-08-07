package com.enonic.xp.content;

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Result of {@link ContentService#list(ListContentsByParentParams)}: every listed content as a lightweight entry, ordered by path.
 *
 * @since 8.1.0
 */
public final class ListContentsByParentResult
{
    private final ImmutableList<ContentListEntry> entries;

    private ListContentsByParentResult( final Builder builder )
    {
        this.entries = builder.entries.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public List<ContentListEntry> getEntries()
    {
        return entries;
    }

    public ContentIds getContentIds()
    {
        return entries.stream().map( ContentListEntry::getId ).collect( ContentIds.collector() );
    }

    public int getSize()
    {
        return entries.size();
    }

    public boolean isEmpty()
    {
        return entries.isEmpty();
    }

    public static final class Builder
    {
        private final ImmutableList.Builder<ContentListEntry> entries = ImmutableList.builder();

        private Builder()
        {
        }

        public Builder addEntry( final ContentListEntry entry )
        {
            this.entries.add( entry );
            return this;
        }

        public ListContentsByParentResult build()
        {
            return new ListContentsByParentResult( this );
        }
    }
}
