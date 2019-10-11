package com.enonic.xp.repo.impl.version.search;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ExcludeEntries
    implements Iterable<ExcludeEntry>
{
    private final Set<ExcludeEntry> excludeEntries;

    @Override
    public Iterator<ExcludeEntry> iterator()
    {
        return excludeEntries.iterator();
    }

    public static ExcludeEntries empty()
    {
        return new ExcludeEntries( new HashSet<>() );
    }

    public boolean isEmpty()
    {
        return this.excludeEntries.isEmpty();
    }

    private ExcludeEntries( final Set<ExcludeEntry> excludeEntries )
    {
        this.excludeEntries = excludeEntries;
    }

    private ExcludeEntries( final Builder builder )
    {
        excludeEntries = builder.excludeEntries;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private final Set<ExcludeEntry> excludeEntries = new HashSet<>();

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