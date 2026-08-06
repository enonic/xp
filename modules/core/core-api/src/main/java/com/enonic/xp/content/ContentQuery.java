package com.enonic.xp.content;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.query.aggregation.AggregationQueries;
import com.enonic.xp.query.aggregation.AggregationQuery;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.query.highlight.HighlightQuery;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;


public final class ContentQuery
{
    public static final int DEFAULT_FETCH_SIZE = 10;

    private final QueryExpr queryExpr;

    private final ContentPath parent;

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
        this.parent = builder.parent;
        this.contentTypeNames = builder.contentTypeNamesBuilder.build();
        this.filterContentIds = builder.filterContentIds;
        this.from = builder.from;
        this.size = builder.size;
        this.aggregationQueries = AggregationQueries.from( builder.aggregationQueries.build() );
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

    /**
     * Path of the content whose direct children the query is restricted to, or {@code null} when the query is not restricted to a parent.
     *
     * @since 8.1.0
     */
    public ContentPath getParent()
    {
        return parent;
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

    public static final class Builder
    {
        private QueryExpr queryExpr;

        private ContentPath parent;

        private final ContentTypeNames.Builder contentTypeNamesBuilder = ContentTypeNames.create();

        private ContentIds filterContentIds;

        private int from = 0;

        private int size = DEFAULT_FETCH_SIZE;

        private final ImmutableSet.Builder<AggregationQuery> aggregationQueries = ImmutableSet.builder();

        private final Filters.Builder queryFilters = Filters.create();

        private HighlightQuery highlight;

        private Builder()
        {
        }

        public Builder queryExpr( final QueryExpr queryExpr )
        {
            this.queryExpr = queryExpr;
            return this;
        }

        /**
         * Restricts the query to the direct children of the given content path. Combines with every other constraint of the query, so
         * ordering, paging, filters, content types and aggregations apply as usual. Descendants deeper than one level are not matched.
         * <p>
         * Unlike {@link ContentService#findIdsByParent(FindContentByParentParams)}, the order of the parent is not applied implicitly:
         * pass explicit order expressions when the child order matters.
         *
         * @param parent path of the parent content, {@link ContentPath#ROOT} for the top level of the content tree.
         * @since 8.1.0
         */
        public Builder parent( final ContentPath parent )
        {
            this.parent = parent;
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
