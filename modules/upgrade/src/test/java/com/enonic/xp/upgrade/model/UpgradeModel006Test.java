package com.enonic.xp.upgrade.model;

import java.nio.file.Paths;

import org.junit.Test;

import static org.junit.Assert.*;

public class UpgradeModel006Test
    extends AbstractUpgradeModelTest
{
    @Test
    public void testSupports()
        throws Exception
    {
        final UpgradeModel006 upgradeModel = new UpgradeModel006();
        assertTrue( upgradeModel.supports( Paths.get( "test", "_", "node.xml" ), "cms-repo", "draft" ) );
        assertTrue( upgradeModel.supports( Paths.get( "test", "test", "_", "node.xml" ), "cms-repo", "draft" ) );
        assertFalse( upgradeModel.supports( Paths.get( "test", "test", "_", "node.xml" ), "system-repo", "draft" ) );
    }

    @Test
    public void add_configs()
        throws Exception
    {
        final UpgradeModel006 upgradeModel = new UpgradeModel006();

        final String upgraded = upgradeModel.upgrade( Paths.get( "/test" ), getSource( "upgrademodel006.xml" ) );

        assertResult( upgraded, "upgrademodel006_result.xml" );
    }

    @Test
    public void has_configs_already()
        throws Exception
    {
        final UpgradeModel006 upgradeModel = new UpgradeModel006();

        final String upgraded = upgradeModel.upgrade( Paths.get( "/test" ), getSource( "upgrademodel006_has_config.xml" ) );

        assertResult( upgraded, "upgrademodel006_has_config_result.xml" );
    }

}