package com.enonic.xp.content;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.query.aggregation.AggregationQueries;
import com.enonic.xp.query.aggregation.AggregationQuery;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.query.highlight.HighlightQuery;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;

@PublicApi
public class ContentQuery
{
    public static final int DEFAULT_FETCH_SIZE = 10;

    private final QueryExpr queryExpr;

    private final ContentTypeNames contentTypeNames;

    private final ContentIds filterContentIds;

    private final AggregationQueries aggregationQueries;

    private final Filters queryFilters;

    private final HighlightQuery highlight;

    private final int from;

    private final int size;

    public ContentQuery( final Builder builder )
    {
        this.queryExpr = builder.queryExpr;
        this.contentTypeNames = builder.contentTypeNamesBuilder.build();
        this.filterContentIds = builder.filterContentIds;
        this.from = builder.from;
        this.size = builder.size;
        this.aggregationQueries = AggregationQueries.fromCollection( builder.aggregationQueries.build() );
        this.queryFilters = builder.queryFilters.build();
        this.highlight = builder.highlight;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public QueryExpr getQueryExpr()
    {
        return queryExpr;
    }

    public ContentTypeNames getContentTypes()
    {
        return contentTypeNames;
    }

    public ContentIds getFilterContentIds()
    {
        return filterContentIds;
    }

    public int getFrom()
    {
        return from;
    }

    public int getSize()
    {
        return size;
    }

    public AggregationQueries getAggregationQueries()
    {
        return aggregationQueries;
    }

    public Filters getQueryFilters()
    {
        return queryFilters;
    }

    public HighlightQuery getHighlight()
    {
        return highlight;
    }

    public static class Builder
    {
        private QueryExpr queryExpr;

        private ContentTypeNames.Builder contentTypeNamesBuilder = ContentTypeNames.create();

        private ContentIds filterContentIds;

        private int from = 0;

        private int size = DEFAULT_FETCH_SIZE;

        private ImmutableSet.Builder<AggregationQuery> aggregationQueries = ImmutableSet.builder();

        private Filters.Builder queryFilters = Filters.create();

        private HighlightQuery highlight;

        public Builder queryExpr( final QueryExpr queryExpr )
        {
            this.queryExpr = queryExpr;
            return this;
        }

        public Builder filterContentIds( final ContentIds filterContentIds )
        {
            this.filterContentIds = filterContentIds;
            return this;
        }

        public Builder addContentTypeName( final ContentTypeName contentTypeName )
        {
            this.contentTypeNamesBuilder.add( contentTypeName );
            return this;
        }

        public Builder addContentTypeNames( final ContentTypeNames contentTypeNames )
        {
            this.contentTypeNamesBuilder.addAll( contentTypeNames );
            return this;
        }

        public Builder from( final int from )
        {
            this.from = from;
            return this;
        }

        public Builder size( final int size )
        {
            this.size = size;
            return this;
        }

        public Builder aggregationQuery( final AggregationQuery aggregationQuery )
        {
            this.aggregationQueries.add( aggregationQuery );
            return this;
        }

        public Builder aggregationQueries( final Iterable<AggregationQuery> aggregationQueries )
        {
            this.aggregationQueries.addAll( aggregationQueries );
            return this;
        }

        public Builder queryFilter( final Filter queryFilter )
        {
            this.queryFilters.add( queryFilter );
            return this;
        }

        public Builder highlight( final HighlightQuery highlight )
        {
            this.highlight = highlight;
            return this;
        }

        public ContentQuery build()
        {
            return new ContentQuery( this );
        }
    }

}
