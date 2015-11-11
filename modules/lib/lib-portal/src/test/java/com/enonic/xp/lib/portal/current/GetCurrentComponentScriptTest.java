package com.enonic.xp.lib.portal.current;

import org.junit.Test;

import com.enonic.xp.lib.portal.TestDataFixtures;
import com.enonic.xp.region.Component;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class GetCurrentComponentScriptTest
    extends ScriptTestSupport
{
    @Test
    public void currentComponent()
    {
        final Component component = TestDataFixtures.newLayoutComponent();
        this.portalRequest.setComponent( component );

        runFunction( "/site/test/getCurrentComponent-test.js", "currentComponent" );
    }

    @Test
    public void noCurrentComponent()
    {
        this.portalRequest.setComponent( null );
        runFunction( "/site/test/getCurrentComponent-test.js", "noCurrentComponent" );
    }
}
