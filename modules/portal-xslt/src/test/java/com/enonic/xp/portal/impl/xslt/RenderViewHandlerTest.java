package com.enonic.xp.portal.impl.xslt;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.wem.api.resource.ResourceProblemException;
import com.enonic.wem.api.xml.DomHelper;
import com.enonic.xp.portal.impl.script.AbstractScriptTest;
import com.enonic.xp.portal.script.ScriptExports;
import com.enonic.xp.portal.script.ScriptValue;

import static org.junit.Assert.*;

public class RenderViewHandlerTest
    extends AbstractScriptTest
{
    @Before
    public void setUp()
    {
        final RenderViewHandler handler = new RenderViewHandler();
        handler.initialize();
        addHandler( handler );
    }

    private ScriptValue execute( final String method )
        throws Exception
    {
        final ScriptExports exports = runTestScript( "xslt-test.js" );
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
        final String result = cleanupXml( execute( "render" ).getValue().toString() );
        final String expected =
            cleanupXml( Resources.toString( getClass().getResource( "/modules/mymodule/view/test-result.xml" ), Charsets.UTF_8 ) );
        assertEquals( expected, result );
    }

    private String cleanupXml( final String xml )
        throws Exception
    {
        return DomHelper.serialize( DomHelper.parse( xml ) );
    }
}
