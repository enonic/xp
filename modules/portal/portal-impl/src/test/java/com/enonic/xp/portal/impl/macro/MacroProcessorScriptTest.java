package com.enonic.xp.portal.impl.macro;

import java.net.URL;
import java.util.Hashtable;

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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

        final BundleContext bundleContext = mock( BundleContext.class );

        final Bundle bundle = mock( Bundle.class );
        when( bundle.getBundleContext() ).thenReturn( bundleContext );
        when( bundle.getHeaders() ).thenReturn( new Hashtable<>() );

        final Application application = mock( Application.class );
        when( application.getBundle() ).thenReturn( bundle );
        when( application.getClassLoader() ).thenReturn( getClass().getClassLoader() );
        when( application.getConfig() ).thenReturn( ConfigBuilder.create().build() );
        when( application.isStarted() ).thenReturn( true );

        final ApplicationService applicationService = mock( ApplicationService.class );
        when( applicationService.getInstalledApplication( ApplicationKey.from( "myapplication" ) ) ).thenReturn( application );

        this.resourceService = mock( ResourceService.class );
        when( resourceService.getResource( Mockito.any() ) ).thenAnswer( invocation -> {
            final ResourceKey resourceKey = (ResourceKey) invocation.getArguments()[0];
            final URL resourceUrl =
                MacroProcessorScriptTest.class.getResource( "/" + resourceKey.getApplicationKey() + resourceKey.getPath() );
            return new UrlResource( resourceKey, resourceUrl );
        } );

        final ScriptAsyncService scriptAsyncService = mock( ScriptAsyncService.class );

        final ScriptRuntimeFactory runtimeFactory =
            ScriptFixturesFacade.getInstance().scriptRuntimeFactory( applicationService, resourceService, scriptAsyncService );

        final PortalScriptServiceImpl scriptService = new PortalScriptServiceImpl( runtimeFactory );
        scriptService.initialize();

        this.factory = new MacroProcessorFactoryImpl( scriptService );

        final HttpServletRequest req = mock( HttpServletRequest.class );
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
