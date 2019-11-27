package com.enonic.xp.repo.impl.elasticsearch;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.Strings;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.suggest.SuggestBuilder;

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

    private final String preference;

    private final String scrollTimeout;

    private final ElasticsearchQuery query;

    private SearchRequestBuilderFactory( final Builder builder )
    {
        this.resolvedSize = builder.resolvedSize;
        this.query = builder.query;
        this.preference = builder.preference;
        this.scrollTimeout = builder.scrollTimeout;
    }

    public static Builder newFactory()
    {
        return new Builder();
    }

    public SearchRequest create()
    {
        final SearchRequest searchRequest = createDefaultRequest();

        searchRequest.source().from( query.getFrom() );

        return searchRequest;
    }

    public SearchRequest createScrollRequest()
    {
        final SearchRequest searchRequest = createDefaultRequest();

        if ( scrollTimeout != null )
        {
            searchRequest.scroll( scrollTimeout );
        }

        return searchRequest;
    }

    private SearchRequest createDefaultRequest()
    {
        final SearchType searchType =
            query.getSearchOptimizer().equals( SearchOptimizer.ACCURACY ) ? SearchType.DFS_QUERY_THEN_FETCH : SearchType.DEFAULT;

        final SearchRequest searchRequest = new SearchRequest().
            searchType( searchType ).
            indices( query.getIndexNames() ).
            source( prepareSearchSource() ).
            preference( SearchPreference.LOCAL.getName() );

        return searchRequest;
    }

    private SearchSourceBuilder prepareSearchSource()
    {
        final SearchSourceBuilder result = new SearchSourceBuilder().
            explain( query.isExplain() ).
            query( query.getQuery() ).
            postFilter( query.getFilter() ).
            size( resolvedSize );

        query.getSortBuilders().forEach( result::sort );
        query.getAggregations().forEach( result::aggregation );

        final SuggestBuilder suggestBuilder = new SuggestBuilder();
        query.getSuggestions().forEach( suggestionBuilder -> suggestBuilder.addSuggestion( suggestionBuilder.field(), suggestionBuilder ) );
        result.suggest( suggestBuilder );

        if ( query.getHighlight() != null )
        {
            setHighlightSettings( result, query.getHighlight() );
        }

        if ( query.getReturnFields() != null && query.getReturnFields().isNotEmpty() )
        {
            result.fetchSource( query.getReturnFields().getReturnFieldNames(), Strings.EMPTY_ARRAY );
        }

        return result;
    }

    private SearchSourceBuilder setHighlightSettings( final SearchSourceBuilder builder, final ElasticHighlightQuery highlight )
    {
//        TODO Upgrade ES
//        builder.fetchSource( new FetchSourceContext( true,
//                                                     (String[]) highlight.getFields().stream().map( HighlightBuilder.Field::name ).collect(
//                                                         Collectors.toList() ).toArray(), Strings.EMPTY_ARRAY ) );

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

        private String preference;

        private String scrollTimeout;

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

        public Builder preference( final String preference )
        {
            this.preference = preference;
            return this;
        }

        public Builder scrollTimeout( final String scrollTimeout )
        {
            this.scrollTimeout = scrollTimeout;
            return this;
        }

        public SearchRequestBuilderFactory build()
        {
            return new SearchRequestBuilderFactory( this );
        }
    }
}
