package com.enonic.xp.repo.impl.elasticsearch;

import org.elasticsearch.action.search.SearchAction;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.node.SearchOptimizer;
import com.enonic.xp.query.highlight.constants.Encoder;
import com.enonic.xp.query.highlight.constants.Fragmenter;
import com.enonic.xp.query.highlight.constants.Order;
import com.enonic.xp.query.highlight.constants.TagsSchema;
import com.enonic.xp.repo.impl.SearchPreference;
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
            setPreference( SearchPreference.LOCAL.getName() ).
            setSize( resolvedSize );

        query.getSortBuilders().forEach( searchRequestBuilder::addSort );

        query.getAggregations().forEach( searchRequestBuilder::addAggregation );
        query.getSuggestions().forEach( searchRequestBuilder::addSuggestion );

        if ( query.getHighlight() != null )
        {
            setHighlightSettings( searchRequestBuilder, query.getHighlight() );
        }

        if ( query.getReturnFields() != null && query.getReturnFields().isNotEmpty() )
        {
            searchRequestBuilder.addFields( query.getReturnFields().getReturnFieldNames() );
        }

        return searchRequestBuilder;
    }

    private SearchRequestBuilder setHighlightSettings( final SearchRequestBuilder builder, final ElasticHighlightQuery highlight )
    {
        highlight.getFields().forEach( builder::addHighlightedField );

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

        builder.setHighlighterType( HIGHLIGHTER_TYPE );
        if ( encoder != null )
        {
            builder.setHighlighterEncoder( encoder.value() );
        }
        if ( fragmenter != null )
        {
            builder.setHighlighterFragmenter( fragmenter.value() );
        }
        if ( fragmentSize != null )
        {
            builder.setHighlighterFragmentSize( fragmentSize );
        }
        if ( noMatchSize != null )
        {
            builder.setHighlighterNoMatchSize( noMatchSize );
        }
        if ( numOfFragments != null )
        {
            builder.setHighlighterNumOfFragments( numOfFragments );
        }
        if ( order != null )
        {
            builder.setHighlighterOrder( order.value() );
        }
        if ( preTags != null && !preTags.isEmpty() )
        {
            builder.setHighlighterPreTags( preTags.toArray( new String[0] ) );
        }
        if ( postTags != null && !postTags.isEmpty() )
        {
            builder.setHighlighterPostTags( postTags.toArray( new String[0] ) );
        }
        if ( requireFieldMatch != null )
        {
            builder.setHighlighterRequireFieldMatch( requireFieldMatch );
        }
        if ( tagsSchema != null )
        {
            builder.setHighlighterTagsSchema( tagsSchema.value() );
        }

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
