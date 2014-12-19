package com.enonic.wem.admin.rest.resource;

import java.net.URL;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.junit.Before;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import junit.framework.Assert;

import com.enonic.wem.admin.json.ObjectMapperHelper;
import com.enonic.wem.admin.rest.multipart.MultipartFormReader;
import com.enonic.wem.admin.rest.provider.JsonObjectProvider;
import com.enonic.wem.admin.rest.provider.JsonSerializableProvider;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.session.SessionKey;
import com.enonic.wem.api.session.SimpleSession;
import com.enonic.xp.web.servlet.ServletRequestHolder;

import static org.junit.Assert.*;

public abstract class AbstractResourceTest
{
    private Dispatcher dispatcher;

    @Before
    public final void setUp()
        throws Exception
    {
        this.dispatcher = MockDispatcherFactory.createDispatcher();
        this.dispatcher.getProviderFactory().register( JsonObjectProvider.class );
        this.dispatcher.getProviderFactory().register( JsonSerializableProvider.class );
        this.dispatcher.getProviderFactory().register( MultipartFormReader.class );
        this.dispatcher.getRegistry().addSingletonResource( getResourceInstance() );

        mockCurrentContextHttpRequest();

        ContextAccessor.INSTANCE.remove();

        final SimpleSession session = new SimpleSession( SessionKey.generate() );
        ContextAccessor.current().getLocalScope().setSession( session );
    }

    private void mockCurrentContextHttpRequest()
    {
        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        Mockito.when( req.getScheme() ).thenReturn( "http" );
        Mockito.when( req.getServerName() ).thenReturn( "localhost" );
        Mockito.when( req.getLocalPort() ).thenReturn( 80 );
        ServletRequestHolder.setRequest( req );
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

    protected final RestRequestBuilder request()
    {
        return new RestRequestBuilder( this.dispatcher );
    }
}
