package com.enonic.xp.upgrade.model;

import java.nio.file.Paths;

import org.junit.Test;

import static org.junit.Assert.*;

public class UpgradeModel003Test
    extends AbstractUpgradeModelTest
{

    @Test
    public void testSupports()
        throws Exception
    {
        final UpgradeModel003 upgradeModel = new UpgradeModel003();

        assertTrue( upgradeModel.supports( Paths.get( "test", "_", "node.xml" ), "cms-repo", "draft" ) );
        assertTrue( upgradeModel.supports( Paths.get( "test", "test", "_", "node.xml" ), "cms-repo", "draft" ) );
        assertFalse( upgradeModel.supports( Paths.get( "test", "test", "_", "node.xml" ), "system-repo", "draft" ) );

    }

    @Test
    public void rename_moduleConfig_to_siteConfig()
        throws Exception
    {
        final UpgradeModel003 upgradeModel = new UpgradeModel003();

        final String upgraded = upgradeModel.upgrade( Paths.get( "/test" ), getSource( "upgrademodel003.xml" ) );

        assertResult( upgraded, "upgrademodel003_result.xml" );

    }


    @Test
    public void dont_touch_other_nodes()
        throws Exception
    {
        final UpgradeModel003 upgradeModel = new UpgradeModel003();

        final String upgraded = upgradeModel.upgrade( Paths.get( "/test" ), getSource( "upgrademodel003_not_site.xml" ) );

        assertResult( upgraded, "upgrademodel003_not_site_result.xml" );

    }

    @Test
    public void rename_multiple_moduleConfig_to_siteConfig()
        throws Exception
    {
        final UpgradeModel003 upgradeModel = new UpgradeModel003();

        final String upgraded = upgradeModel.upgrade( Paths.get( "/test" ), getSource( "upgrademodel003_multi_modules.xml" ) );

        assertResult( upgraded, "upgrademodel003_multi_modules_result.xml" );

    }

}