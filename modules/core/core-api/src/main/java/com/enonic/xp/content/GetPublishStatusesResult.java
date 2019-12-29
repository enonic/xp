package com.enonic.xp.content;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class GetPublishStatusesResult
    implements Iterable<GetPublishStatusResult>
{
    private final ImmutableSet<GetPublishStatusResult> getPublishStatusResults;

    private final Map<ContentId, GetPublishStatusResult> getPublishStatusResultsMap;

    private GetPublishStatusesResult( Builder builder )
    {
        getPublishStatusResults = ImmutableSet.copyOf( builder.compareResults );
        getPublishStatusResultsMap = ImmutableMap.copyOf( builder.compareResultsMap );
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public Iterator<GetPublishStatusResult> iterator()
    {
        return getPublishStatusResults.iterator();
    }

    public Map<ContentId, GetPublishStatusResult> getGetPublishStatusResultsMap()
    {
        return getPublishStatusResultsMap;
    }

    public ContentIds contentIds()
    {
        return ContentIds.from( getPublishStatusResultsMap.keySet() );
    }

    public int size()
    {
        return getPublishStatusResultsMap.size();
    }

    public static final class Builder
    {
        private Set<GetPublishStatusResult> compareResults = new HashSet<>();

        private Map<ContentId, GetPublishStatusResult> compareResultsMap = new HashMap<>();

        private Builder()
        {
        }

        public Builder add( final GetPublishStatusResult result )
        {
            this.compareResults.add( result );
            this.compareResultsMap.put( result.getContentId(), result );
            return this;
        }

        public Builder addAll( final GetPublishStatusesResult results )
        {
            this.compareResults.addAll( results.getPublishStatusResults );

            for ( final GetPublishStatusResult result : results )
            {
                this.compareResultsMap.put( result.getContentId(), result );
            }

            return this;
        }

        public GetPublishStatusesResult build()
        {
            return new GetPublishStatusesResult( this );
        }
    }
}