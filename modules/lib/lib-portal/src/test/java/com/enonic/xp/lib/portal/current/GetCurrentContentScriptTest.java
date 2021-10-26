package com.enonic.xp.lib.portal.current;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.lib.portal.TestDataFixtures;
import com.enonic.xp.testing.ScriptTestSupport;

public class GetCurrentContentScriptTest
    extends ScriptTestSupport
{
    @Test
    public void currentContent()
    {
        final Content content = TestDataFixtures.newContent();
        this.portalRequest.setContent( content );

        runFunction( "/test/getCurrentContent-test.js", "currentContent" );
    }

    @Test
    public void noCurrentContent()
    {
        this.portalRequest.setContent( null );
        runFunction( "/test/getCurrentContent-test.js", "noCurrentContent" );
    }

    @Test
    public void currentContentByContentPath()
    {
        final Content content = TestDataFixtures.newContent();
        this.portalRequest.setContent( null );
        this.portalRequest.setContentPath( content.getPath() );
        runFunction( "/test/getCurrentContent-test.js", "noCurrentContent" );
    }

    @Test
    public void testExample()
    {
        final Content content = TestDataFixtures.newExampleContent();
        this.portalRequest.setContent( content );

        runScript( "/lib/xp/examples/portal/getContent.js" );
    }
}
