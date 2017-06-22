package com.enonic.xp.node;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;

public class SearchTargets
    implements Iterable<SearchTarget>
{
    private final Collection<SearchTarget> targets;

    private SearchTargets( final Collection<SearchTarget> targets )
    {
        this.targets = targets;
    }

    private SearchTargets( final Builder builder )
    {
        targets = builder.targets;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static SearchTargets from( final Collection<SearchTarget> searchTargets )
    {
        return new SearchTargets( searchTargets );
    }

    @Override
    public Iterator<SearchTarget> iterator()
    {
        return targets.iterator();
    }

    public static final class Builder
    {
        private Set<SearchTarget> targets = Sets.newHashSet();

        private Builder()
        {
        }

        public Builder add( final SearchTarget target )
        {
            this.targets.add( target );
            return this;
        }

        public SearchTargets build()
        {
            return new SearchTargets( this );
        }
    }
}


