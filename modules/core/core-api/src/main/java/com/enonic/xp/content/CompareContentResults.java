package com.enonic.xp.content;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

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

    private final ContentIds requiredIds;

    private CompareContentResults( Builder builder )
    {
        compareContentResults = ImmutableSet.copyOf( builder.compareResults );
        compareContentResultsMap = ImmutableMap.copyOf( builder.compareResultsMap );
        requiredIds = builder.requiredIds.build();
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

    public ContentIds getRequiredIds()
    {
        return requiredIds;
    }

    public int size()
    {
        return compareContentResultsMap.size();
    }

    public static final class Builder
    {
        private Set<CompareContentResult> compareResults = Sets.newHashSet();

        private Map<ContentId, CompareContentResult> compareResultsMap = Maps.newHashMap();

        private ContentIds.Builder requiredIds = ContentIds.create();

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

        public Builder addRequiredIds( final ContentIds ids )
        {
            this.requiredIds.addAll( ids );
            return this;
        }


        public CompareContentResults build()
        {
            return new CompareContentResults( this );
        }
    }
}