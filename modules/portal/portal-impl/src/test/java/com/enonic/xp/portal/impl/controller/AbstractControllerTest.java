package com.enonic.xp.portal.impl.controller;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.net.MediaType;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.impl.postprocess.PostProcessorImpl;
import com.enonic.xp.portal.impl.script.PortalScriptServiceImpl;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.script.ScriptFixturesFacade;
import com.enonic.xp.script.runtime.ScriptRuntimeFactory;
import com.enonic.xp.util.Version;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public abstract class AbstractControllerTest
{
    private static final ObjectMapper MAPPER = new ObjectMapper().enable( SerializationFeature.INDENT_OUTPUT );

    protected PostProcessorImpl postProcessor;

    private ControllerScriptFactoryImpl factory;

    protected PortalRequest portalRequest;

    protected PortalResponse portalResponse;

    protected ResourceService resourceService;

    @BeforeEach
    void setup()
    {
        this.portalRequest = new PortalRequest();
        this.portalRequest.setMode( RenderMode.LIVE );
        this.portalResponse = PortalResponse.create().build();

        final Application application = Mockito.mock( Application.class );
        when( application.getKey() ).thenReturn( ApplicationKey.from( "myapplication" ) );
        when( application.getVersion() ).thenReturn( Version.emptyVersion );
        when( application.getClassLoader() ).thenReturn( getClass().getClassLoader() );
        when( application.isStarted() ).thenReturn( true );
        when( application.getConfig() ).thenReturn( ConfigBuilder.create().build() );

        this.resourceService = Mockito.mock( ResourceService.class );
        when( resourceService.getResource( Mockito.any() ) ).thenAnswer( invocation -> {
            final ResourceKey resourceKey = invocation.getArgument( 0 );
            final URL resourceUrl =
                AbstractControllerTest.class.getResource( "/" + resourceKey.getApplicationKey() + resourceKey.getPath() );
            return new UrlResource( resourceKey, resourceUrl );
        } );

        final ScriptRuntimeFactory runtimeFactory =
            ScriptFixturesFacade.getInstance().scriptRuntimeFactory( resourceService, null, application );

        final PortalScriptServiceImpl scriptService = new PortalScriptServiceImpl( runtimeFactory );
        scriptService.initialize();

        this.factory = new ControllerScriptFactoryImpl( scriptService );

        this.postProcessor = new PostProcessorImpl();
    }

    protected final void execute( final String script )
    {
        final ControllerScript controllerScript = this.factory.fromScript( ResourceKey.from( script ) );
        this.portalResponse = controllerScript.execute( this.portalRequest );
    }

    protected final String getResponseAsString()
    {
        final Object body = this.portalResponse.getBody();
        if ( body instanceof Map || body instanceof List )
        {
            try
            {
                return MAPPER.writeValueAsString( body );
            }
            catch ( final Exception e )
            {
                throw new RuntimeException( e );
            }
        }
        return ( body != null ) ? body.toString() : null;
    }

    protected final void assertBodyJson( final String name )
    {
        assertEquals( MediaType.JSON_UTF_8, this.portalResponse.getContentType() );
        final String resource = "/" + getClass().getName().replace( '.', '/' ) + "-" + name;
        final URL url = getClass().getResource( resource );

        assertNotNull( url, "File [" + resource + "] not found" );
        final String expectedStr;
        final String actualStr;
        try
        {
            final JsonNode expectedJson = MAPPER.readTree( url );
            JsonNode actualJson = MAPPER.readTree( getResponseAsString() );
            expectedStr = MAPPER.writeValueAsString( expectedJson );
            actualStr = MAPPER.writeValueAsString( actualJson );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }

        assertEquals( expectedStr, actualStr );
    }
}
