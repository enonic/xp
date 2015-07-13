package com.enonic.xp.upgrade.model;

import java.nio.file.Paths;

import org.junit.Test;

import static org.junit.Assert.*;

public class UpgradeModel001Test
    extends AbstractUpgradeModelTest
{
    @Test
    public void testSupports()
        throws Exception
    {
        final UpgradeModel001 upgradeModel001 = new UpgradeModel001();

        assertTrue( upgradeModel001.supports( Paths.get( "test", "_", "node.xml" ), "cms-repo", "draft" ) );
        assertTrue( upgradeModel001.supports( Paths.get( "test", "test", "_", "node.xml" ), "cms-repo", "draft" ) );
        assertFalse( upgradeModel001.supports( Paths.get( "test", "test", "_", "node.xml" ), "system-repo", "draft" ) );

    }

    @Test
    public void replace_htmlPart_with_string()
        throws Exception
    {
        final UpgradeModel001 upgradeModel001 = new UpgradeModel001();

        final String upgraded = upgradeModel001.upgrade( Paths.get( "/test" ), getSource( "upgrademodel001.xml" ) );

        assertResult( upgraded, "upgrademodel001_result.xml" );
    }
}