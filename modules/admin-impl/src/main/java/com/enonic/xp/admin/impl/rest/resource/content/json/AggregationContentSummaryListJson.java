package com.enonic.xp.admin.impl.rest.resource.content.json;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.admin.impl.json.content.ContentSummaryJson;
import com.enonic.xp.admin.impl.rest.resource.content.ContentIconUrlResolver;
import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentListMetaData;
import com.enonic.xp.content.Contents;

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
