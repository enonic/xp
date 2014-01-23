package com.enonic.wem.admin.rest.resource.content.json;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.admin.json.content.ContentJson;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.query.aggregation.Aggregations;

public class AggregationsContentListJson
    extends AbstractAggregationContentListJson<ContentJson>
{
    public AggregationsContentListJson( final Content content, final Aggregations aggregations )
    {
        super( content, aggregations );
    }

    public AggregationsContentListJson( final Contents contents, final Aggregations aggregations )
    {
        super( contents, ImmutableSet.copyOf( aggregations ) );
    }

    @Override
    protected ContentJson createItem( final Content content )
    {
        return new ContentJson( content );
    }
}
