package com.enonic.xp.lib.portal.current;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.lib.portal.TestDataFixtures;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class GetCurrentContentScriptTest
    extends ScriptTestSupport
{
    @Before
    public void setUp()
        throws Exception
    {
        setupRequest();

        mockResource( "mymodule:/test/getCurrentContent-test.js" );
        mockResource( "mymodule:/site/lib/xp/portal.js" );
    }

    @Test
    public void currentContent()
        throws Exception
    {
        final Content content = TestDataFixtures.newContent();
        this.portalRequest.setContent( content );

        runTestFunction( "/test/getCurrentContent-test.js", "currentContent" );
    }

    @Test
    public void noCurrentContent()
        throws Exception
    {
        this.portalRequest.setContent( null );
        runTestFunction( "/test/getCurrentContent-test.js", "noCurrentContent" );
    }
}
