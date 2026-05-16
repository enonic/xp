package com.enonic.xp.lib.portal.current;

import org.junit.jupiter.api.Test;

import com.enonic.xp.lib.portal.TestDataFixtures;
import com.enonic.xp.region.Component;
import com.enonic.xp.testing.ScriptTestSupport;

class GetCurrentComponentScriptTest
    extends ScriptTestSupport
{
    @Test
    void currentComponent()
    {
        final Component component = TestDataFixtures.newLayoutComponent();
        this.portalRequest.setComponent( component );

        runFunction( "/test/getCurrentComponent-test.js", "currentComponent" );
    }

    @Test
    void noCurrentComponent()
    {
        this.portalRequest.setComponent( null );
        runFunction( "/test/getCurrentComponent-test.js", "noCurrentComponent" );
    }

    @Test
    void testExample()
    {
        final Component component = TestDataFixtures.newLayoutComponent();
        this.portalRequest.setComponent( component );

        runScript( "/lib/xp/examples/portal/getComponent.js" );
    }

    @Test
    void currentRegions()
    {
        final Component component = TestDataFixtures.newLayoutComponent();
        this.portalRequest.setComponent( component );

        runFunction( "/test/getCurrentComponent-test.js", "currentRegions" );
    }

    @Test
    void noCurrentComponentRegions()
    {
        this.portalRequest.setComponent( null );
        runFunction( "/test/getCurrentComponent-test.js", "noCurrentComponentRegions" );
    }

    @Test
    void testGetRegionsExample()
    {
        final Component component = TestDataFixtures.newLayoutComponent();
        this.portalRequest.setComponent( component );

        runScript( "/lib/xp/examples/portal/getRegions.js" );
    }
}
