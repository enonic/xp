package com.enonic.xp.lib.portal.current;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.lib.portal.TestDataFixtures;
import com.enonic.xp.region.Component;
import com.enonic.xp.testing.script.OldScriptTestSupport;

public class GetCurrentComponentScriptTest
    extends OldScriptTestSupport
{
    @Before
    public void setUp()
    {
        setupRequest();
    }

    @Test
    public void currentComponent()
        throws Exception
    {
        final Component component = TestDataFixtures.newLayoutComponent();
        this.portalRequest.setComponent( component );

        runTestFunction( "/test/getCurrentComponent-test.js", "currentComponent" );
    }

    @Test
    public void noCurrentComponent()
        throws Exception
    {
        this.portalRequest.setComponent( null );
        runTestFunction( "/test/getCurrentComponent-test.js", "noCurrentComponent" );
    }
}
