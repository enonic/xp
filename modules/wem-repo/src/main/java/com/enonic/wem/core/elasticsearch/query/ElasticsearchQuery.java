package com.enonic.wem.core.elasticsearch.query;

import java.util.Set;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.core.elasticsearch.ReturnFields;

public class ElasticsearchQuery
{
    private static final int DEFAULT_SIZE = 10;

    private final QueryBuilder query;

    private final FilterBuilder filter;

    private final String indexType;

    private final String indexName;

    private final ImmutableSet<SortBuilder> sortBuilders;

    private final int from;

    private final int size;

    private final boolean explain;

    private final ImmutableSet<AggregationBuilder> aggregations;

    private final ReturnFields returnFields;

    private ElasticsearchQuery( final Builder builder )
    {
        this.query = builder.queryBuilder;
        this.filter = builder.filter;
        this.indexType = builder.indexType;
        this.indexName = builder.indexName;
        this.sortBuilders = ImmutableSet.copyOf( builder.sortBuilders );
        this.size = builder.size;
        this.from = builder.from;
        this.explain = builder.explain;
        this.aggregations = ImmutableSet.copyOf( builder.aggregations );
        this.returnFields = builder.returnFields;
    }

    public boolean isExplain()
    {
        return explain;
    }

    public ImmutableSet<AggregationBuilder> getAggregations()
    {
        return aggregations;
    }

    public QueryBuilder getQuery()
    {
        return query;
    }

    public FilterBuilder getFilter()
    {
        return filter;
    }

    public String getIndexType()
    {
        return indexType;
    }

    public String getIndexName()
    {
        return this.indexName;
    }

    public static Builder create()
    {
        return new Builder();
    }

    int getFrom()
    {
        return from;
    }

    public int getSize()
    {
        return size;
    }

    boolean doExplain()
    {
        return explain;
    }

    public ReturnFields getReturnFields()
    {
        return returnFields;
    }

    public ImmutableSet<SortBuilder> getSortBuilders()
    {
        return sortBuilders;
    }

    public SearchSourceBuilder toSearchSourceBuilder()
    {

        SearchSourceBuilder builder = new SearchSourceBuilder().
            // field( IndexPaths.ENTITY_KEY ).
                query( this.getQuery() ).
            from( this.getFrom() ).
            size( this.getSize() );

        if ( this.getFilter() != null )
        {
            builder.postFilter( this.getFilter() );
        }

        if ( this.aggregations != null && this.aggregations.size() > 0 )
        {
            aggregations.forEach( builder::aggregation );
        }

        for ( final SortBuilder sortBuilder : this.getSortBuilders() )
        {
            builder.sort( sortBuilder );
        }

        return builder;
    }

    @Override
    public String toString()
    {
        final String sortBuildersAsString = getSortBuildersAsString();

        return "ElasticsearchQuery{" +
            "query=" + query +
            ", filter=" + filter +
            ", indexType=" + indexType +
            ", index=" + indexName +
            ", sortBuilders=" + sortBuildersAsString +
            ", aggregations= " + aggregations +
            '}';
    }

    private String getSortBuildersAsString()
    {
        String sortBuildersAsString = "";

        if ( sortBuilders != null && sortBuilders.size() > 0 )
        {
            Joiner joiner = Joiner.on( "," );
            sortBuildersAsString = joiner.join( getSortBuilders() );
        }
        return sortBuildersAsString;
    }

    public static class Builder
    {
        private QueryBuilder queryBuilder;

        private FilterBuilder filter;

        private String indexType;

        private String indexName;

        private Set<SortBuilder> sortBuilders = Sets.newHashSet();

        private int from = 0;

        private int size = DEFAULT_SIZE;

        private final boolean explain = false;

        private Set<AggregationBuilder> aggregations = Sets.newHashSet();

        private ReturnFields returnFields = ReturnFields.empty();

        public Builder query( final QueryBuilder queryBuilder )
        {
            this.queryBuilder = queryBuilder;
            return this;
        }

        public Builder filter( final FilterBuilder filter )
        {
            this.filter = filter;
            return this;
        }

        public Builder indexType( final String indexType )
        {
            this.indexType = indexType;
            return this;
        }

        public Builder index( final String indexName )
        {
            this.indexName = indexName;
            return this;
        }

        public Builder addSortBuilder( final SortBuilder sortBuilder )
        {
            this.sortBuilders.add( sortBuilder );
            return this;
        }

        public Builder sortBuilders( final Set<SortBuilder> sortBuilders )
        {
            this.sortBuilders = sortBuilders;
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

        public Builder setAggregations( final Set<AggregationBuilder> aggregations )
        {
            this.aggregations = aggregations;
            return this;
        }

        public Builder setReturnFields( final ReturnFields returnFields )
        {
            this.returnFields = returnFields;
            return this;
        }

        public ElasticsearchQuery build()
        {
            return new ElasticsearchQuery( this );
        }

    }


}
