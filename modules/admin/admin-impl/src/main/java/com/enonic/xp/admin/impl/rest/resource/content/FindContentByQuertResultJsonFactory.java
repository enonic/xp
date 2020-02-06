package com.enonic.xp.admin.impl.rest.resource.content;

import com.enonic.xp.admin.impl.json.aggregation.BucketAggregationJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.AbstractContentQueryResultJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.ContentIdQueryResultJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.ContentQueryResultJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.ContentSummaryQueryResultJson;
import com.enonic.xp.aggregation.Aggregation;
import com.enonic.xp.aggregation.Aggregations;
import com.enonic.xp.aggregation.BucketAggregation;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentListMetaData;
import com.enonic.xp.content.Contents;

public class FindContentByQuertResultJsonFactory
{
    private final long totalHits;

    private final long hits;

    private final Contents contents;

    private final Aggregations aggregations;

    private final ContentIconUrlResolver iconUrlResolver;

    private final ContentPrincipalsResolver contentPrincipalsResolver;

    private final ComponentNameResolver componentNameResolver;

    private final String expand;

    private FindContentByQuertResultJsonFactory( final Builder builder )
    {
        totalHits = builder.totalHits;
        hits = builder.hits;
        contents = builder.contents;
        aggregations = builder.aggregations;
        iconUrlResolver = builder.iconUrlResolver;
        contentPrincipalsResolver = builder.contentPrincipalsResolver;
        componentNameResolver = builder.componentNameResolver;
        expand = builder.expand;
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
        if ( aggregations == null )
        {
            return;
        }

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

    public static Builder create()
    {
        return new Builder();
    }

    public AbstractContentQueryResultJson execute()
    {
        final AbstractContentQueryResultJson.Builder builder;

        final ContentListMetaData metadata = ContentListMetaData.create().
            totalHits( this.totalHits ).
            hits( this.hits ).
            build();

        if ( Expand.FULL.matches( expand ) )
        {
            builder = ContentQueryResultJson.newBuilder( iconUrlResolver, contentPrincipalsResolver, componentNameResolver );
        }
        else if ( Expand.SUMMARY.matches( expand ) )
        {
            builder = ContentSummaryQueryResultJson.newBuilder( iconUrlResolver );
        }
        else
        {
            builder = ContentIdQueryResultJson.newBuilder();
        }

        addAggregations( this.aggregations, builder );
        addContents( this.contents, builder );
        setMetadata( metadata, builder );

        return builder.build();
    }

    public static final class Builder
    {
        private long totalHits = 0;

        private long hits = 0;

        private Contents contents = Contents.empty();

        private Aggregations aggregations = Aggregations.empty();

        private ContentIconUrlResolver iconUrlResolver;

        private ContentPrincipalsResolver contentPrincipalsResolver;

        private ComponentNameResolver componentNameResolver;

        private String expand;

        private Builder()
        {
        }

        public Builder totalHits( final long val )
        {
            totalHits = val;
            return this;
        }

        public Builder hits( final long val )
        {
            hits = val;
            return this;
        }

        public Builder contents( final Contents val )
        {
            contents = val;
            return this;
        }

        public Builder aggregations( final Aggregations val )
        {
            aggregations = val;
            return this;
        }

        public Builder iconUrlResolver( final ContentIconUrlResolver val )
        {
            iconUrlResolver = val;
            return this;
        }

        public Builder contentPrincipalsResolver( final ContentPrincipalsResolver val )
        {
            contentPrincipalsResolver = val;
            return this;
        }

        public Builder componentNameResolver( final ComponentNameResolver val )
        {
            componentNameResolver = val;
            return this;
        }


        public Builder expand( final String val )
        {
            expand = val;
            return this;
        }

        public FindContentByQuertResultJsonFactory build()
        {
            return new FindContentByQuertResultJsonFactory( this );
        }
    }
}
