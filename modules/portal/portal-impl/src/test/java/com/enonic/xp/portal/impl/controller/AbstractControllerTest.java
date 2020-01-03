package com.enonic.xp.portal.impl.controller;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.impl.postprocess.PostProcessorImpl;
import com.enonic.xp.portal.impl.script.PortalScriptServiceImpl;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.script.impl.standard.ScriptRuntimeFactoryImpl;
import com.enonic.xp.web.servlet.ServletRequestHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class AbstractControllerTest
{
    private static final ObjectMapper MAPPER = new ObjectMapper().
        enable( SerializationFeature.INDENT_OUTPUT ).
        enable( SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS );

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
        Mockito.when( bundle.getBundleContext() ).thenReturn( bundleContext );

        final Application application = Mockito.mock( Application.class );
        Mockito.when( application.getBundle() ).thenReturn( bundle );
        Mockito.when( application.getClassLoader() ).thenReturn( getClass().getClassLoader() );

        final ApplicationService applicationService = Mockito.mock( ApplicationService.class );
        Mockito.when( applicationService.getInstalledApplication( ApplicationKey.from( "myapplication" ) ) ).thenReturn( application );

        this.resourceService = Mockito.mock( ResourceService.class );
        Mockito.when( resourceService.getResource( Mockito.any() ) ).thenAnswer( invocation -> {
            final ResourceKey resourceKey = (ResourceKey) invocation.getArguments()[0];
            final URL resourceUrl =
                AbstractControllerTest.class.getResource( "/" + resourceKey.getApplicationKey() + resourceKey.getPath() );
            return new UrlResource( resourceKey, resourceUrl );
        } );

        final ScriptRuntimeFactoryImpl runtimeFactory = new ScriptRuntimeFactoryImpl();
        runtimeFactory.setApplicationService( applicationService );
        runtimeFactory.setResourceService( this.resourceService );

        final PortalScriptServiceImpl scriptService = new PortalScriptServiceImpl();
        scriptService.setScriptRuntimeFactory( runtimeFactory );
        scriptService.initialize();

        this.factory = new ControllerScriptFactoryImpl();
        this.factory.setScriptService( scriptService );

        this.postProcessor = new PostProcessorImpl();

        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        ServletRequestHolder.setRequest( req );
    }

    protected final void execute( final String script )
    {
        final ControllerScript controllerScript = this.factory.fromScript( ResourceKey.from( script ) );
        this.portalResponse = controllerScript.execute( this.portalRequest );
    }

    protected final String getResponseAsString()
    {
        return portalResponse.getAsString();
    }

    protected final void assertJson( final String name, final String actual )
        throws Exception
    {
        final String resource = "/" + getClass().getName().replace( '.', '/' ) + "-" + name + ".json";
        final URL url = getClass().getResource( resource );

        assertNotNull( url, "File [" + resource + "] not found" );
        final JsonNode expectedJson = MAPPER.readTree( url );
        final JsonNode actualJson = MAPPER.readTree( actual );

        final String expectedStr = MAPPER.writeValueAsString( expectedJson );
        final String actualStr = MAPPER.writeValueAsString( actualJson );

        assertEquals( expectedStr, actualStr );
    }
}
