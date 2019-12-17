package com.enonic.xp.jaxrs.impl;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.jaxrs.impl.json.JsonObjectProvider;
import com.enonic.xp.jaxrs.impl.multipart.MultipartFormReader;
import com.enonic.xp.json.ObjectMapperHelper;
import com.enonic.xp.session.SessionKey;
import com.enonic.xp.session.SimpleSession;
import com.enonic.xp.web.multipart.MultipartService;
import com.enonic.xp.web.servlet.ServletRequestHolder;

public abstract class JaxRsResourceTestSupport
{
    private String basePath = "/";

    private Dispatcher dispatcher;

    protected MultipartService multipartService;

    @BeforeEach
    public final void setUp()
        throws Exception
    {
        this.multipartService = Mockito.mock( MultipartService.class );
        final MultipartFormReader reader = new MultipartFormReader( multipartService );

        this.dispatcher = MockDispatcherFactory.createDispatcher();
        this.dispatcher.getProviderFactory().register( JsonObjectProvider.class );
        this.dispatcher.getProviderFactory().register( reader );
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
        Mockito.when( req.getLocales() ).thenReturn( Collections.enumeration( Collections.singleton( Locale.ENGLISH ) ) );
        ServletRequestHolder.setRequest( req );
    }

    protected abstract Object getResourceInstance();

    protected final void assertJson( final String fileName, final String actualJson )
        throws Exception
    {
        assertStringJson( readFromFile( fileName ), actualJson );
    }

    protected final void assertStringJson( final String expectedJson, final String actualJson )
        throws Exception
    {
        final JsonNode expectedNode = parseJson( expectedJson );
        final JsonNode actualNode = parseJson( actualJson );

        final String expectedStr = toJson( expectedNode );
        final String actualStr = toJson( actualNode );

        Assertions.assertEquals( expectedStr, actualStr );
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

        return Resources.toString( url, StandardCharsets.UTF_8 );
    }

    private String toJson( final Object value )
        throws Exception
    {
        final ObjectMapper mapper = ObjectMapperHelper.create();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString( value );
    }

    protected final void assertArrayEquals( Object[] a1, Object[] a2 )
    {
        Assertions.assertEquals( arrayToString( a1 ), arrayToString( a2 ) );
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

    protected final void setBasePath( final String basePath )
    {
        this.basePath = basePath;
    }

    protected final RestRequestBuilder request()
    {
        return new RestRequestBuilder( this.dispatcher ).path( this.basePath );
    }
}
