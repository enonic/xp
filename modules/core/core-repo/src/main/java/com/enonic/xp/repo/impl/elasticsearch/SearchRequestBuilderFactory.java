package com.enonic.xp.repo.impl.elasticsearch;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import com.enonic.xp.node.SearchOptimizer;
import com.enonic.xp.node.SearchPreference;
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

    private final TimeValue scrollTime;

    private SearchRequestBuilderFactory( final Builder builder )
    {
        resolvedSize = builder.resolvedSize;
        query = builder.query;
        client = builder.client;
        scrollTime = builder.scrollTime;
    }

    public static Builder newFactory()
    {
        return new Builder();
    }

    public SearchRequestBuilder createScrollRequest()
    {
        final SearchRequestBuilder searchRequestBuilder = initRequestBuilder();

        searchRequestBuilder.
            setScroll( scrollTime ).
            setSearchType( query.getSortBuilders().isEmpty() ? SearchType.SCAN : SearchType.DEFAULT );

        return searchRequestBuilder;
    }

    public SearchRequestBuilder createSearchRequest()
    {
        final SearchRequestBuilder searchRequestBuilder = initRequestBuilder();

        searchRequestBuilder.setExplain( query.isExplain() )
            .setSearchType(
                query.getSearchOptimizer().equals( SearchOptimizer.ACCURACY ) ? SearchType.DFS_QUERY_THEN_FETCH : SearchType.DEFAULT )
            .setPreference(
                query.getSearchPreference() != null ? query.getSearchPreference().toString() : SearchPreference.LOCAL.toString() );

        query.getAggregations().forEach( searchRequestBuilder::addAggregation );
        query.getSuggestions().forEach( searchRequestBuilder::addSuggestion );

        return searchRequestBuilder;
    }

    private SearchRequestBuilder initRequestBuilder()
    {
        final SearchRequestBuilder searchRequestBuilder = client.prepareSearch( query.getIndexNames() );

        searchRequestBuilder.setTypes( query.getIndexTypes() ).
            setQuery( query.getQuery() ).
            setPostFilter( query.getFilter() ).
            setFrom( query.getFrom() ).
            setSize( resolvedSize );

        if ( query.getReturnFields() != null && query.getReturnFields().isNotEmpty() )
        {
            searchRequestBuilder.addFields( query.getReturnFields().getReturnFieldNames() );
        }

        if ( query.getHighlight() != null )
        {
            setHighlightSettings( searchRequestBuilder, query.getHighlight() );
        }

        query.getSortBuilders().forEach( searchRequestBuilder::addSort );

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

        private TimeValue scrollTime;

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

        public Builder scrollTime( final TimeValue scrollTime )
        {
            this.scrollTime = scrollTime;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( query, "query must be set." );
            Preconditions.checkNotNull( client, "client must be set." );
            Preconditions.checkNotNull( resolvedSize, "resolvedSize must be set." );
        }

        public SearchRequestBuilderFactory build()
        {
            validate();
            return new SearchRequestBuilderFactory( this );
        }
    }
}
