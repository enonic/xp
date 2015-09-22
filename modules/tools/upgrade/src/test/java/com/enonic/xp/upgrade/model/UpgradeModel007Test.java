package com.enonic.xp.upgrade.model;

import java.nio.file.Paths;

import org.junit.Test;

import static org.junit.Assert.*;

public class UpgradeModel007Test
    extends AbstractUpgradeModelTest
{
    @Test
    public void testSupports()
        throws Exception
    {
        final UpgradeModel007 upgradeModel = new UpgradeModel007();
        assertTrue( upgradeModel.supports( Paths.get( "test", "_", "node.xml" ), "cms-repo", "draft" ) );
        assertTrue( upgradeModel.supports( Paths.get( "test", "test", "_", "node.xml" ), "cms-repo", "draft" ) );
        assertFalse( upgradeModel.supports( Paths.get( "test", "test", "_", "node.xml" ), "system-repo", "draft" ) );
    }

    @Test
    public void add_timestamp()
        throws Exception
    {
        final UpgradeModel007 upgradeModel = new UpgradeModel007();

        final String upgraded = upgradeModel.upgrade( Paths.get( "/test" ), getSource( "upgrademodel007.xml" ) );

        assertResult( upgraded, "upgrademodel007_result.xml" );
    }

    @Test
    public void has_timestamp_already()
        throws Exception
    {
        final UpgradeModel007 upgradeModel = new UpgradeModel007();

        final String upgraded = upgradeModel.upgrade( Paths.get( "/test" ), getSource( "upgrademodel007_has_timestamp.xml" ) );

        assertResult( upgraded, "upgrademodel007_has_timestamp_result.xml" );
    }

    @Test
    public void only_createdTime()
        throws Exception
    {
        final UpgradeModel007 upgradeModel = new UpgradeModel007();

        final String upgraded = upgradeModel.upgrade( Paths.get( "/test" ), getSource( "upgrademodel007_only_createdtime.xml" ) );

        assertResult( upgraded, "upgrademodel007_only_createdtime_result.xml" );
    }

}