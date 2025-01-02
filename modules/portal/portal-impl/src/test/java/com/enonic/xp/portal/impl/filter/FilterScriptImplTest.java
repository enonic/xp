package com.enonic.xp.portal.impl.filter;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Assertions;
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
import com.enonic.xp.portal.filter.FilterScript;
import com.enonic.xp.portal.impl.script.PortalScriptServiceImpl;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.script.ScriptFixturesFacade;
import com.enonic.xp.script.impl.async.ScriptAsyncService;
import com.enonic.xp.script.runtime.ScriptRuntimeFactory;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.servlet.ServletRequestHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class FilterScriptImplTest
{
    private FilterScriptFactoryImpl factory;

    protected PortalRequest portalRequest;

    protected PortalResponse portalResponse;

    protected ResourceService resourceService;

    public FilterScriptImplTest()
    {
    }

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
        Mockito.when( application.isStarted() ).thenReturn( true );
        Mockito.when( application.getConfig() ).thenReturn( ConfigBuilder.create().build() );

        final ApplicationService applicationService = Mockito.mock( ApplicationService.class );
        Mockito.when( applicationService.getInstalledApplication( ApplicationKey.from( "myapplication" ) ) ).thenReturn( application );

        this.resourceService = Mockito.mock( ResourceService.class );
        Mockito.when( resourceService.getResource( Mockito.any() ) ).thenAnswer( invocation -> {
            final ResourceKey resourceKey = (ResourceKey) invocation.getArguments()[0];
            final URL resourceUrl = FilterScriptImplTest.class.getResource( "/" + resourceKey.getApplicationKey() + resourceKey.getPath() );
            return new UrlResource( resourceKey, resourceUrl );
        } );

        final ScriptAsyncService scriptAsyncService = Mockito.mock( ScriptAsyncService.class );

        final ScriptRuntimeFactory runtimeFactory =
            ScriptFixturesFacade.getInstance().scriptRuntimeFactory( applicationService, resourceService, scriptAsyncService );

        final PortalScriptServiceImpl scriptService = new PortalScriptServiceImpl( runtimeFactory );
        scriptService.initialize();

        this.factory = new FilterScriptFactoryImpl( scriptService );

        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        ServletRequestHolder.setRequest( req );
    }

    @Test
    public void testExecute()
    {
        this.portalRequest.setMethod( HttpMethod.GET );
        execute( "myapplication:/filter/simple.js", null );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
    }

    @Test
    public void testNextFilter()
        throws Exception
    {
        WebHandlerChain webHandlerChain = Mockito.mock( WebHandlerChain.class );
        Mockito.when( webHandlerChain.handle( Mockito.any(), Mockito.any() ) ).thenReturn( this.portalResponse );

        this.portalRequest.setMethod( HttpMethod.POST );
        execute( "myapplication:/filter/callnext.js", webHandlerChain );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
    }

    @Test
    void testRemoveHeader()
        throws Exception
    {
        this.portalRequest.setMethod( HttpMethod.GET );
        this.portalResponse = PortalResponse.create().header( "pleaseDontFail", "value" ).build();
        WebHandlerChain webHandlerChain = Mockito.mock( WebHandlerChain.class );
        Mockito.when( webHandlerChain.handle( Mockito.any(), Mockito.any() ) ).thenReturn( this.portalResponse );

        execute( "myapplication:/filter/removeHeader.js", webHandlerChain );
        assertThat( this.portalResponse.getHeaders() ).doesNotContainKey( "pleaseDontFail" );
    }

    @Test
    public void testNoFilterFunction()
        throws Exception
    {
        WebHandlerChain webHandlerChain = Mockito.mock( WebHandlerChain.class );
        Mockito.when( webHandlerChain.handle( Mockito.any(), Mockito.any() ) ).thenReturn( this.portalResponse );

        this.portalRequest.setMethod( HttpMethod.POST );
        try
        {
            execute( "myapplication:/filter/nofilter.js", webHandlerChain );
            fail( "Expected exception" );
        }
        catch ( WebException e )
        {
            assertEquals( "Missing exported function 'filter' in filter script: myapplication:/filter/nofilter.js", e.getMessage() );
        }
    }

    @Test
    public void testExecErrorHandling()
        throws Exception
    {
        WebHandlerChain webHandlerChain = Mockito.mock( WebHandlerChain.class );
        Mockito.when( webHandlerChain.handle( Mockito.any(), Mockito.any() ) ).thenReturn( this.portalResponse );

        this.portalRequest.setMethod( HttpMethod.POST );
        final ResourceProblemException e =
            assertThrows( ResourceProblemException.class, () -> execute( "myapplication:/filter/filtererror.js", webHandlerChain ) );
        assertEquals( "myapplication:/filter/filtererror.js", e.getResource().toString() );
        assertEquals( 3, e.getLineNumber() );
        assertNotNull( e.getMessage() );
    }

    @Test
    public void testDuplicatedNextCall()
        throws Exception
    {
        WebHandlerChain webHandlerChain = Mockito.mock( WebHandlerChain.class );
        Mockito.when( webHandlerChain.handle( Mockito.any(), Mockito.any() ) ).thenReturn( this.portalResponse );

        this.portalRequest.setMethod( HttpMethod.POST );
        try
        {
            execute( "myapplication:/filter/duplicated_next_call.js", webHandlerChain );
            fail( "Expected exception" );
        }
        catch ( ResourceProblemException e )
        {
            assertEquals( "myapplication:/filter/duplicated_next_call.js", e.getResource().toString() );
            assertEquals( "Filter 'next' function was called multiple times", e.getMessage() );
        }
    }

    @Test
    public void testResourceException()
        throws Exception
    {
        WebHandlerChain webHandlerChain = Mockito.mock( WebHandlerChain.class );
        Mockito.when( webHandlerChain.handle( Mockito.any(), Mockito.any() ) ).thenThrow( ResourceProblemException.class );

        Assertions.assertThrows( ResourceProblemException.class, () -> execute( "myapplication:/filter/callnext.js", webHandlerChain ) );

        Mockito.verify( webHandlerChain, Mockito.times( 1 ) ).handle( Mockito.any(), Mockito.any() );
    }

    @Test
    public void testHandleException()
        throws Exception
    {
        WebHandlerChain webHandlerChain = Mockito.mock( WebHandlerChain.class );
        Mockito.when( webHandlerChain.handle( Mockito.any(), Mockito.any() ) ).thenThrow( Exception.class );

        final ResourceProblemException exception = Assertions.assertThrows( ResourceProblemException.class,
                                                                            () -> execute( "myapplication:/filter/callnext.js",
                                                                                           webHandlerChain ) );

        assertEquals( "Error executing filter script: myapplication:/filter/callnext.js", exception.getMessage() );

        Mockito.verify( webHandlerChain, Mockito.times( 1 ) ).handle( Mockito.any(), Mockito.any() );
    }

    protected final void execute( final String script, final WebHandlerChain webHandlerChain )
    {
        final FilterScript controllerScript = this.factory.fromScript( ResourceKey.from( script ) );
        this.portalResponse = controllerScript.execute( this.portalRequest, this.portalResponse, webHandlerChain );
    }
}
