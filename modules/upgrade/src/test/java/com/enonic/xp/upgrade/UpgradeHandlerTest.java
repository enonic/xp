package com.enonic.xp.upgrade;

import java.nio.file.Paths;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class UpgradeHandlerTest
{
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void initialize()
        throws Exception
    {
        UpgradeHandler.create().
            sourceRoot( temporaryFolder.getRoot().toPath() ).
            build().
            execute();
    }

    @Test(expected = UpgradeException.class)
    public void non_exisiting_root()
        throws Exception
    {
        UpgradeHandler.create().
            sourceRoot( Paths.get( "bogus" ) ).
            build().
            execute();
    }
}