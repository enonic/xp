package com.enonic.xp.portal.impl.filter;

import java.net.URL;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
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
import com.enonic.xp.script.runtime.ScriptRuntimeFactory;
import com.enonic.xp.util.Version;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.handler.WebHandlerChain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

class FilterScriptImplTest
{
    private FilterScriptFactoryImpl factory;

    protected PortalRequest portalRequest;

    protected PortalResponse portalResponse;

    protected ResourceService resourceService;

    public FilterScriptImplTest()
    {
    }

    @BeforeEach
    void setup()
    {
        this.portalRequest = new PortalRequest();
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
            final URL resourceUrl = FilterScriptImplTest.class.getResource( "/" + resourceKey.getApplicationKey() + resourceKey.getPath() );
            return new UrlResource( resourceKey, resourceUrl );
        } );

        final ScriptRuntimeFactory runtimeFactory =
            ScriptFixturesFacade.getInstance().scriptRuntimeFactory( resourceService, null, application );

        final PortalScriptServiceImpl scriptService = new PortalScriptServiceImpl( runtimeFactory );
        scriptService.initialize();

        this.factory = new FilterScriptFactoryImpl( scriptService );
    }

    @Test
    void testExecute()
    {
        this.portalRequest.setMethod( HttpMethod.GET );
        execute( "myapplication:/filter/simple.js", null );
        assertEquals( HttpStatus.OK, this.portalResponse.getStatus() );
    }

    @Test
    void testNextFilter()
        throws Exception
    {
        WebHandlerChain webHandlerChain = Mockito.mock( WebHandlerChain.class );
        when( webHandlerChain.handle( Mockito.any(), Mockito.any() ) ).thenReturn( this.portalResponse );

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
        when( webHandlerChain.handle( Mockito.any(), Mockito.any() ) ).thenReturn( this.portalResponse );

        execute( "myapplication:/filter/removeHeader.js", webHandlerChain );
        assertThat( this.portalResponse.getHeaders() ).doesNotContainKey( "pleaseDontFail" );
    }

    @Test
    void testNoFilterFunction()
        throws Exception
    {
        WebHandlerChain webHandlerChain = Mockito.mock( WebHandlerChain.class );
        when( webHandlerChain.handle( Mockito.any(), Mockito.any() ) ).thenReturn( this.portalResponse );

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
    void testExecErrorHandling()
        throws Exception
    {
        WebHandlerChain webHandlerChain = Mockito.mock( WebHandlerChain.class );
        when( webHandlerChain.handle( Mockito.any(), Mockito.any() ) ).thenReturn( this.portalResponse );

        this.portalRequest.setMethod( HttpMethod.POST );
        final ResourceProblemException e =
            assertThrows( ResourceProblemException.class, () -> execute( "myapplication:/filter/filtererror.js", webHandlerChain ) );
        assertEquals( "myapplication:/filter/filtererror.js", e.getResource().toString() );
        assertEquals( 3, e.getLineNumber() );
        assertNotNull( e.getMessage() );
    }

    @Test
    void testDuplicatedNextCall()
        throws Exception
    {
        WebHandlerChain webHandlerChain = Mockito.mock( WebHandlerChain.class );
        when( webHandlerChain.handle( Mockito.any(), Mockito.any() ) ).thenReturn( this.portalResponse );

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
    void testResourceException()
        throws Exception
    {
        WebHandlerChain webHandlerChain = Mockito.mock( WebHandlerChain.class );
        when( webHandlerChain.handle( Mockito.any(), Mockito.any() ) ).thenThrow( ResourceProblemException.class );

        Assertions.assertThrows( ResourceProblemException.class, () -> execute( "myapplication:/filter/callnext.js", webHandlerChain ) );

        Mockito.verify( webHandlerChain, Mockito.times( 1 ) ).handle( Mockito.any(), Mockito.any() );
    }

    @Test
    void testHandleException()
        throws Exception
    {
        WebHandlerChain webHandlerChain = Mockito.mock( WebHandlerChain.class );
        when( webHandlerChain.handle( Mockito.any(), Mockito.any() ) ).thenThrow( Exception.class );

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
