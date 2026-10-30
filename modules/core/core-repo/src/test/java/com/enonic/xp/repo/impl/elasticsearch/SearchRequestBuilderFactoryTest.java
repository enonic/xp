package com.enonic.xp.repo.impl.elasticsearch;

import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchAction;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SearchRequestBuilderFactoryTest
{
    private Client client;

    @BeforeEach
    void setUp()
    {
        client = mock( Client.class );
        when( client.prepareSearch( any( String[].class ) ) ).thenAnswer(
            invocation -> new SearchRequestBuilder( client, SearchAction.INSTANCE ) );
    }

    @Test
    void search_without_return_fields_does_not_fetch_source()
    {
        final ElasticsearchQuery query = ElasticsearchQuery.create().query( QueryBuilders.matchAllQuery() ).build();

        final Map<String, Object> request = source( factory( query ).createSearchRequest() );

        assertEquals( false, request.get( "_source" ) );
    }

    @Test
    void scroll_without_return_fields_does_not_fetch_source()
    {
        final ElasticsearchQuery query = ElasticsearchQuery.create().query( QueryBuilders.matchAllQuery() ).build();

        final Map<String, Object> request = source( factory( query ).createScrollRequest( TimeValue.timeValueMinutes( 1 ) ) );

        assertEquals( false, request.get( "_source" ) );
    }

    @Test
    void search_with_return_fields_requests_fields_only()
    {
        final ElasticsearchQuery query = ElasticsearchQuery.create()
            .query( QueryBuilders.matchAllQuery() )
            .setReturnFields( ReturnFields.from( NodeIndexPath.ID, NodeIndexPath.SOURCE ) )
            .build();

        final Map<String, Object> request = source( factory( query ).createSearchRequest() );

        assertEquals( List.of( "_id", "_source" ), request.get( "fields" ) );
        assertFalse( request.containsKey( "_source" ) );
    }

    private SearchRequestBuilderFactory factory( final ElasticsearchQuery query )
    {
        return SearchRequestBuilderFactory.newFactory().query( query ).client( client ).build();
    }

    private static Map<String, Object> source( final SearchRequestBuilder builder )
    {
        return XContentHelper.convertToMap( new BytesArray( builder.toString() ), false ).v2();
    }
}
