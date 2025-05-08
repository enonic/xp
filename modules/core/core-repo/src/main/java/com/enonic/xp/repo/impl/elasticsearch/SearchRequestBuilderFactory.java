package com.enonic.xp.repo.impl.elasticsearch;

import java.util.List;
import java.util.Objects;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.sort.FieldSortBuilder;

import com.google.common.base.Preconditions;

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

    private final ElasticsearchQuery query;

    private final Client client;

    private SearchRequestBuilderFactory( final Builder builder )
    {
        query = builder.query;
        client = builder.client;
    }

    public static Builder newFactory()
    {
        return new Builder();
    }

    public SearchRequestBuilder createScrollRequest( final TimeValue keepAlive )
    {
        final SearchRequestBuilder searchRequestBuilder = initRequestBuilder();
        searchRequestBuilder.setSize( query.getBatchSize() );

        if ( query.getSortBuilders().isEmpty() )
        {
            searchRequestBuilder.addSort( new FieldSortBuilder( "_doc" ) );
        }
        searchRequestBuilder.setScroll( keepAlive );

        query.getAggregations().forEach( searchRequestBuilder::addAggregation );
        query.getSuggestions().forEach( searchRequestBuilder::addSuggestion );

        return searchRequestBuilder;
    }

    public SearchRequestBuilder createSearchRequest()
    {
        final SearchRequestBuilder searchRequestBuilder = initRequestBuilder();
        searchRequestBuilder.setSize( query.getSize() );

        searchRequestBuilder.setExplain( query.isExplain() )
            .setSearchType( SearchOptimizer.ACCURACY == query.getSearchOptimizer() ? SearchType.DFS_QUERY_THEN_FETCH : SearchType.DEFAULT );

        query.getAggregations().forEach( searchRequestBuilder::addAggregation );
        query.getSuggestions().forEach( searchRequestBuilder::addSuggestion );

        return searchRequestBuilder;
    }

    public SearchRequestBuilder createCountRequest()
    {
        return client.prepareSearch( query.getIndexNames() )
            .setTypes( query.getIndexTypes() )
            .setQuery( query.getQuery() )
            .setSize( 0 )
            .setPreference( Objects.requireNonNullElse( query.getSearchPreference(), SearchPreference.LOCAL ).getName() );
    }

    private SearchRequestBuilder initRequestBuilder()
    {
        final SearchRequestBuilder searchRequestBuilder = client.prepareSearch( query.getIndexNames() );

        searchRequestBuilder.setTypes( query.getIndexTypes() )
            .setQuery( query.getQuery() )
            .setPostFilter( query.getFilter() )
            .setFrom( query.getFrom() )
            .setPreference( Objects.requireNonNullElse( query.getSearchPreference(), SearchPreference.LOCAL ).getName() );

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
        final List<String> preTags = highlight.getPreTags();
        final List<String> postTags = highlight.getPostTags();
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
        private ElasticsearchQuery query;

        private Client client;

        private Builder()
        {
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

        private void validate()
        {
            Preconditions.checkNotNull( query, "query must be set." );
            Preconditions.checkNotNull( client, "client must be set." );
        }

        public SearchRequestBuilderFactory build()
        {
            validate();
            return new SearchRequestBuilderFactory( this );
        }
    }
}
