package com.enonic.xp.upgrade.model;

import java.nio.file.Paths;

import org.junit.Test;

public class UpgradeModel002Test
    extends AbstractUpgradeModelTest
{
    @Test
    public void rename_image_elements_with_hypen_to_camelCase()
        throws Exception
    {
        final UpgradeModel002 upgradeModel002 = new UpgradeModel002();

        final String upgraded = upgradeModel002.upgrade( Paths.get( "/test" ), getSource( "upgrademodel002.xml" ) );

        assertResult( upgraded, "upgrademodel002_result.xml" );

    }


}