package com.enonic.xp.portal.impl.macro;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.macro.MacroContext;
import com.enonic.xp.portal.impl.script.PortalScriptServiceImpl;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.script.impl.ScriptRuntimeFactoryImpl;
import com.enonic.xp.web.servlet.ServletRequestHolder;

public class MacroProcessorScriptTest
{
    private MacroProcessorScriptFactoryImpl factory;

    protected MacroContext macroContext;

    protected String response;

    private final ObjectMapper mapper;

    protected ResourceService resourceService;

    public MacroProcessorScriptTest()
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
        this.macroContext = MacroContext.create().
            name( "macroName" ).
            body( "body" ).
            param( "firstParam", "firstParamValue" ).
            param( "secondParam", "secondParamValue" ).
            build();

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
                MacroProcessorScriptTest.class.getResource( "/" + resourceKey.getApplicationKey() + resourceKey.getPath() );
            return new UrlResource( resourceKey, resourceUrl );
        } );

        final ScriptRuntimeFactoryImpl runtimeFactory = new ScriptRuntimeFactoryImpl();
        runtimeFactory.setApplicationService( applicationService );
        runtimeFactory.setResourceService( this.resourceService );

        final PortalScriptServiceImpl scriptService = new PortalScriptServiceImpl();
        scriptService.setScriptRuntimeFactory( runtimeFactory );
        scriptService.initialize();

        this.factory = new MacroProcessorScriptFactoryImpl();
        this.factory.setScriptService( scriptService );

        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        ServletRequestHolder.setRequest( req );
    }

    private final void execute( final String scriptKey )
    {
        final MacroProcessorScript macroProcessorScript = this.factory.fromScript( ResourceKey.from( scriptKey ) );
        this.response = macroProcessorScript.process( this.macroContext );
    }

    @Test
    public void testMacro()
    {
        execute( "myapplication:/macro/macro.js" );
        Assert.assertEquals(
            "Macro context: {\"name\":\"macroName\",\"body\":\"body\",\"params\":{\"firstParam\":\"firstParamValue\",\"secondParam\":\"secondParamValue\"}}",
            this.response );
    }

    @Test
    public void testMissingMacro()
    {
        execute( "myapplication:/macro/missing-macro.js" );
        Assert.assertNull( this.response );
    }
}
