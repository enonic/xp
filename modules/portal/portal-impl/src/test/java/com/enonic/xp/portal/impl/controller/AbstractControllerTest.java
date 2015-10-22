package com.enonic.xp.portal.impl.controller;

import java.net.URL;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.io.Resources;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.mapper.PortalResponseMapper;
import com.enonic.xp.portal.impl.postprocess.PostProcessorImpl;
import com.enonic.xp.portal.impl.script.PortalScriptServiceImpl;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.script.impl.ScriptRuntimeFactoryImpl;
import com.enonic.xp.script.serializer.JsonMapGenerator;
import com.enonic.xp.web.servlet.ServletRequestHolder;

public abstract class AbstractControllerTest
{
    protected PostProcessorImpl postProcessor;

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
        this.portalRequest = new PortalRequest();
        this.portalResponse = PortalResponse.create().build();

        final BundleContext bundleContext = Mockito.mock( BundleContext.class );

        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getBundleContext() ).thenReturn( bundleContext );

        final Application application = Mockito.mock( Application.class );
        Mockito.when( application.getBundle() ).thenReturn( bundle );

        final ApplicationService applicationService = Mockito.mock( ApplicationService.class );
        Mockito.when( applicationService.getApplication( ApplicationKey.from( "myapplication" ) ) ).thenReturn( application );
        Mockito.when( applicationService.getClassLoader( Mockito.any() ) ).thenReturn( getClass().getClassLoader() );

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

    protected String getResponseAsJson()
        throws JsonProcessingException
    {
        final JsonMapGenerator generator = new JsonMapGenerator();
        new PortalResponseMapper( portalResponse ).serialize( generator );
        return this.mapper.writeValueAsString( generator.getRoot() );
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

    protected final void assertHtml( final String name, final String actual )
        throws Exception
    {
        final String resource = "/" + getClass().getName().replace( '.', '/' ) + "-" + name + ".html";
        final URL url = getClass().getResource( resource );
        final String expected = Resources.toString( url, Charsets.UTF_8 );

        if ( !normalizeHTMLString( expected ).equals( normalizeHTMLString( actual ) ) )
        {
            Assert.assertEquals( expected, actual );
        }
    }

    private String normalizeHTMLString( final String text )
    {
        final Iterable<String> lines = Splitter.on( Pattern.compile( "(\r\n|\n|\r)" ) ).trimResults().split( text );
        return Joiner.on( "" ).join( lines );
    }
}
