package com.enonic.wem.admin.rest.resource.content;

import com.enonic.wem.admin.json.aggregation.BucketAggregationJson;
import com.enonic.wem.admin.rest.resource.content.json.AbstractContentQueryResultJson;
import com.enonic.wem.admin.rest.resource.content.json.ContentIdQueryResultJson;
import com.enonic.wem.admin.rest.resource.content.json.ContentQueryResultJson;
import com.enonic.wem.admin.rest.resource.content.json.ContentSummaryQueryResultJson;
import com.enonic.wem.api.aggregation.Aggregation;
import com.enonic.wem.api.aggregation.BucketAggregation;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.query.ContentQueryHit;
import com.enonic.wem.api.content.query.ContentQueryResult;

public class ContentQueryResultJsonFactory
{
    public static AbstractContentQueryResultJson create( final ContentQueryResult contentQueryResult, final Contents contents,
                                                         final String expand )
    {
        final AbstractContentQueryResultJson.Builder builder;

        if ( Expand.FULL.matches( expand ) )
        {
            builder = ContentQueryResultJson.newBuilder();
        }
        else if ( Expand.SUMMARY.matches( expand ) )
        {
            builder = ContentSummaryQueryResultJson.newBuilder();
        }
        else
        {
            builder = ContentIdQueryResultJson.newBuilder();
        }

        addAggregations( contentQueryResult, builder );
        addContents( contentQueryResult, contents, builder );

        return builder.build();
    }

    private static void addContents( final ContentQueryResult contentQueryResult, final Contents contents,
                                     final AbstractContentQueryResultJson.Builder builder )
    {
        for ( final ContentQueryHit queryHit : contentQueryResult.getContentQueryHits() )
        {
            builder.addContent( contents.getContentById( queryHit.getContentId() ) );
        }
    }

    private static void addAggregations( final ContentQueryResult contentQueryResult, final AbstractContentQueryResultJson.Builder builder )
    {
        for ( final Aggregation aggregation : contentQueryResult.getAggregations() )
        {
            if ( aggregation instanceof BucketAggregation )
            {
                builder.addAggregation( new BucketAggregationJson( (BucketAggregation) aggregation ) );
            }
        }
    }
}
