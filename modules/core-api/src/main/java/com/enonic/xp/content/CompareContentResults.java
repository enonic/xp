package com.enonic.xp.content;

import java.util.Iterator;
import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

@Beta
public class CompareContentResults
    implements Iterable<CompareContentResult>
{
    private final ImmutableSet<CompareContentResult> compareContentResults;

    private CompareContentResults( Builder builder )
    {
        compareContentResults = ImmutableSet.copyOf( builder.compareResults );
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public Iterator<CompareContentResult> iterator()
    {
        return compareContentResults.iterator();
    }

    public ImmutableSet<CompareContentResult> getCompareContentResults()
    {
        return compareContentResults;
    }

    public static final class Builder
    {
        private Set<CompareContentResult> compareResults = Sets.newHashSet();


        private Builder()
        {
        }

        public Builder add( final CompareContentResult result )
        {
            this.compareResults.add( result );
            return this;
        }

        public CompareContentResults build()
        {
            return new CompareContentResults( this );
        }
    }
}
