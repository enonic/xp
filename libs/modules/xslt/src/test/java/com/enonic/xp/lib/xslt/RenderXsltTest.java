package com.enonic.xp.lib.xslt;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.xp.portal.script.ScriptExports;
import com.enonic.xp.portal.script.ScriptValue;
import com.enonic.xp.testing.script.ScriptTestSupport;
import com.enonic.xp.xml.DomHelper;

import static org.junit.Assert.*;

public class RenderXsltTest
    extends ScriptTestSupport
{
    @Before
    public void setUp()
    {
        final XsltService service = new XsltService();
        addBean( "com.enonic.xp.lib.xslt.XsltService", service );
    }

    private ScriptValue execute( final String method )
        throws Exception
    {
        final ScriptExports exports = runTestScript( "/test/xslt-test.js" );
        return exports.executeMethod( method );
    }

    @Test
    public void renderTest()
        throws Exception
    {
        final String result = cleanupXml( execute( "render" ).getValue().toString() );
        final String expected =
            cleanupXml( Resources.toString( getClass().getResource( "/app/test/view/test-result.xml" ), Charsets.UTF_8 ) );
        assertEquals( expected, result );
    }

    private String cleanupXml( final String xml )
        throws Exception
    {
        return DomHelper.serialize( DomHelper.parse( xml ) );
    }
}
