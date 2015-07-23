package com.enonic.xp.lib.portal.current;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.lib.portal.TestDataFixtures;
import com.enonic.xp.site.Site;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class GetCurrentSiteScriptTest
    extends ScriptTestSupport
{
    @Before
    public void setUp()
        throws Exception
    {
        setupRequest();

        mockResource( "mymodule:/test/getCurrentSite-test.js" );
        mockResource( "mymodule:/site/lib/xp/portal.js" );
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
