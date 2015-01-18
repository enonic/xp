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
import com.enonic.wem.api.content.ContentListMetaData;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.FindContentByQueryResult;
import com.enonic.wem.api.form.MixinReferencesToFormItemsTransformer;

public class FindContentByQuertResultJsonFactory
{
    public static AbstractContentQueryResultJson create( final FindContentByQueryResult contentQueryResult, final String expand,
                                                         final ContentIconUrlResolver iconUrlResolver,
                                                         final MixinReferencesToFormItemsTransformer mixinReferencesToFormItemsTransformer,
                                                         final ContentPrincipalsResolver contentPrincipalsResolver )
    {
        final AbstractContentQueryResultJson.Builder builder;

        final ContentListMetaData metadata = ContentListMetaData.create().
            totalHits( contentQueryResult.getTotalHits() ).
            hits( contentQueryResult.getHits() ).
            build();

        if ( Expand.FULL.matches( expand ) )
        {
            builder = ContentQueryResultJson.newBuilder( iconUrlResolver, contentPrincipalsResolver );
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
        addContents( contentQueryResult.getContents(), builder, mixinReferencesToFormItemsTransformer );
        setMetadata( metadata, builder );

        return builder.build();
    }

    private static void addContents( final Contents contents, final AbstractContentQueryResultJson.Builder builder,
                                     final MixinReferencesToFormItemsTransformer mixinReferencesToFormItemsTransformer )
    {
        for ( final Content content : contents )
        {
            builder.addContent( content, mixinReferencesToFormItemsTransformer );
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

    private static void setMetadata( final ContentListMetaData metadata, final AbstractContentQueryResultJson.Builder builder )
    {
        builder.setMetadata( metadata );
    }
}
