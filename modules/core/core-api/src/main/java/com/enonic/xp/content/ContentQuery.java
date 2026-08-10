package com.enonic.xp.content;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.index.IndexPath;
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

    /**
     * The fields a content query may ask back per hit, mapped to the name a {@link Content} shows each of them by. A content answers
     * only for what it shows, so anything outside this set is rejected rather than fetched.
     * <p>
     * Keys are index paths, and therefore lowercase; values are the names callers see on a hit.
     *
     * @since 8.1.0
     */
    public static final Map<IndexPath, String> SUPPORTED_RETURN_FIELDS =
        Stream.of( "_name", "_path", ContentPropertyNames.DISPLAY_NAME, ContentPropertyNames.TYPE, ContentPropertyNames.CREATOR,
                   ContentPropertyNames.MODIFIER, ContentPropertyNames.CREATED_TIME, ContentPropertyNames.MODIFIED_TIME,
                   ContentPropertyNames.OWNER, ContentPropertyNames.LANGUAGE )
            .collect( Collectors.toUnmodifiableMap( IndexPath::from, name -> name ) );

    private final QueryExpr queryExpr;

    private final ContentPath parentPath;

    private final ContentId parentId;

    private final boolean recursive;

    private final ImmutableSet<IndexPath> returnFields;

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
        Preconditions.checkArgument( !builder.recursive || builder.parentPath != null || builder.parentId != null,
                                     "recursive expects a parentPath or a parentId" );
        this.queryExpr = builder.queryExpr;
        this.parentPath = builder.parentPath;
        this.parentId = builder.parentId;
        this.recursive = builder.recursive;
        this.returnFields = ImmutableSet.copyOf( builder.returnFields );
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

    /**
     * Whether the parent restriction reaches every descendant instead of the direct children only. Always {@code false} when the query is
     * not restricted to a parent.
     *
     * @since 8.1.0
     */
    public boolean isRecursive()
    {
        return recursive;
    }

    /**
     * The fields asked to come back with every hit, delivered per id through {@link FindContentIdsByQueryResult#getFields()}.
     *
     * @since 8.1.0
     */
    public Set<IndexPath> getReturnFields()
    {
        return returnFields;
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

        private boolean recursive;

        private final Set<IndexPath> returnFields = new LinkedHashSet<>();

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
         * so paging, filters, content types, aggregations and highlighting apply as usual. Deeper descendants are matched only with
         * {@link #recursive(boolean)}, and a parent that does not exist matches nothing.
         * <p>
         * When the query itself specifies no order expressions, results come back in the child order of the parent. Specify order
         * expressions to sort otherwise.
         * <p>
         * The path is relative to the content root of the calling context, which the same API serves the archive through: the very same
         * path names a different content depending on whether the context roots it at the content tree or at the archive.
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

        /**
         * Widens the parent restriction from the direct children to every descendant of the parent, at any depth. Expects a parent to be
         * set, by either {@link #parentPath(ContentPath)} or {@link #parentId(ContentId)}.
         * <p>
         * A parent orders its own children, so its child order is not applied to a subtree - it would sort levels against each other by
         * a value only siblings can be compared on. Specify order expressions when the order of a recursive result matters.
         *
         * @since 8.1.0
         */
        public Builder recursive( final boolean recursive )
        {
            this.recursive = recursive;
            return this;
        }

        /**
         * Asks for fields to come back with every hit, from {@link #SUPPORTED_RETURN_FIELDS} only. Each field arrives as a list of
         * strings, and a field a hit has no value for is absent rather than empty. {@code _path} answers with a content path, relative to
         * the content root of the calling context, so a query run against the archive answers with paths inside the archive.
         *
         * @throws IllegalArgumentException for a field outside {@link #SUPPORTED_RETURN_FIELDS}.
         * @since 8.1.0
         */
        public Builder returnFields( final IndexPath... fields )
        {
            for ( final IndexPath field : fields )
            {
                Preconditions.checkArgument( SUPPORTED_RETURN_FIELDS.containsKey( field ), "unsupported return field: %s", field );
                this.returnFields.add( field );
            }
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
