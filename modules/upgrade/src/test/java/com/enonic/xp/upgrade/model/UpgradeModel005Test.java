package com.enonic.xp.upgrade.model;

import java.nio.file.Paths;

import org.junit.Test;

import static org.junit.Assert.*;

public class UpgradeModel005Test
    extends AbstractUpgradeModelTest
{
    @Test
    public void testSupports()
        throws Exception
    {
        final UpgradeModel005 upgradeModel = new UpgradeModel005();

        assertTrue( upgradeModel.supports( Paths.get( "test", "_", "node.xml" ), "cms-repo", "draft" ) );
        assertTrue( upgradeModel.supports( Paths.get( "test", "test", "_", "node.xml" ), "cms-repo", "draft" ) );
        assertTrue( upgradeModel.supports( Paths.get( "test", "test", "_", "node.xml" ), "system-repo", "draft" ) );
    }

    @Test
    public void rename_content_default_analyzer()
        throws Exception
    {
        final UpgradeModel005 upgradeModel = new UpgradeModel005();

        final String upgraded = upgradeModel.upgrade( Paths.get( "/test" ), getSource( "upgrademodel005.xml" ) );

        assertResult( upgraded, "upgrademodel005_result.xml" );
    }

    @Test
    public void rename_not_content_default_analyzer()
        throws Exception
    {
        final UpgradeModel005 upgradeModel = new UpgradeModel005();

        final String upgraded = upgradeModel.upgrade( Paths.get( "/test" ), getSource( "upgrademodel005_non_default.xml" ) );

        assertResult( upgraded, "upgrademodel005_non_default_result.xml" );
    }

}