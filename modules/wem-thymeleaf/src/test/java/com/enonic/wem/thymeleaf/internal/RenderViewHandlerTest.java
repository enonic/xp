package com.enonic.wem.thymeleaf.internal;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.wem.api.resource.ResourceProblemException;
import com.enonic.wem.portal.PortalContextAccessor;
import com.enonic.wem.portal.internal.controller.PortalContextImpl;
import com.enonic.wem.portal.internal.controller.PortalRequestImpl;
import com.enonic.wem.script.AbstractScriptTest;
import com.enonic.wem.script.ScriptExports;
import com.enonic.wem.servlet.ServletRequestHolder;

import static org.junit.Assert.*;

public class RenderViewHandlerTest
    extends AbstractScriptTest
{
    @Before
    public void setUp()
    {
        final RenderViewHandler handler = new RenderViewHandler();
        handler.setFactory( new ThymeleafProcessorFactoryImpl() );
        addHandler( handler );
    }

    private Object execute( final String method )
        throws Exception
    {
        final ScriptExports exports = runTestScript( "thymeleaf-test.js" );
        return exports.executeMethod( method ).getValue();
    }

    private void executeException( final String method, final String expectedMessage )
        throws Exception
    {
        try
        {
            execute( method );
            fail( "Expected to fail with exception" );
        }
        catch ( final ResourceProblemException e )
        {
            assertEquals( expectedMessage, e.getMessage() );
        }
        catch ( final Exception e )
        {
            fail( "Expected ResourceProblemException but got " + e.getClass().getName() );
        }
    }

    @Test
    public void renderNoView()
        throws Exception
    {
        executeException( "noViewTest", "Parameter [view] is required" );
    }

    @Test
    public void renderTest()
        throws Exception
    {
        final String result = execute( "renderTest" ).toString();
        final String expected = Resources.toString( getClass().getResource( "/view/test-result.html" ), Charsets.UTF_8 );
        assertEquals( expected, result );
    }

    @Test
    @Ignore
    public void functionsTest()
        throws Exception
    {
        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        Mockito.when( req.getScheme() ).thenReturn( "http" );
        Mockito.when( req.getServerName() ).thenReturn( "localhost" );
        Mockito.when( req.getLocalPort() ).thenReturn( 80 );
        ServletRequestHolder.setRequest( req );

        final PortalRequestImpl request = new PortalRequestImpl();
        final PortalContextImpl context = new PortalContextImpl();
        context.setRequest( request );

        PortalContextAccessor.set( context );

        final String result = execute( "functionsTest" ).toString();
        final String expected = Resources.toString( getClass().getResource( "/view/functions-result.html" ), Charsets.UTF_8 );
        assertEquals( expected, result );
    }
}
