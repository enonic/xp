package com.enonic.xp.repo.impl.elasticsearch.query;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.xp.node.SearchMode;
import com.enonic.xp.node.SearchOptimizer;
import com.enonic.xp.repo.impl.ReturnFields;

public class ElasticsearchQuery
{
    private static final int DEFAULT_SIZE = 10;

    private final QueryBuilder query;

    private final FilterBuilder filter;

    private final Set<String> indexTypes;

    private final Set<String> indexNames;

    private final ImmutableSet<SortBuilder> sortBuilders;

    private final int from;

    private final int size;

    private final int batchSize;

    private final ImmutableSet<AbstractAggregationBuilder> aggregations;

    private final ReturnFields returnFields;

    private final SearchMode searchMode;

    private final SearchOptimizer searchOptimizer;

    private ElasticsearchQuery( final Builder builder )
    {
        this.query = builder.queryBuilder;
        this.filter = builder.filter;
        this.indexTypes = builder.indexTypes;
        this.indexNames = builder.indexNames;
        this.sortBuilders = ImmutableSet.copyOf( builder.sortBuilders );
        this.size = builder.size;
        this.batchSize = builder.batchSize;
        this.from = builder.from;
        this.aggregations = ImmutableSet.copyOf( builder.aggregations );
        this.returnFields = builder.returnFields;
        this.searchMode = builder.searchMode;
        this.searchOptimizer = builder.searchOptimizer;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ImmutableSet<AbstractAggregationBuilder> getAggregations()
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

    public String[] getIndexTypes()
    {
        return this.indexTypes.toArray( new String[this.indexTypes.size()] );
    }

    public String[] getIndexNames()
    {
        return this.indexNames.toArray( new String[this.indexNames.size()] );
    }

    public int getFrom()
    {
        return from;
    }

    public int getSize()
    {
        return size;
    }

    public int getBatchSize()
    {
        return batchSize;
    }

    public ReturnFields getReturnFields()
    {
        return returnFields;
    }

    public ImmutableSet<SortBuilder> getSortBuilders()
    {
        return sortBuilders;
    }

    public SearchMode getSearchMode()
    {
        return searchMode;
    }

    public SearchOptimizer getSearchOptimizer()
    {
        return searchOptimizer;
    }

    @Override
    public String toString()
    {
        final String sortBuildersAsString = getSortBuildersAsString();

        return "ElasticsearchQuery{" +
            "query=" + query +
            ", size=" + size +
            ", from=" + from +
            ", filter=" + filter +
            ", indexType=" + indexTypes +
            ", index=" + indexNames +
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

        private final Set<String> indexTypes = Sets.newHashSet();

        private final Set<String> indexNames = Sets.newHashSet();

        private List<SortBuilder> sortBuilders = Lists.newArrayList();

        private int from = 0;

        private int size = DEFAULT_SIZE;

        private int batchSize = 15_000;

        private Set<AbstractAggregationBuilder> aggregations = Sets.newHashSet();

        private ReturnFields returnFields = ReturnFields.empty();

        private SearchMode searchMode = SearchMode.SEARCH;

        private SearchOptimizer searchOptimizer = SearchOptimizer.DEFAULT;

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

        public Builder addIndexType( final String indexType )
        {
            this.indexTypes.add( indexType );
            return this;
        }

        public Builder addIndexTypes( final Collection<String> indexTypes )
        {
            this.indexTypes.addAll( indexTypes );
            return this;
        }


        public Builder addIndexName( final String indexName )
        {
            this.indexNames.add( indexName );
            return this;
        }

        public Builder addIndexNames( final Collection<String> indexNames )
        {
            this.indexNames.addAll( indexNames );
            return this;
        }


        public Builder addSortBuilder( final SortBuilder sortBuilder )
        {
            this.sortBuilders.add( sortBuilder );
            return this;
        }

        public Builder sortBuilders( final List<SortBuilder> sortBuilders )
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

        public Builder batchSize( final int batchSize )
        {
            this.batchSize = batchSize;
            return this;
        }

        public Builder setAggregations( final Set<AbstractAggregationBuilder> aggregations )
        {
            this.aggregations = aggregations;
            return this;
        }

        public Builder setReturnFields( final ReturnFields returnFields )
        {
            this.returnFields = returnFields;
            return this;
        }

        public Builder searchMode( final SearchMode searchMode )
        {
            this.searchMode = searchMode;
            return this;
        }

        public Builder searchOptimizer( final SearchOptimizer searchOptimizer )
        {
            this.searchOptimizer = searchOptimizer;
            return this;
        }

        public ElasticsearchQuery build()
        {
            return new ElasticsearchQuery( this );
        }

    }


}
