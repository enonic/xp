package com.enonic.xp.repo.impl.elasticsearch;

import org.elasticsearch.action.search.SearchAction;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.suggest.SuggestBuilder;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.node.SearchOptimizer;
import com.enonic.xp.query.highlight.constants.Encoder;
import com.enonic.xp.query.highlight.constants.Fragmenter;
import com.enonic.xp.query.highlight.constants.Order;
import com.enonic.xp.query.highlight.constants.TagsSchema;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticHighlightQuery;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;

public class SearchRequestBuilderFactory
{
    private static final String HIGHLIGHTER_TYPE = "plain";

    private final int resolvedSize;

    private final ElasticsearchQuery query;

    private final Client client;

    private SearchRequestBuilderFactory( final Builder builder )
    {
        resolvedSize = builder.resolvedSize;
        query = builder.query;
        client = builder.client;
    }

    public static Builder newFactory()
    {
        return new Builder();
    }

    public SearchRequestBuilder create()
    {
        final SearchType searchType =
            query.getSearchOptimizer().equals( SearchOptimizer.ACCURACY ) ? SearchType.DFS_QUERY_THEN_FETCH : SearchType.DEFAULT;

        final SearchRequestBuilder searchRequestBuilder = new SearchRequestBuilder( this.client, SearchAction.INSTANCE ).
            setExplain( query.isExplain() ).
            setIndices( query.getIndexNames() ).
            setTypes( query.getIndexTypes() ).
            setSearchType( searchType ).
            setQuery( query.getQuery() ).
            setPostFilter( query.getFilter() ).
            setFrom( query.getFrom() ).
            setSize( resolvedSize );

        query.getSortBuilders().forEach( searchRequestBuilder::addSort );

        query.getAggregations().forEach( searchRequestBuilder::addAggregation );

        final SuggestBuilder suggestBuilder = new SuggestBuilder();
        query.getSuggestions().forEach( suggestionBuilder -> suggestBuilder.addSuggestion( suggestionBuilder.field(), suggestionBuilder ) );
        searchRequestBuilder.suggest( suggestBuilder );

        if ( query.getHighlight() != null )
        {
            setHighlightSettings( searchRequestBuilder, query.getHighlight() );
        }

        if ( query.getReturnFields() != null && query.getReturnFields().isNotEmpty() )
        {
            searchRequestBuilder.storedFields( query.getReturnFields().getReturnFieldNames() );
        }

        return searchRequestBuilder;
    }

    private SearchRequestBuilder setHighlightSettings( final SearchRequestBuilder builder, final ElasticHighlightQuery highlight )
    {
        highlight.getFields().forEach( field -> builder.addStoredField( field.name() ) );

        final Encoder encoder = highlight.getEncoder();
        final TagsSchema tagsSchema = highlight.getTagsSchema();
        final Fragmenter fragmenter = highlight.getFragmenter();
        final Integer fragmentSize = highlight.getFragmentSize();
        final Integer noMatchSize = highlight.getNoMatchSize();
        final Integer numOfFragments = highlight.getNumOfFragments();
        final Order order = highlight.getOrder();
        final ImmutableList<String> preTags = highlight.getPreTags();
        final ImmutableList<String> postTags = highlight.getPostTags();
        final Boolean requireFieldMatch = highlight.getRequireFieldMatch();

        final HighlightBuilder highlightBuilder = new HighlightBuilder();

        highlightBuilder.highlighterType( HIGHLIGHTER_TYPE );
        if ( encoder != null )
        {
            highlightBuilder.encoder( encoder.value() );
        }
        if ( fragmenter != null )
        {
            highlightBuilder.fragmenter( fragmenter.value() );
        }
        if ( fragmentSize != null )
        {
            highlightBuilder.fragmentSize( fragmentSize );
        }
        if ( noMatchSize != null )
        {
            highlightBuilder.noMatchSize( noMatchSize );
        }
        if ( numOfFragments != null )
        {
            highlightBuilder.numOfFragments( numOfFragments );
        }
        if ( order != null )
        {
            highlightBuilder.order( order.value() );
        }
        if ( preTags != null && !preTags.isEmpty() )
        {
            highlightBuilder.preTags( preTags.toArray( new String[preTags.size()] ) );
        }
        if ( postTags != null && !postTags.isEmpty() )
        {
            highlightBuilder.postTags( postTags.toArray( new String[postTags.size()] ) );
        }
        if ( requireFieldMatch != null )
        {
            highlightBuilder.requireFieldMatch( requireFieldMatch );
        }
        if ( tagsSchema != null )
        {
            highlightBuilder.tagsSchema( tagsSchema.value() );
        }

        builder.highlighter( highlightBuilder );

        return builder;
    }


    public static final class Builder
    {
        private int resolvedSize;

        private ElasticsearchQuery query;

        private Client client;

        private Builder()
        {
        }

        public Builder resolvedSize( final int resolvedSize )
        {
            this.resolvedSize = resolvedSize;
            return this;
        }

        public Builder query( final ElasticsearchQuery query )
        {
            this.query = query;
            return this;
        }

        public Builder client( final Client client )
        {
            this.client = client;
            return this;
        }

        public SearchRequestBuilderFactory build()
        {
            return new SearchRequestBuilderFactory( this );
        }
    }
}
