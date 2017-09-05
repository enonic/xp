package com.enonic.xp.lib.thymeleaf;

import java.util.TimeZone;
import java.util.regex.Pattern;

import org.junit.Assert;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.testing.ScriptRunnerSupport;

public class ThymeleafScriptTest
    extends ScriptRunnerSupport
{
    @Override
    public String getScriptTestFile()
    {
        return "/site/thymeleaf-test.js";
    }

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();
        TimeZone.setDefault( TimeZone.getTimeZone( "GMT" ) );
    }

    private void assertHtmlEquals( final String expectedHtml, final String actualHtml )
    {
        Assert.assertEquals( normalizeText( expectedHtml ), normalizeText( actualHtml ) );
    }

    public void assertHtmlEquals( final ResourceKey resource, final String actualHtml )
    {
        assertHtmlEquals( loadResource( resource ).readString(), actualHtml );
    }

    private String normalizeText( final String text )
    {
        final Iterable<String> lines = Splitter.on( Pattern.compile( "(\r\n|\n|\r)" ) ).trimResults().split( text );
        return Joiner.on( "\n" ).join( lines );
    }
}
