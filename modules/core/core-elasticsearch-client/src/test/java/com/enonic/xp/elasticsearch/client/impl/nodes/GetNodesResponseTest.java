package com.enonic.xp.elasticsearch.client.impl.nodes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GetNodesResponseTest
{

    @Test
    public void testFromResponse_EntityIsNull()
    {
        // prepare
        final Response response = Mockito.mock( Response.class );

        // mock
        Mockito.when( response.getEntity() ).thenReturn( null );

        // test
        IllegalStateException ex = assertThrows( IllegalStateException.class, () -> GetNodesResponse.fromResponse( response ) );

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
        IllegalStateException ex = assertThrows( IllegalStateException.class, () -> GetNodesResponse.fromResponse( response ) );

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
        IllegalStateException ex = assertThrows( IllegalStateException.class, () -> GetNodesResponse.fromResponse( response ) );

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

        try (final InputStream stream = getResource( "nodes.json" ))
        {
            Mockito.when( httpEntity.getContent() ).thenReturn( stream );

            // test
            final GetNodesResponse result = GetNodesResponse.fromResponse( response );

            // assert
            assertNotNull( result );
        }
    }

    @Test
    public void testFromXContent()
        throws IOException
    {
        final List<String> roles = Arrays.asList( "ingest", "master", "data", "ml" );

        final XContentType xContentType = XContentType.fromMediaTypeOrFormat( "application/json" );

        try (final InputStream stream = getResource( "nodes.json" );

             final XContentParser parser = XContentFactory.xContent( xContentType ).
                 createParser( NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, stream ))
        {
            final GetNodesResponse result = GetNodesResponse.fromXContent( parser );

            assertNotNull( result );
            assertEquals( "elasticsearch", result.getClusterName() );

            final List<Node> nodes = result.getNodes();
            assertFalse( nodes.isEmpty() );
            assertEquals( 2, nodes.size() );

            // assert the first node
            assertEquals( "gSzRPkbvTva5BJakoxbCeA", nodes.get( 0 ).getId() );
            assertEquals( "127.0.0.1:9300", nodes.get( 0 ).getAddress() );
            assertEquals( "7.4.0", nodes.get( 0 ).getVersion() );
            assertEquals( "127.0.0.1", nodes.get( 0 ).getHostName() );
            assertEquals( "es01", nodes.get( 0 ).getName() );
            assertEquals( roles, nodes.get( 0 ).getRoles() );

            // assert the second node
            assertEquals( "xOOpT6oeQ1WVKm9cYISRIw", nodes.get( 1 ).getId() );
            assertEquals( "127.0.0.2:9300", nodes.get( 1 ).getAddress() );
            assertEquals( "7.4.0", nodes.get( 1 ).getVersion() );
            assertEquals( "127.0.0.2", nodes.get( 1 ).getHostName() );
            assertEquals( "es02", nodes.get( 1 ).getName() );
            assertEquals( roles, nodes.get( 1 ).getRoles() );
        }
    }

    private InputStream getResource( final String fileName )
    {
        final InputStream inputStream = this.getClass().getResourceAsStream( fileName );

        if ( inputStream == null )
        {
            throw new IllegalArgumentException( "Resource [" + fileName + "] not found relative to: " + this.getClass() );
        }

        return inputStream;
    }

}
