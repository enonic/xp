package com.enonic.wem.admin.rest.resource.content.json;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.admin.json.content.ContentSummaryJson;
import com.enonic.wem.admin.rest.resource.content.ContentIconUrlResolver;
import com.enonic.wem.api.aggregation.Aggregations;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentListMetaData;
import com.enonic.wem.api.content.Contents;

public class AggregationContentSummaryListJson
    extends AbstractAggregationContentListJson<ContentSummaryJson>
{
    public AggregationContentSummaryListJson( final Content content, final ContentListMetaData contentListMetaData,
                                              final Aggregations aggregations, final ContentIconUrlResolver iconUrlResolver )
    {
        super( content, contentListMetaData, aggregations, iconUrlResolver, null, null );
    }

    public AggregationContentSummaryListJson( final Contents contents, final ContentListMetaData contentListMetaData,
                                              final Aggregations aggregations, final ContentIconUrlResolver iconUrlResolver )
    {
        super( contents, contentListMetaData, ImmutableSet.copyOf( aggregations ), iconUrlResolver, null, null );
    }

    @Override
    protected ContentSummaryJson createItem( final Content content )
    {
        return new ContentSummaryJson( content, iconUrlResolver );
    }
}
