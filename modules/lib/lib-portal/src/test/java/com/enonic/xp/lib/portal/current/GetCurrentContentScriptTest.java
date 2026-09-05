package com.enonic.xp.lib.portal.current;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.lib.portal.TestDataFixtures;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.testing.ScriptTestSupport;

class GetCurrentContentScriptTest
    extends ScriptTestSupport
{
    @Test
    void currentContent()
    {
        final Content content = TestDataFixtures.newContent();
        this.portalRequest.setContent( content );

        runFunction( "/test/getCurrentContent-test.js", "currentContent" );
    }

    @Test
    void currentContentNotReadable()
    {
        final Content content = Content.create( TestDataFixtures.newContent() )
            .permissions( AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allow( Permission.READ ).build() ) )
            .build();
        this.portalRequest.setContent( content );

        runFunction( "/test/getCurrentContent-test.js", "noCurrentContent" );
    }

    @Test
    void noCurrentContent()
    {
        this.portalRequest.setContent( null );
        runFunction( "/test/getCurrentContent-test.js", "noCurrentContent" );
    }

    @Test
    void currentContentByContentPath()
    {
        final Content content = TestDataFixtures.newContent();
        this.portalRequest.setContent( null );
        this.portalRequest.setContentPath( content.getPath() );
        runFunction( "/test/getCurrentContent-test.js", "noCurrentContent" );
    }

    @Test
    void testExample()
    {
        final Content content = TestDataFixtures.newExampleContent();
        this.portalRequest.setContent( content );

        runScript( "/lib/xp/examples/portal/getContent.js" );
    }
}
