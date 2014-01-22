package com.enonic.wem.admin.rest.resource.content.json;

import com.enonic.wem.admin.json.content.ContentSummaryJson;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.query.aggregation.Aggregations;

public class AggregationContentSummaryListJson
    extends AbstractAggregationContentListJson<ContentSummaryJson>
{

    public AggregationContentSummaryListJson( final Content content, final Aggregations aggregations )
    {
        super( content, aggregations );
    }

    public AggregationContentSummaryListJson( final Contents contents, final Aggregations aggregations )
    {
        super( contents, aggregations );
    }

    @Override
    protected ContentSummaryJson createItem( final Content content )
    {
        return new ContentSummaryJson( content );
    }
}
