package com.enonic.xp.content;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class CompareContentResults
    implements Iterable<CompareContentResult>
{
    private final Map<ContentId, CompareContentResult> compareContentResultsMap;

    private CompareContentResults( Builder builder )
    {
        compareContentResultsMap = ImmutableMap.copyOf( builder.compareResultsMap );
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
        private final Map<ContentId, CompareContentResult> compareResultsMap = new LinkedHashMap<>();

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
            return new CompareContentResults( this );
        }
    }
}
