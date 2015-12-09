package com.enonic.xp.lib.thymeleaf;

import java.util.regex.Pattern;

import org.junit.Test;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.testing.script.ScriptTestSupport;

import static org.junit.Assert.*;

public class ThymeleafLibTest
    extends ScriptTestSupport
{
    @Test
    public void testExample()
    {
        runScript( "/site/lib/xp/examples/render.js" );
    }

    @Test
    public void renderTest()
    {
        final String actual = runFunction( "/site/thymeleaf-test.js", "renderTest" ).getValue().toString();
        assertHtmlEquals( loadResource( "/site/view/test-result.html" ), actual );
    }

    @Test
    public void functionsTest()
    {
        final String actual = runFunction( "/site/thymeleaf-test.js", "functionsTest" ).getValue().toString();
        assertHtmlEquals( loadResource( "/site/view/functions-result.html" ), actual );
    }

    @Test
    public void inlineFragment()
    {
        final String actual = runFunction( "/site/thymeleaf-test.js", "inlineFragmentTest" ).getValue().toString();
        assertHtmlEquals( loadResource( "/site/fragment/inline-fragment-result.html" ), actual );
    }

    @Test
    public void externalFragment()
    {
        final String actual = runFunction( "/site/thymeleaf-test.js", "externalFragmentTest" ).getValue().toString();
        assertHtmlEquals( loadResource( "/site/fragment/external-fragment-result.html" ), actual );
    }

    @Test
    public void dateTest()
    {
        final String actual = runFunction( "/site/thymeleaf-test.js", "dateTest" ).getValue().toString();
        assertHtmlEquals( loadResource( "/site/view/date-result.html" ), actual );
    }

    public String normalizeText( final String text )
    {
        final Iterable<String> lines = Splitter.on( Pattern.compile( "(\r\n|\n|\r)" ) ).trimResults().split( text );
        return Joiner.on( "\n" ).join( lines );
    }

    public void assertHtmlEquals( final String expectedHtml, final String actualHtml )
    {
        assertEquals( normalizeText( expectedHtml ), normalizeText( actualHtml ) );
    }

    public void assertHtmlEquals( final Resource resource, final String actualHtml )
    {
        final String expectedHtml = resource.readString();
        assertHtmlEquals( expectedHtml, actualHtml );
    }
}
