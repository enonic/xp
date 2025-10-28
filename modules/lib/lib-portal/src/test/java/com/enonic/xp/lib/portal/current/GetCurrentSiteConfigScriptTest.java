package com.enonic.xp.lib.portal.current;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.lib.portal.TestDataFixtures;
import com.enonic.xp.site.Site;
import com.enonic.xp.testing.ScriptTestSupport;

class GetCurrentSiteConfigScriptTest
    extends ScriptTestSupport
{
    @Test
    void currentSite()
    {
        final Site site = TestDataFixtures.newSite().build();
        this.portalRequest.setSite( site );

        runFunction( "/test/getCurrentSiteConfig-test.js", "currentSite" );
    }

    @Test
    void noCurrentSite()
    {
        this.portalRequest.setSite( null );
        runFunction( "/test/getCurrentSiteConfig-test.js", "noCurrentSite" );
    }

    @Test
    void noCurrentApplication()
    {
        this.portalRequest.setApplicationKey( null );
        runFunction( "/test/getCurrentSiteConfig-test.js", "noCurrentSite" );
    }

    @Test
    void currentSiteByContentPath()
    {
        final Content content = TestDataFixtures.newContent();
        this.portalRequest.setContent( null );
        this.portalRequest.setContentPath( content.getPath() );
        this.portalRequest.setSite( null );
        runFunction( "/test/getCurrentSiteConfig-test.js", "noCurrentSite" );
    }

    @Test
    void testExample()
    {
        final Site site = TestDataFixtures.newSite().build();
        this.portalRequest.setSite( site );

        runScript( "/lib/xp/examples/portal/getSiteConfig.js" );
    }

    @Test
    void configFromProject()
    {
        this.portalRequest.setSite( null );
        this.portalRequest.setProject( TestDataFixtures.newDefaultProject().build() );

        runFunction( "/test/getCurrentSiteConfig-test.js", "configFromProject" );
    }
}
