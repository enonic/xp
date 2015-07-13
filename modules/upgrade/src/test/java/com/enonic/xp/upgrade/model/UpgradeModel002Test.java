package com.enonic.xp.upgrade.model;

import java.nio.file.Paths;

import org.junit.Test;

import static org.junit.Assert.*;

public class UpgradeModel002Test
    extends AbstractUpgradeModelTest
{

    @Test
    public void testSupports()
        throws Exception
    {
        final UpgradeModel002 upgradeModel = new UpgradeModel002();

        assertTrue( upgradeModel.supports( Paths.get( "test", "_", "node.xml" ), "cms-repo", "draft" ) );
        assertTrue( upgradeModel.supports( Paths.get( "test", "test", "_", "node.xml" ), "cms-repo", "draft" ) );
        assertFalse( upgradeModel.supports( Paths.get( "test", "test", "_", "node.xml" ), "system-repo", "draft" ) );

    }

    @Test
    public void rename_image_elements_with_hypen_to_camelCase()
        throws Exception
    {
        final UpgradeModel002 upgradeModel002 = new UpgradeModel002();

        final String upgraded = upgradeModel002.upgrade( Paths.get( "/test" ), getSource( "upgrademodel002.xml" ) );

        assertResult( upgraded, "upgrademodel002_result.xml" );

    }


}