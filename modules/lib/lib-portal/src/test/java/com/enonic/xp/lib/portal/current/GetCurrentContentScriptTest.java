package com.enonic.xp.lib.portal.current;

import org.junit.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.lib.portal.TestDataFixtures;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class GetCurrentContentScriptTest
    extends ScriptTestSupport
{
    @Test
    public void currentContent()
    {
        final Content content = TestDataFixtures.newContent();
        this.portalRequest.setContent( content );

        runFunction( "/site/test/getCurrentContent-test.js", "currentContent" );
    }

    @Test
    public void noCurrentContent()
    {
        this.portalRequest.setContent( null );
        runFunction( "/site/test/getCurrentContent-test.js", "noCurrentContent" );
    }
}
