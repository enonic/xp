package com.enonic.wem.xslt.internal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.wem.api.resource.ResourceProblemException;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.api.xml.DomHelper;
import com.enonic.wem.portal.PortalContext;
import com.enonic.wem.portal.PortalContextAccessor;
import com.enonic.wem.portal.PortalRequest;
import com.enonic.wem.portal.RenderingMode;
import com.enonic.wem.script.AbstractScriptTest;
import com.enonic.wem.script.ScriptExports;

import static org.junit.Assert.*;

public class RenderViewHandler2Test
    extends AbstractScriptTest
{
    @Before
    public void setUp()
    {
        final PortalRequest portalRequest = Mockito.mock( PortalRequest.class );
        Mockito.when( portalRequest.getBaseUri() ).thenReturn( "/root" );
        Mockito.when( portalRequest.getMode() ).thenReturn( RenderingMode.EDIT );
        Mockito.when( portalRequest.getWorkspace() ).thenReturn( Workspace.from( "stage" ) );

        final PortalContext portalContext = Mockito.mock( PortalContext.class );
        Mockito.when( portalContext.getRequest() ).thenReturn( portalRequest );

        PortalContextAccessor.set( portalContext );

        final RenderViewHandler2 handler = new RenderViewHandler2();
        handler.setFactory( new XsltProcessorFactoryImpl() );
        addHandler( handler );
    }

    private Object execute( final String method )
        throws Exception
    {
        final ScriptExports exports = runTestScript( "xslt-v2-test.js" );
        return exports.executeMethod( method );
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
        executeException( "render_no_view", "Parameter [view] is required" );
    }

    @Test
    public void renderTest()
        throws Exception
    {
        final String result = cleanupXml( execute( "render" ).toString() );
        final String expected = cleanupXml( Resources.toString( getClass().getResource( "/view/test-v2-result.xml" ), Charsets.UTF_8 ) );
        assertEquals( expected, result );
    }

    private String cleanupXml( final String xml )
        throws Exception
    {
        return DomHelper.serialize( DomHelper.parse( xml ) );
    }
}
