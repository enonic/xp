package com.enonic.xp.jaxrs.impl;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.spi.Dispatcher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextAccessorSupport;
import com.enonic.xp.context.LocalScope;
import com.enonic.xp.core.internal.json.ObjectMapperHelper;
import com.enonic.xp.jaxrs.impl.multipart.MultipartFormReader;
import com.enonic.xp.session.Session;
import com.enonic.xp.session.SessionMock;
import com.enonic.xp.web.multipart.MultipartService;

public abstract class JaxRsResourceTestSupport
{
    private static final ObjectMapper MAPPER = ObjectMapperHelper.create();

    private static final ObjectWriter OBJECT_WRITER = MAPPER.writerWithDefaultPrettyPrinter();

    private final String basePath;

    private Dispatcher dispatcher;

    protected MultipartService multipartService;

    public JaxRsResourceTestSupport()
    {
        this( "" );
    }

    public JaxRsResourceTestSupport( final String basePath )
    {
        this.basePath = "/" + basePath;
    }

    @BeforeEach
    final void setUp()
    {
        this.multipartService = Mockito.mock( MultipartService.class );

        this.dispatcher = MockDispatcherFactory.createDispatcher();
        this.dispatcher.getProviderFactory().register( new JacksonJsonProvider( MAPPER ) );
        this.dispatcher.getProviderFactory().register( new MultipartFormReader( multipartService ) );
        this.dispatcher.getRegistry().addSingletonResource( getResourceInstance() );

        mockCurrentContextHttpRequest();

        ContextAccessorSupport.getInstance().remove();

        ContextAccessor.current().getLocalScope().setSession( new SessionMock() );
    }

    @AfterEach
    final void destroy()
    {
        final LocalScope localScope = ContextAccessor.current().getLocalScope();

        if ( localScope != null )
        {
            Session session = localScope.getSession();

            if ( session != null )
            {
                session.invalidate();
            }
        }
    }

    private void mockCurrentContextHttpRequest()
    {
        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        Mockito.when( req.getScheme() ).thenReturn( "http" );
        Mockito.when( req.getServerName() ).thenReturn( "localhost" );
        Mockito.when( req.getLocalPort() ).thenReturn( 80 );
        Mockito.when( req.getLocales() ).thenReturn( Collections.enumeration( Collections.singleton( Locale.ENGLISH ) ) );
    }

    protected abstract Object getResourceInstance();

    protected final void assertJson( final String fileName, final String actualJson )
        throws Exception
    {
        assertStringJson( readFromFile( fileName ), actualJson );
    }

    protected final void assertStringJson( final String expectedJson, final String actualJson )
    {
        final JsonNode actualNode;
        try
        {
            actualNode = MAPPER.readTree( actualJson );
        }
        catch ( JsonProcessingException e )
        {
            Assertions.fail( "actualJson is not a JSON: " + actualJson, e );
            return;
        }

        final JsonNode expectedNode;
        try
        {
            expectedNode = MAPPER.readTree( expectedJson );
            final String expectedStr = OBJECT_WRITER.writeValueAsString( expectedNode );
            final String actualStr = OBJECT_WRITER.writeValueAsString( actualNode );
            Assertions.assertEquals( expectedStr, actualStr );
        }
        catch ( JsonProcessingException e )
        {
            throw new RuntimeException( e );
        }
    }

    protected String readFromFile( final String fileName )
        throws Exception
    {
        final InputStream stream =
            Objects.requireNonNull( getClass().getResourceAsStream( fileName ), "Resource file [" + fileName + "] not found" );
        try (stream)
        {
            return new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }
    }

    protected final RestRequestBuilder request()
    {
        return new RestRequestBuilder( this.dispatcher ).path( this.basePath );
    }
}
