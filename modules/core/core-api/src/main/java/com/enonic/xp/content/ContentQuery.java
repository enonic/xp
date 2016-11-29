package com.enonic.xp.content;

import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import com.enonic.xp.query.aggregation.AggregationQueries;
import com.enonic.xp.query.aggregation.AggregationQuery;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.Filters;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;

@Beta
public class ContentQuery
{
    public static final int DEFAULT_FETCH_SIZE = 10;

    private final QueryExpr queryExpr;

    private final ContentTypeNames contentTypeNames;

    private final ContentIds filterContentIds;

    private final AggregationQueries aggregationQueries;

    private final Filters queryFilters;

    private final int from;

    private final int size;

    public ContentQuery( final Builder builder )
    {
        this.queryExpr = builder.queryExpr;
        this.contentTypeNames = builder.contentTypeNamesBuilder.build();
        this.filterContentIds = builder.filterContentIds;
        this.from = builder.from;
        this.size = builder.size;
        this.aggregationQueries = AggregationQueries.fromCollection( ImmutableSet.copyOf( builder.aggregationQueries ) );
        this.queryFilters = builder.queryFilters.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final ContentQuery source )
    {
        return new Builder( source );
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

    public static class Builder
    {
        private QueryExpr queryExpr;

        private ContentTypeNames.Builder contentTypeNamesBuilder;

        private ContentIds filterContentIds;

        private int from;

        private int size;

        private Set<AggregationQuery> aggregationQueries;

        private Filters.Builder queryFilters;

        private Builder()
        {
            this.contentTypeNamesBuilder = ContentTypeNames.create();
            this.from = 0;
            this.size = DEFAULT_FETCH_SIZE;
            this.aggregationQueries = Sets.newHashSet();
            this.queryFilters = Filters.create();
        }

        private Builder( ContentQuery source )
        {
            this.queryExpr = source.queryExpr;
            this.contentTypeNamesBuilder = ContentTypeNames.create().addAll( source.contentTypeNames );
            this.filterContentIds = source.filterContentIds;
            this.from = source.from;
            this.size = source.size;
            this.aggregationQueries = Sets.newHashSet( source.aggregationQueries );
            this.queryFilters = Filters.create();
            source.queryFilters.forEach( this.queryFilters::add );
        }

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
            Iterables.addAll( this.aggregationQueries, aggregationQueries );
            return this;
        }

        public Builder queryFilter( final Filter queryFilter )
        {
            this.queryFilters.add( queryFilter );
            return this;
        }

        public ContentQuery build()
        {
            return new ContentQuery( this );
        }
    }

}
