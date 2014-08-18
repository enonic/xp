package com.enonic.wem.admin.rest.resource.content.json;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.admin.json.content.ContentIdJson;
import com.enonic.wem.api.aggregation.Aggregations;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentListMetaData;
import com.enonic.wem.api.content.Contents;

public class AggregationContentIdListJson
    extends AbstractAggregationContentListJson<ContentIdJson>
{

    public AggregationContentIdListJson( final Content content, final ContentListMetaData contentListMetaData,
                                         final Aggregations aggregations )
    {
        super( content, contentListMetaData, aggregations );
    }

    public AggregationContentIdListJson( final Contents contents, final ContentListMetaData contentListMetaData,
                                         final Aggregations aggregations )
    {
        super( contents, contentListMetaData, ImmutableSet.copyOf( aggregations ) );
    }

    @Override
    protected ContentIdJson createItem( final Content content )
    {
        return new ContentIdJson( content.getId() );
    }
}
