package com.enonic.xp.portal.impl.controller;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
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
import com.enonic.xp.portal.impl.postprocess.PostProcessorImpl;
import com.enonic.xp.portal.impl.script.ScriptServiceImpl;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.ResourceUrlTestHelper;
import com.enonic.xp.web.servlet.ServletRequestHolder;

public abstract class AbstractControllerTest
{
    protected PostProcessor postProcessor;

    private ControllerScriptFactoryImpl factory;

    protected PortalRequest portalRequest;

    protected PortalResponse portalResponse;

    private final ObjectMapper mapper;

    protected ResourceService resourceService;

    public AbstractControllerTest()
    {
        this.mapper = new ObjectMapper();
        this.mapper.enable( SerializationFeature.INDENT_OUTPUT );
        this.mapper.enable( SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS );
        this.mapper.enable( SerializationFeature.WRITE_NULL_MAP_VALUES );
    }

    @Before
    public void setup()
        throws Exception
    {
        ResourceUrlTestHelper.mockModuleScheme().modulesClassLoader( getClass().getClassLoader() );

        this.portalRequest = new PortalRequest();
        this.portalResponse = PortalResponse.create().build();

        final BundleContext bundleContext = Mockito.mock( BundleContext.class );

        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getBundleContext() ).thenReturn( bundleContext );

        final Application application = Mockito.mock( Application.class );
        Mockito.when( application.getBundle() ).thenReturn( bundle );

        final ApplicationService applicationService = Mockito.mock( ApplicationService.class );
        Mockito.when( applicationService.getModule( ApplicationKey.from( "mymodule" ) ) ).thenReturn( application );
        Mockito.when( applicationService.getClassLoader( Mockito.any() ) ).thenReturn( getClass().getClassLoader() );

        this.resourceService = Mockito.mock( ResourceService.class );
        Mockito.when( resourceService.getResource( Mockito.any() ) ).thenAnswer( invocation -> {
            final ResourceKey resourceKey = (ResourceKey) invocation.getArguments()[0];
            return new Resource( resourceKey, new URL( "module:" + resourceKey.toString() ) );
        } );

        final ScriptServiceImpl scriptService = new ScriptServiceImpl();
        scriptService.setApplicationService( applicationService );
        scriptService.setResourceService( this.resourceService );

        this.factory = new ControllerScriptFactoryImpl();
        this.factory.setScriptService( scriptService );

        this.postProcessor = new PostProcessorImpl();
        this.factory.setPostProcessor( this.postProcessor );

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
        final PortalResponseSerializer serializer = new PortalResponseSerializer( portalResponse );
        return serializer.serialize().getAsString();
    }

    protected final void assertJson( final String name, final String actual )
        throws Exception
    {
        final String resource = "/" + getClass().getName().replace( '.', '/' ) + "-" + name + ".json";
        final URL url = getClass().getResource( resource );

        Assert.assertNotNull( "File [" + resource + "] not found", url );
        final JsonNode expectedJson = this.mapper.readTree( url );
        final JsonNode actualJson = this.mapper.readTree( actual );

        final String expectedStr = this.mapper.writeValueAsString( expectedJson );
        final String actualStr = this.mapper.writeValueAsString( actualJson );

        Assert.assertEquals( expectedStr, actualStr );
    }
}
