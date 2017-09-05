package com.enonic.xp.lib.portal.current;

import org.junit.Test;

import com.enonic.xp.lib.portal.TestDataFixtures;
import com.enonic.xp.site.Site;
import com.enonic.xp.testing.ScriptTestSupport;

public class GetCurrentSiteScriptTest
    extends ScriptTestSupport
{
    @Test
    public void currentSite()
    {
        final Site site = TestDataFixtures.newSite();
        this.portalRequest.setSite( site );

        runFunction( "/site/test/getCurrentSite-test.js", "currentSite" );
    }

    @Test
    public void noCurrentSite()
    {
        this.portalRequest.setSite( null );
        runFunction( "/site/test/getCurrentSite-test.js", "noCurrentSite" );
    }

    @Test
    public void testExample()
    {
        final Site site = TestDataFixtures.newSite();
        this.portalRequest.setSite( site );

        runScript( "/site/lib/xp/examples/portal/getSite.js" );
    }
}
