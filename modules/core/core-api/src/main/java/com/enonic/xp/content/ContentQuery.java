package com.enonic.xp.content;

import com.google.common.base.Preconditions;
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

    private final ContentPath parentPath;

    private final ContentId parentId;

    private final ContentTypeNames contentTypeNames;

    private final ContentIds filterContentIds;

    private final AggregationQueries aggregationQueries;

    private final Filters queryFilters;

    private final HighlightQuery highlight;

    private final int from;

    private final int size;

    public ContentQuery( final Builder builder )
    {
        Preconditions.checkArgument( builder.parentPath == null || builder.parentId == null,
                                     "expected either parentPath or parentId, but not both" );
        this.queryExpr = builder.queryExpr;
        this.parentPath = builder.parentPath;
        this.parentId = builder.parentId;
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
     * Path of the content whose direct children the query is restricted to, or {@code null} when the parent is given by id or the query is
     * not restricted to a parent at all.
     *
     * @since 8.1.0
     */
    public ContentPath getParentPath()
    {
        return parentPath;
    }

    /**
     * Id of the content whose direct children the query is restricted to, or {@code null} when the parent is given by path or the query is
     * not restricted to a parent at all.
     *
     * @since 8.1.0
     */
    public ContentId getParentId()
    {
        return parentId;
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

        private ContentPath parentPath;

        private ContentId parentId;

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
         * Restricts the query to the direct children of the content at the given path. Combines with every other constraint of the query,
         * so paging, filters, content types, aggregations and highlighting apply as usual. Descendants deeper than one level are not
         * matched, and a parent that does not exist matches nothing.
         * <p>
         * When the query itself specifies no order expressions, results come back in the child order of the parent, the same order
         * {@link ContentService#findIdsByParent(FindContentByParentParams)} uses. Specify order expressions to sort otherwise.
         * <p>
         * Mutually exclusive with {@link #parentId(ContentId)}.
         *
         * @param parentPath path of the parent content, {@link ContentPath#ROOT} for the top level of the content tree.
         * @since 8.1.0
         */
        public Builder parentPath( final ContentPath parentPath )
        {
            this.parentPath = parentPath;
            return this;
        }

        /**
         * Restricts the query to the direct children of the content with the given id, otherwise identical to
         * {@link #parentPath(ContentPath)}. Mutually exclusive with it.
         *
         * @param parentId id of the parent content.
         * @since 8.1.0
         */
        public Builder parentId( final ContentId parentId )
        {
            this.parentId = parentId;
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
