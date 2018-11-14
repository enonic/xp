package com.enonic.xp.portal.impl.filter;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.filter.FilterScript;
import com.enonic.xp.portal.impl.script.PortalScriptServiceImpl;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;
import com.enonic.xp.script.impl.standard.ScriptRuntimeFactoryImpl;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.servlet.ServletRequestHolder;

import static org.junit.Assert.*;

public class FilterScriptImplTest
{
    private FilterScriptFactoryImpl factory;

    protected PortalRequest portalRequest;

    protected PortalResponse portalResponse;

    protected ResourceService resourceService;

    public FilterScriptImplTest()
    {
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
        Mockito.when( application.getClassLoader() ).thenReturn( getClass().getClassLoader() );

        final ApplicationService applicationService = Mockito.mock( ApplicationService.class );
        Mockito.when( applicationService.getInstalledApplication( ApplicationKey.from( "myapplication" ) ) ).thenReturn( application );

        this.resourceService = Mockito.mock( ResourceService.class );
        Mockito.when( resourceService.getResource( Mockito.any() ) ).thenAnswer( invocation -> {
            final ResourceKey resourceKey = (ResourceKey) invocation.getArguments()[0];
            final URL resourceUrl = FilterScriptImplTest.class.getResource( "/" + resourceKey.getApplicationKey() + resourceKey.getPath() );
            return new UrlResource( resourceKey, resourceUrl );
        } );

        final ScriptRuntimeFactoryImpl runtimeFactory = new ScriptRuntimeFactoryImpl();
        runtimeFactory.setApplicationService( applicationService );
        runtimeFactory.setResourceService( this.resourceService );

        final PortalScriptServiceImpl scriptService = new PortalScriptServiceImpl();
        scriptService.setScriptRuntimeFactory( runtimeFactory );
        scriptService.initialize();

        this.factory = new FilterScriptFactoryImpl();
        this.factory.setScriptService( scriptService );

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
        try
        {
            execute( "myapplication:/filter/filtererror.js", webHandlerChain );
            fail( "Expected exception" );
        }
        catch ( ResourceProblemException e )
        {
            assertEquals( "myapplication:/filter/filtererror.js", e.getResource().toString() );
            assertEquals( 3, e.getLineNumber() );
            assertEquals( "ReferenceError: \"callback\" is not defined", e.getMessage() );
        }
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
            assertEquals( 3, e.getLineNumber() );
            assertEquals( "Filter 'next' function was called multiple times", e.getMessage() );
        }
    }

    protected final void execute( final String script, final WebHandlerChain webHandlerChain )
    {
        final FilterScript controllerScript = this.factory.fromScript( ResourceKey.from( script ) );
        this.portalResponse = controllerScript.execute( this.portalRequest, this.portalResponse, webHandlerChain );
    }
}
