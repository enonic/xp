package com.enonic.xp.portal.impl.macro;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.script.PortalScriptServiceImpl;
import com.enonic.xp.portal.macro.MacroContext;
import com.enonic.xp.portal.macro.MacroProcessor;
import com.enonic.xp.portal.postprocess.HtmlTag;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.script.ScriptFixturesFacade;
import com.enonic.xp.script.impl.async.ScriptAsyncService;
import com.enonic.xp.script.runtime.ScriptRuntimeFactory;
import com.enonic.xp.web.servlet.ServletRequestHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MacroProcessorScriptTest
{
    private MacroProcessorFactoryImpl factory;

    private MacroContext macroContext;

    private ResourceService resourceService;

    @BeforeEach
    public void setup()
        throws Exception
    {
        this.macroContext = MacroContext.create().
            request( new PortalRequest() ).
            name( "macroName" ).
            body( "body" ).
            param( "firstParam", "firstParamValue" ).
            param( "secondParam", "secondParamValue" ).
            document( "<h1>document</h1>" ).
            build();

        final BundleContext bundleContext = Mockito.mock( BundleContext.class );

        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getBundleContext() ).thenReturn( bundleContext );

        final Application application = Mockito.mock( Application.class );
        Mockito.when( application.getBundle() ).thenReturn( bundle );
        Mockito.when( application.getClassLoader() ).thenReturn( getClass().getClassLoader() );
        Mockito.when( application.getConfig() ).thenReturn( ConfigBuilder.create().build() );
        Mockito.when( application.isStarted() ).thenReturn( true );

        final ApplicationService applicationService = Mockito.mock( ApplicationService.class );
        Mockito.when( applicationService.getInstalledApplication( ApplicationKey.from( "myapplication" ) ) ).thenReturn( application );

        this.resourceService = Mockito.mock( ResourceService.class );
        Mockito.when( resourceService.getResource( Mockito.any() ) ).thenAnswer( invocation -> {
            final ResourceKey resourceKey = (ResourceKey) invocation.getArguments()[0];
            final URL resourceUrl =
                MacroProcessorScriptTest.class.getResource( "/" + resourceKey.getApplicationKey() + resourceKey.getPath() );
            return new UrlResource( resourceKey, resourceUrl );
        } );

        final ScriptAsyncService scriptAsyncService = Mockito.mock( ScriptAsyncService.class );

        final ScriptRuntimeFactory runtimeFactory =
            ScriptFixturesFacade.getInstance().scriptRuntimeFactory( applicationService, resourceService, scriptAsyncService );

        final PortalScriptServiceImpl scriptService = new PortalScriptServiceImpl( runtimeFactory );
        scriptService.initialize();

        this.factory = new MacroProcessorFactoryImpl( scriptService );

        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        ServletRequestHolder.setRequest( req );
    }

    private PortalResponse execute( final String scriptKey )
    {
        final MacroProcessor macroProcessor = this.factory.fromScript( ResourceKey.from( scriptKey ) );
        return macroProcessor.process( this.macroContext );
    }

    @Test
    public void testMacro()
    {
        final PortalResponse response = execute( "myapplication:/macro/macro.js" );
        assertEquals(
            "Macro context: {\"name\":\"macroName\",\"body\":\"body\",\"params\":{\"firstParam\":\"firstParamValue\",\"secondParam\":\"secondParamValue\"},\"request\":{\"port\":0,\"mode\":\"live\",\"webSocket\":false,\"repositoryId\":\"com.enonic.cms.default\",\"branch\":\"draft\",\"params\":{},\"headers\":{},\"cookies\":{}},\"document\":\"<h1>document</h1>\"}",
            response.getBody() );
        assertEquals( 1, response.getContributions( HtmlTag.HEAD_END ).size() );
        assertEquals( 1, response.getContributions( HtmlTag.BODY_END ).size() );
    }

    @Test
    public void testMissingMacro()
    {
        final PortalResponse response = execute( "myapplication:/macro/missing-macro.js" );
        assertNull( response );
    }
}
