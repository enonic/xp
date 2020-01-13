package com.enonic.xp.elasticsearch.client.impl.cluster.state;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.common.xcontent.DeprecationHandler;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.elasticsearch.client.impl.EsClientResponseBaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GetClusterStateResponseTest
    extends EsClientResponseBaseTest
{

    @Test
    public void testFromResponse_EntityIsNull()
    {
        // prepare
        final Response response = Mockito.mock( Response.class );

        // mock
        Mockito.when( response.getEntity() ).thenReturn( null );

        // test
        IllegalStateException ex = assertThrows( IllegalStateException.class, () -> GetClusterStateResponse.fromResponse( response ) );

        // assert
        assertEquals( "Response body expected but not returned", ex.getMessage() );
    }

    @Test
    public void testFromResponse_ContentTypeNotDefined()
    {
        // prepare
        final Response response = Mockito.mock( Response.class );
        final HttpEntity httpEntity = Mockito.mock( HttpEntity.class );

        // mock
        Mockito.when( response.getEntity() ).thenReturn( httpEntity );
        Mockito.when( httpEntity.getContentType() ).thenReturn( null );

        // test
        IllegalStateException ex = assertThrows( IllegalStateException.class, () -> GetClusterStateResponse.fromResponse( response ) );

        // assert
        assertEquals( "Elasticsearch didn't return the [Content-Type] header, unable to parse response body", ex.getMessage() );
    }

    @Test
    public void testFromResponse_MediaTypeTypeNotFound()
    {
        // prepare
        final Response response = Mockito.mock( Response.class );
        final HttpEntity httpEntity = Mockito.mock( HttpEntity.class );
        final Header contentType = Mockito.mock( Header.class );

        // mock
        Mockito.when( response.getEntity() ).thenReturn( httpEntity );
        Mockito.when( httpEntity.getContentType() ).thenReturn( contentType );
        Mockito.when( contentType.getValue() ).thenReturn( "unknown" );

        // test
        IllegalStateException ex = assertThrows( IllegalStateException.class, () -> GetClusterStateResponse.fromResponse( response ) );

        // assert
        assertEquals( "Unsupported Content-Type: unknown", ex.getMessage() );
    }

    @Test
    public void testFromResponse()
        throws IOException
    {
        // prepare
        final Response response = Mockito.mock( Response.class );
        final HttpEntity httpEntity = Mockito.mock( HttpEntity.class );
        final Header contentTypeHeader = Mockito.mock( Header.class );

        // mock
        Mockito.when( response.getEntity() ).thenReturn( httpEntity );
        Mockito.when( httpEntity.getContentType() ).thenReturn( contentTypeHeader );
        Mockito.when( contentTypeHeader.getValue() ).thenReturn( "application/*" );

        try (final InputStream stream = getResource( "cluster_state.json" ))
        {
            Mockito.when( httpEntity.getContent() ).thenReturn( stream );

            // test
            final GetClusterStateResponse result = GetClusterStateResponse.fromResponse( response );

            // assert
            assertNotNull( result );
        }
    }

    @Test
    public void test()
        throws IOException
    {
        final XContentType xContentType = XContentType.fromMediaTypeOrFormat( "application/json" );

        try (final InputStream stream = getResource( "cluster_state.json" );

             final XContentParser parser = XContentFactory.xContent( xContentType ).
                 createParser( NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, stream ))
        {
            final GetClusterStateResponse result = GetClusterStateResponse.fromXContent( parser );

            assertNotNull( result );
            assertEquals( "gSzRPkbvTva5BJakoxbCeA", result.getMasterNodeId() );
            final List<IndexRoutingTable> indices = result.getRoutingTable().getIndices();
            assertEquals( 2, indices.size() );
            assertEquals( 4, indices.get( 0 ).getShards().size() );
            assertEquals( 2, indices.get( 1 ).getShards().size() );
        }
    }

}
