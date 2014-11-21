package com.enonic.wem.thymeleaf.internal;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.wem.api.resource.ResourceProblemException;
import com.enonic.wem.script.AbstractScriptTest;
import com.enonic.wem.script.ScriptExports;

import static org.junit.Assert.*;

public class RenderViewHandler2Test
    extends AbstractScriptTest
{
    @Before
    public void setUp()
    {
        final RenderViewHandler2 handler = new RenderViewHandler2();
        handler.setFactory( new ThymeleafProcessorFactoryImpl() );
        addHandler( handler );
    }

    private Object execute( final String method )
        throws Exception
    {
        final ScriptExports exports = runTestScript( "thymeleaf-v2-test.js" );
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
        final String result = execute( "render" ).toString();
        final String expected = Resources.toString( getClass().getResource( "/view/view-v2-result.html" ), Charsets.UTF_8 );
        assertEquals( expected, result );
    }
}
