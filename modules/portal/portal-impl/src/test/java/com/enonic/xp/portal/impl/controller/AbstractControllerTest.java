package com.enonic.xp.portal.impl.controller;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.net.MediaType;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.impl.postprocess.PostProcessorImpl;
import com.enonic.xp.portal.impl.script.PortalScriptServiceImpl;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.script.ScriptFixturesFacade;
import com.enonic.xp.script.impl.async.ScriptAsyncService;
import com.enonic.xp.script.runtime.ScriptRuntimeFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public abstract class AbstractControllerTest
{
    private static final ObjectMapper MAPPER = new ObjectMapper().
        enable( SerializationFeature.INDENT_OUTPUT );

    protected PostProcessorImpl postProcessor;

    private ControllerScriptFactoryImpl factory;

    protected PortalRequest portalRequest;

    protected PortalResponse portalResponse;

    protected ResourceService resourceService;

    @BeforeEach
    public void setup()
        throws Exception
    {
        this.portalRequest = new PortalRequest();
        this.portalResponse = PortalResponse.create().build();

        final BundleContext bundleContext = Mockito.mock( BundleContext.class );

        final Bundle bundle = Mockito.mock( Bundle.class );
        when( bundle.getBundleContext() ).thenReturn( bundleContext );
        when( bundle.getHeaders() ).thenReturn( new Hashtable<>() );

        final Application application = Mockito.mock( Application.class );
        when( application.getBundle() ).thenReturn( bundle );
        when( application.getClassLoader() ).thenReturn( getClass().getClassLoader() );
        when( application.isStarted() ).thenReturn( true );
        when( application.getConfig() ).thenReturn( ConfigBuilder.create().build() );

        final ApplicationService applicationService = Mockito.mock( ApplicationService.class );
        when( applicationService.getInstalledApplication( ApplicationKey.from( "myapplication" ) ) ).thenReturn( application );

        this.resourceService = Mockito.mock( ResourceService.class );
        when( resourceService.getResource( Mockito.any() ) ).thenAnswer( invocation -> {
            final ResourceKey resourceKey = (ResourceKey) invocation.getArguments()[0];
            final URL resourceUrl =
                AbstractControllerTest.class.getResource( "/" + resourceKey.getApplicationKey() + resourceKey.getPath() );
            return new UrlResource( resourceKey, resourceUrl );
        } );

        final ScriptAsyncService scriptAsyncService = Mockito.mock( ScriptAsyncService.class );

        final ScriptRuntimeFactory runtimeFactory =
            ScriptFixturesFacade.getInstance().scriptRuntimeFactory( applicationService, resourceService, scriptAsyncService );

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
