package com.enonic.xp.lib.xslt;

import org.junit.Test;

import com.enonic.xp.testing.script.ScriptTestSupport;
import com.enonic.xp.xml.DomHelper;

import static org.junit.Assert.*;

public class XsltLibTest
    extends ScriptTestSupport
{
    @Test
    public void testSimple()
    {
        final String expected = loadResource( "/site/view/simple-result.xml" ).readString();
        final String actual = runFunction( "/site/xslt-test.js", "simple" ).getValue().toString();

        assertEquals( cleanupXml( expected ), cleanupXml( actual ) );
    }

    @Test
    public void testUrlFunctions()
    {
        final String expected = loadResource( "/site/view/url-functions-result.xml" ).readString();
        final String actual = runFunction( "/site/xslt-test.js", "urlFunctions" ).getValue().toString();

        assertEquals( cleanupXml( expected ), cleanupXml( actual ) );
    }

    private String cleanupXml( final String xml )
    {
        return DomHelper.serialize( DomHelper.parse( xml ) );
    }
}
