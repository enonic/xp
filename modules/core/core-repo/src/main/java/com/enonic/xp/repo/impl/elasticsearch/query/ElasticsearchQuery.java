package com.enonic.xp.repo.impl.elasticsearch.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.suggest.SuggestBuilder;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.node.SearchOptimizer;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.SearchPreference;

public class ElasticsearchQuery
{
    private static final int DEFAULT_SIZE = 10;

    private final QueryBuilder query;

    private final QueryBuilder filter;

    private final Set<String> indexTypes;

    private final Set<String> indexNames;

    private final boolean explain;

    private final ImmutableList<SortBuilder> sortBuilders;

    private final int from;

    private final int size;

    private final int batchSize;

    private final ImmutableList<AbstractAggregationBuilder> aggregations;

    private final ImmutableList<SuggestBuilder.SuggestionBuilder> suggestions;

    private final ElasticHighlightQuery highlight;

    private final ReturnFields returnFields;

    private final SearchOptimizer searchOptimizer;

    private final SearchPreference searchPreference;

    private ElasticsearchQuery( final Builder builder )
    {
        this.query = builder.queryBuilder;
        this.filter = builder.filter;
        this.indexTypes = builder.indexTypes;
        this.indexNames = builder.indexNames;
        this.sortBuilders = ImmutableList.copyOf( builder.sortBuilders );
        this.size = builder.size;
        this.batchSize = builder.batchSize;
        this.from = builder.from;
        this.aggregations = ImmutableList.copyOf( builder.aggregations );
        this.suggestions = ImmutableList.copyOf( builder.suggestions );
        this.highlight = builder.highlight;
        this.returnFields = builder.returnFields;
        this.searchOptimizer = builder.searchOptimizer;
        this.explain = builder.explain;
        this.searchPreference = builder.searchPreference;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public List<AbstractAggregationBuilder> getAggregations()
    {
        return aggregations;
    }

    public List<SuggestBuilder.SuggestionBuilder> getSuggestions()
    {
        return suggestions;
    }

    public QueryBuilder getQuery()
    {
        return query;
    }

    public QueryBuilder getFilter()
    {
        return filter;
    }

    public String[] getIndexTypes()
    {
        return this.indexTypes.toArray( new String[0] );
    }

    public String[] getIndexNames()
    {
        return this.indexNames.toArray( new String[0] );
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

    public List<SortBuilder> getSortBuilders()
    {
        return sortBuilders;
    }

    public SearchOptimizer getSearchOptimizer()
    {
        return searchOptimizer;
    }

    public SearchPreference getSearchPreference()
    {
        return searchPreference;
    }

    public ElasticHighlightQuery getHighlight()
    {
        return highlight;
    }

    public boolean isExplain()
    {
        return explain;
    }

    @Override
    public String toString()
    {
        final String sortBuildersAsString = getSortBuildersAsString();

        return "ElasticsearchQuery{" + "query=" + query + ", size=" + size + ", from=" + from + ", filter=" + filter + ", indexType=" +
            indexTypes + ", index=" + indexNames + ", sortBuilders=" + sortBuildersAsString + ", aggregations= " + aggregations +
            ", suggestions= " + suggestions + ", highlight= " + highlight + "}";
    }

    private String getSortBuildersAsString()
    {
        return sortBuilders.stream().map( Objects::toString ).collect( Collectors.joining( "," ) );
    }

    public static class Builder
    {
        private QueryBuilder queryBuilder;

        private QueryBuilder filter;

        private final Set<String> indexTypes = new HashSet<>();

        private final Set<String> indexNames = new HashSet<>();

        private List<SortBuilder> sortBuilders = new ArrayList<>();

        private int from = 0;

        private int size = DEFAULT_SIZE;

        private int batchSize = 15_000;

        private Set<AbstractAggregationBuilder> aggregations = new HashSet<>();

        private Set<SuggestBuilder.SuggestionBuilder> suggestions = new HashSet<>();

        private ElasticHighlightQuery highlight = ElasticHighlightQuery.empty();

        private ReturnFields returnFields = ReturnFields.empty();

        private SearchOptimizer searchOptimizer = SearchOptimizer.DEFAULT;

        private SearchPreference searchPreference;

        private boolean explain = false;

        public Builder query( final QueryBuilder queryBuilder )
        {
            this.queryBuilder = queryBuilder;
            return this;
        }

        public Builder filter( final QueryBuilder filter )
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

        public Builder setSuggestions( final Set<SuggestBuilder.SuggestionBuilder> suggestions )
        {
            this.suggestions = suggestions;
            return this;
        }

        public Builder setHighlight( final ElasticHighlightQuery highlight )
        {
            this.highlight = highlight;
            return this;
        }

        public Builder setReturnFields( final ReturnFields returnFields )
        {
            this.returnFields = returnFields;
            return this;
        }

        public Builder searchOptimizer( final SearchOptimizer searchOptimizer )
        {
            this.searchOptimizer = searchOptimizer;
            return this;
        }

        public Builder searchPreference( final SearchPreference searchPreference )
        {
            this.searchPreference = searchPreference;
            return this;
        }

        public Builder explain( final boolean explain )
        {
            this.explain = explain;
            return this;
        }

        public ElasticsearchQuery build()
        {
            return new ElasticsearchQuery( this );
        }

    }


}
