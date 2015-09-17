package com.enonic.xp.upgrade.model;

import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

public class UpgradeExportPropertiesTest
    extends AbstractUpgradeModelTest
{
    @Test
    public void replace_export_properties()
        throws Exception
    {
        final UpgradeExportProperties upgradeExportProperties = new UpgradeExportProperties();

        final String upgraded = upgradeExportProperties.upgrade( Paths.get( "/test" ), getSource( "export.properties" ) );

        Assert.assertEquals( upgraded, getSource( "export_result.properties" ).read() );
    }
}