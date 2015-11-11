package com.enonic.xp.lib.portal.current;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.lib.portal.TestDataFixtures;
import com.enonic.xp.site.Site;
import com.enonic.xp.testing.script.OldScriptTestSupport;

public class GetCurrentSiteScriptTest
    extends OldScriptTestSupport
{
    @Before
    public void setUp()
    {
        setupRequest();
    }

    @Test
    public void currentSite()
        throws Exception
    {
        final Site site = TestDataFixtures.newSite();
        this.portalRequest.setSite( site );

        runTestFunction( "/test/getCurrentSite-test.js", "currentSite" );
    }

    @Test
    public void noCurrentSite()
        throws Exception
    {
        this.portalRequest.setSite( null );
        runTestFunction( "/test/getCurrentSite-test.js", "noCurrentSite" );
    }
}
