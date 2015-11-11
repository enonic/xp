package com.enonic.xp.lib.mustache;

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

import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.testing.script.OldScriptTestSupport;

import static org.junit.Assert.*;

public class RenderMustacheTest
    extends OldScriptTestSupport
{

    @Before
    public void setUp()
        throws Exception
    {
        addService( ResourceService.class, this.resourceService );
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
        final String output = runTestFunction( "test/mustache-test.js", "render" ).getValue().toString();
        final String result = stripEmptyLines( output );
        final String expected = stripEmptyLines( readFile( "/site/test/view/test-result.html" ) );
        assertEquals( expected, result );
    }
}
