package com.enonic.xp.content;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class GetPublishStatusesResult
    implements Iterable<GetPublishStatusResult>
{
    private final ImmutableMap<ContentId, GetPublishStatusResult> getPublishStatusResultsMap;

    private GetPublishStatusesResult( Builder builder )
    {
        getPublishStatusResultsMap = ImmutableMap.copyOf( builder.compareResultsMap );
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public Iterator<GetPublishStatusResult> iterator()
    {
        return getPublishStatusResultsMap.values().iterator();
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
        private final Map<ContentId, GetPublishStatusResult> compareResultsMap = new LinkedHashMap<>();

        private Builder()
        {
        }

        public Builder add( final GetPublishStatusResult result )
        {
            this.compareResultsMap.put( result.getContentId(), result );
            return this;
        }

        public Builder addAll( final GetPublishStatusesResult results )
        {
            this.compareResultsMap.putAll( results.getPublishStatusResultsMap );
            return this;
        }

        public GetPublishStatusesResult build()
        {
            return new GetPublishStatusesResult( this );
        }
    }
}
