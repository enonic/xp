package com.enonic.wem.admin.rest.resource.content.json;

import com.enonic.wem.admin.json.content.ContentIdJson;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.query.aggregation.Aggregations;

public class AggregationContentIdListJson
    extends AbstractAggregationContentListJson<ContentIdJson>
{

    public AggregationContentIdListJson( final Content content, final Aggregations aggregations )
    {
        super( content, aggregations );
    }

    public AggregationContentIdListJson( final Contents contents, final Aggregations aggregations )
    {
        super( contents, aggregations );
    }

    @Override
    protected ContentIdJson createItem( final Content content )
    {
        return new ContentIdJson( content.getId() );
    }
}
