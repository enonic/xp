package com.enonic.xp.repo.impl.version.search;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

public final class ExcludeEntries
    implements Iterable<ExcludeEntry>
{
    private final Set<ExcludeEntry> excludeEntries;

    private static final ExcludeEntries EMPTY = create().build();

    @Override
    public Iterator<ExcludeEntry> iterator()
    {
        return excludeEntries.iterator();
    }

    private ExcludeEntries( final Builder builder )
    {
        excludeEntries = builder.excludeEntries.build();
    }

    public boolean isEmpty()
    {
        return this.excludeEntries.isEmpty();
    }

    public static ExcludeEntries empty()
    {
        return EMPTY;
    }

    public Set<ExcludeEntry> getSet()
    {
        return excludeEntries;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private final ImmutableSet.Builder<ExcludeEntry> excludeEntries = ImmutableSet.builder();

        private Builder()
        {
        }

        public Builder addAll( final Set<ExcludeEntry> val )
        {
            excludeEntries.addAll( val );
            return this;
        }

        public Builder add( final ExcludeEntry val )
        {
            excludeEntries.add( val );
            return this;
        }

        public ExcludeEntries build()
        {
            return new ExcludeEntries( this );
        }
    }
}
