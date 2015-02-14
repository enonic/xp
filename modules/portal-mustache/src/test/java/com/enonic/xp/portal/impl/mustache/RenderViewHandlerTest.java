package com.enonic.xp.portal.impl.mustache;

import java.io.StringReader;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;

import com.enonic.wem.api.resource.ResourceProblemException;
import com.enonic.xp.portal.impl.script.AbstractScriptTest;
import com.enonic.xp.portal.script.ScriptExports;

import static org.junit.Assert.*;

public class RenderViewHandlerTest
    extends AbstractScriptTest
{
    @Before
    public void setUp()
    {
        addHandler( new RenderViewHandler() );
    }

    private Object execute( final String method )
        throws Exception
    {
        final ScriptExports exports = runTestScript( "mustache-test.js" );
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
        executeException( "render_no_view", "Parameter [view] is required" );
    }

    private String readFile( final String name )
        throws Exception
    {
        final URL url = getClass().getResource( name );
        return Resources.toString( url, Charsets.UTF_8 );
    }

    private String stripEmptyLines( final String text )
        throws Exception
    {
        final List<String> lines = CharStreams.readLines( new StringReader( text ) );
        final List<String> trimmed = lines.stream().filter( str -> str.trim().length() > 0 ).collect( Collectors.toList() );
        return Joiner.on( '\n' ).join( trimmed );
    }

    @Test
    public void renderTest()
        throws Exception
    {
        final String result = stripEmptyLines( execute( "render" ).toString() );
        final String expected = stripEmptyLines( readFile( "/modules/mymodule/view/view-result.html" ) );
        assertEquals( expected, result );
    }
}
