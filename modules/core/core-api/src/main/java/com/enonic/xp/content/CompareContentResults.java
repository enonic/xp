package com.enonic.xp.content;

import java.util.Iterator;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class CompareContentResults
    implements Iterable<CompareContentResult>
{
    private final ImmutableMap<ContentId, CompareContentResult> compareContentResultsMap;

    private CompareContentResults( ImmutableMap<ContentId, CompareContentResult> builder )
    {
        compareContentResultsMap = builder;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Stream<CompareContentResult> stream()
    {
        return this.compareContentResultsMap.values().stream();
    }

    @Override
    public Iterator<CompareContentResult> iterator()
    {
        return compareContentResultsMap.values().iterator();
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
        private final ImmutableMap.Builder<ContentId, CompareContentResult> compareResultsMap = ImmutableMap.builder();

        private Builder()
        {
        }

        public Builder add( final CompareContentResult result )
        {
            this.compareResultsMap.put( result.getContentId(), result );
            return this;
        }

        public Builder addAll( final CompareContentResults results )
        {
            this.compareResultsMap.putAll( results.compareContentResultsMap );
            return this;
        }

        public CompareContentResults build()
        {
            return new CompareContentResults( compareResultsMap.buildKeepingLast() );
        }
    }
}
