package com.enonic.xp.upgrade;

import java.io.File;
import java.util.logging.Logger;

public final class UpgradeTool
{
    private static final Logger LOG = Logger.getLogger( UpgradeTool.class.getName() );

    public static void main( String... args )
    {
        if ( args.length != 1 )
        {
            throw new UpgradeException( "source-path must be provided" );
        }

        final File sourceRoot = new File( args[0] );

        if ( !sourceRoot.exists() )
        {
            throw new UpgradeException( "Source-root does not exist" );
        }

        for ( final String arg : args )
        {
            System.out.println( arg );
        }

        UpgradeHandler.create().
            sourceRoot( sourceRoot.toPath() ).
            upgradeModels( new UpgradeTaskLocator() ).
            build().
            execute();
    }
}
