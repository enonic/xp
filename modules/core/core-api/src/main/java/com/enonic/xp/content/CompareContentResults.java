package com.enonic.xp.content;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
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

    public Stream<CompareContentResult> stream()
    {
        return this.compareContentResults.stream();
    }

    @Override
    public Iterator<CompareContentResult> iterator()
    {
        return compareContentResults.iterator();
    }

    public Map<ContentId, CompareContentResult> getCompareContentResultsMap()
    {
        return compareContentResultsMap;
    }

    public ContentIds contentIds()
    {
        return ContentIds.from( compareContentResultsMap.keySet() );
    }

    public int size()
    {
        return compareContentResultsMap.size();
    }

    public static final class Builder
    {
        private Set<CompareContentResult> compareResults = new HashSet<>();

        private Map<ContentId, CompareContentResult> compareResultsMap = new HashMap<>();

        private Builder()
        {
        }

        public Builder add( final CompareContentResult result )
        {
            this.compareResults.add( result );
            this.compareResultsMap.put( result.getContentId(), result );
            return this;
        }

        public Builder addAll( final CompareContentResults results )
        {
            this.compareResults.addAll( results.compareContentResults );

            for ( final CompareContentResult result : results )
            {
                this.compareResultsMap.put( result.getContentId(), result );
            }

            return this;
        }

        public CompareContentResults build()
        {
            return new CompareContentResults( this );
        }
    }
}