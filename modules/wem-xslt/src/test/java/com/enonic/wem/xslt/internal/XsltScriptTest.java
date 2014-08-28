package com.enonic.wem.xslt.internal;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.script.AbstractScriptTest;

public class XsltScriptTest
    extends AbstractScriptTest
{
    @Before
    public void setUp()
    {
        final XsltScriptContributor contributor = new XsltScriptContributor();
        contributor.setProcessor( new SaxonXsltProcessor() );

        addContributor( contributor );
    }

    @Test
    public void renderTest()
    {
        runTestScript( "xslt-test.js" );
    }
}
