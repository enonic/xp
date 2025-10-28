package com.enonic.xp.lib.portal.current;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.lib.portal.TestDataFixtures;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.site.Site;
import com.enonic.xp.testing.ScriptTestSupport;

class GetCurrentSiteScriptTest
    extends ScriptTestSupport
{
    @Test
    void currentSite()
    {
        final Site site = TestDataFixtures.newSite().build();
        this.portalRequest.setSite( site );

        runFunction( "/test/getCurrentSite-test.js", "currentSite" );
    }

    @Test
    void noCurrentSite()
    {
        this.portalRequest.setSite( null );
        runFunction( "/test/getCurrentSite-test.js", "noCurrentSite" );
    }

    @Test
    void insufficientRights()
    {
        final Site site = TestDataFixtures.newSite().permissions(
            AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.ADMIN ).allow( Permission.READ ).build() ) ).build();

        this.portalRequest.setSite( site );
        runFunction( "/test/getCurrentSite-test.js", "noCurrentSite" );
    }

    @Test
    void currentSiteByContentPath()
    {
        final Content content = TestDataFixtures.newContent();
        this.portalRequest.setContent( null );
        this.portalRequest.setContentPath( content.getPath() );
        this.portalRequest.setSite( null );
        runFunction( "/test/getCurrentSite-test.js", "noCurrentSite" );
    }

    @Test
    void testExample()
    {
        final Site site = TestDataFixtures.newSite().build();
        this.portalRequest.setSite( site );

        runScript( "/lib/xp/examples/portal/getSite.js" );
    }
}
