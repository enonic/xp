package com.enonic.wem.admin.rest.resource;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.LowLevelAppDescriptor;
import com.sun.jersey.test.framework.spi.container.inmemory.InMemoryTestContainerFactory;

import junit.framework.Assert;

import com.enonic.wem.admin.json.ObjectMapperHelper;
import com.enonic.wem.admin.rest.provider.JsonObjectProvider;
import com.enonic.wem.admin.rest.provider.JsonSerializableProvider;
import com.enonic.wem.core.web.servlet.ServletRequestHolder;

import static org.junit.Assert.*;

public abstract class AbstractResourceTest
    extends JerseyTest
{
    public AbstractResourceTest()
    {
        super( new InMemoryTestContainerFactory() );
    }

    @Override
    protected AppDescriptor configure()
    {
        mockCurrentContextHttpRequest();

        final DefaultResourceConfig config = new DefaultResourceConfig();
        configure( config );

        return new LowLevelAppDescriptor.Builder( config ).build();
    }

    protected void mockCurrentContextHttpRequest()
    {
        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        Mockito.when( req.getScheme() ).thenReturn( "http" );
        Mockito.when( req.getServerName() ).thenReturn( "localhost" );
        Mockito.when( req.getLocalPort() ).thenReturn( 80 );
        ServletRequestHolder.setRequest( req );
    }

    private void configure( final DefaultResourceConfig config )
    {
        config.getClasses().add( JsonObjectProvider.class );
        config.getClasses().add( JsonSerializableProvider.class );
        config.getSingletons().add( getResourceInstance() );
    }

    protected abstract Object getResourceInstance();

    protected final void assertJson( final String fileName, final String actualJson )
        throws Exception
    {
        final JsonNode expectedNode = parseJson( readFromFile( fileName ) );
        final JsonNode actualNode = parseJson( actualJson );

        final String expectedStr = toJson( expectedNode );
        final String actualStr = toJson( actualNode );

        assertEquals( expectedStr, actualStr );
    }

    protected JsonNode parseJson( final String json )
        throws Exception
    {
        final ObjectMapper mapper = ObjectMapperHelper.create();
        return mapper.readTree( json );
    }

    protected String readFromFile( final String fileName )
        throws Exception
    {
        final URL url = getClass().getResource( fileName );
        if ( url == null )
        {
            throw new IllegalArgumentException( "Resource file [" + fileName + "] not found" );
        }

        return Resources.toString( url, Charsets.UTF_8 );
    }

    private String toJson( final Object value )
        throws Exception
    {
        final ObjectMapper mapper = ObjectMapperHelper.create();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString( value );
    }

    protected final void assertUnorderedListEquals( Object[] a1, List a2 )
    {
        assertArrayEquals( a1, a2.toArray() );
    }

    protected final void assertListEquals( Object[] a1, List a2 )
    {
        assertArrayEquals( a1, a2.toArray() );
    }

    protected final void assertArrayEquals( Object[] a1, Object[] a2 )
    {
        Assert.assertEquals( arrayToString( a1 ), arrayToString( a2 ) );
    }

    protected final String arrayToString( Object[] a )
    {
        final StringBuilder result = new StringBuilder( "[" );

        for ( int i = 0; i < a.length; i++ )
        {
            result.append( i ).append( ": " ).append( a[i] );
            if ( i < a.length - 1 )
            {
                result.append( ", " );
            }
        }

        result.append( "]" );

        return result.toString();
    }

    protected final byte[] createMultipart( final String name, final String fileName, final byte[] data, final MediaType type )
        throws Exception
    {
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody( name, data, ContentType.create( type.toString() ), fileName );

        System.out.println( builder.build().getContentType() );
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        builder.build().writeTo( out );
        return out.toByteArray();
    }

    protected final RestRequestBuilder request()
    {
        return new RestRequestBuilder( resource() );
    }
}

