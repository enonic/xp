package com.enonic.wem.admin.rest.resource.content;

import com.enonic.wem.admin.json.aggregation.BucketAggregationJson;
import com.enonic.wem.admin.rest.resource.content.json.AbstractContentQueryResultJson;
import com.enonic.wem.admin.rest.resource.content.json.ContentIdQueryResultJson;
import com.enonic.wem.admin.rest.resource.content.json.ContentQueryResultJson;
import com.enonic.wem.admin.rest.resource.content.json.ContentSummaryQueryResultJson;
import com.enonic.wem.api.aggregation.Aggregation;
import com.enonic.wem.api.aggregation.Aggregations;
import com.enonic.wem.api.aggregation.BucketAggregation;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.FindContentByQueryResult;

public class FindContentByQuertResultJsonFactory
{
    public static AbstractContentQueryResultJson create( final FindContentByQueryResult contentQueryResult, final String expand,
                                                         final ContentIconUrlResolver iconUrlResolver )
    {
        final AbstractContentQueryResultJson.Builder builder;

        if ( Expand.FULL.matches( expand ) )
        {
            builder = ContentQueryResultJson.newBuilder( iconUrlResolver );
        }
        else if ( Expand.SUMMARY.matches( expand ) )
        {
            builder = ContentSummaryQueryResultJson.newBuilder( iconUrlResolver );
        }
        else
        {
            builder = ContentIdQueryResultJson.newBuilder();
        }

        addAggregations( contentQueryResult.getAggregations(), builder );
        addContents( contentQueryResult.getContents(), builder );

        return builder.build();
    }

    private static void addContents( final Contents contents, final AbstractContentQueryResultJson.Builder builder )
    {
        for ( final Content content : contents )
        {
            builder.addContent( content );
        }
    }

    private static void addAggregations( final Aggregations aggregations, final AbstractContentQueryResultJson.Builder builder )
    {
        for ( final Aggregation aggregation : aggregations )
        {
            if ( aggregation instanceof BucketAggregation )
            {
                builder.addAggregation( new BucketAggregationJson( (BucketAggregation) aggregation ) );
            }
        }
    }
}
