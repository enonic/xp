package com.enonic.xp.lib.xslt;

import org.junit.Assert;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.testing.script.ScriptRunnerSupport;
import com.enonic.xp.xml.DomHelper;

public class XsltScriptTest
    extends ScriptRunnerSupport
{
    @Override
    public String getScriptTestFile()
    {
        return "/site/xslt-test.js";
    }

    private void assertXmlEquals( final String expectedXml, final String actualXml )
    {
        Assert.assertEquals( cleanupXml( expectedXml ), cleanupXml( actualXml ) );
    }

    public void assertXmlEquals( final ResourceKey resource, final String actualXml )
    {
        assertXmlEquals( loadResource( resource ).readString(), actualXml );
    }

    private String cleanupXml( final String xml )
    {
        return DomHelper.serialize( DomHelper.parse( xml ) );
    }
}
