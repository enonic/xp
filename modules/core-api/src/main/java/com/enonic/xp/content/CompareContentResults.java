package com.enonic.xp.content;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Beta
public class CompareContentResults
    implements Iterable<CompareContentResult>
{
    private final ImmutableSet<CompareContentResult> compareContentResults;

    private final Map<ContentId, CompareContentResult> compareContentResultsMap;

    private CompareContentResults( Builder builder )
    {
        compareContentResults = ImmutableSet.copyOf( builder.compareResults );
        compareContentResultsMap = ImmutableMap.copyOf( builder.compareResultsMap );
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

    public Map<ContentId, CompareContentResult> getCompareContentResultsMap()
    {
        return compareContentResultsMap;
    }

    public static final class Builder
    {
        private Set<CompareContentResult> compareResults = Sets.newHashSet();

        private Map<ContentId, CompareContentResult> compareResultsMap = Maps.newHashMap();

        private Builder()
        {
        }

        public Builder add( final CompareContentResult result )
        {
            this.compareResults.add( result );
            this.compareResultsMap.put( result.getContentId(), result );
            return this;
        }

        public CompareContentResults build()
        {
            return new CompareContentResults( this );
        }
    }
}