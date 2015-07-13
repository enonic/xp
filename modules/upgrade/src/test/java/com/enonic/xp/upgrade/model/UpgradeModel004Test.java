package com.enonic.xp.upgrade.model;

import java.nio.file.Paths;

import org.junit.Test;

import static org.junit.Assert.*;

public class UpgradeModel004Test
    extends AbstractUpgradeModelTest
{


    @Test
    public void testSupports()
        throws Exception
    {
        final UpgradeModel004 upgradeModel = new UpgradeModel004();

        assertTrue( upgradeModel.supports( Paths.get( "test", "_", "node.xml" ), "cms-repo", "draft" ) );
        assertTrue( upgradeModel.supports( Paths.get( "test", "test", "_", "node.xml" ), "cms-repo", "draft" ) );
        assertFalse( upgradeModel.supports( Paths.get( "test", "test", "_", "node.xml" ), "system-repo", "draft" ) );

    }

    @Test
    public void remove_pre_scaled_attachments()
        throws Exception
    {
        final UpgradeModel004 upgradeModel = new UpgradeModel004();

        final String upgraded = upgradeModel.upgrade( Paths.get( "/test" ), getSource( "upgrademodel004.xml" ) );

        assertResult( upgraded, "upgrademodel004_result.xml" );
    }

    @Test
    public void no_attachments()
        throws Exception
    {
        final UpgradeModel004 upgradeModel = new UpgradeModel004();

        final String upgraded = upgradeModel.upgrade( Paths.get( "/test" ), getSource( "upgrademodel004_no_attachments.xml" ) );

        assertResult( upgraded, "upgrademodel004_no_attachments_result.xml" );
    }

    @Test
    public void only_source_attachment()
        throws Exception
    {
        final UpgradeModel004 upgradeModel = new UpgradeModel004();

        final String upgraded = upgradeModel.upgrade( Paths.get( "/test" ), getSource( "upgrademodel004_only_source_attach.xml" ) );

        assertResult( upgraded, "upgrademodel004_only_source_attach_result.xml" );
    }

}