package com.enonic.wem.admin.rest.resource.content.json;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.admin.json.content.ContentSummaryJson;
import com.enonic.wem.api.aggregation.Aggregations;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;

public class AggregationContentSummaryListJson
    extends AbstractAggregationContentListJson<ContentSummaryJson>
{

    public AggregationContentSummaryListJson( final Content content, final Aggregations aggregations )
    {
        super( content, aggregations );
    }

    public AggregationContentSummaryListJson( final Contents contents, final Aggregations aggregations )
    {
        super( contents, ImmutableSet.copyOf( aggregations ) );
    }

    @Override
    protected ContentSummaryJson createItem( final Content content )
    {
        return new ContentSummaryJson( content );
    }
}
