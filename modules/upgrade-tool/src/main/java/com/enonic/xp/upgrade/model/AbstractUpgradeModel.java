package com.enonic.xp.upgrade.model;

import java.util.logging.Logger;

public abstract class AbstractUpgradeModel
    implements UpgradeModel
{
    private static final Logger LOG = Logger.getLogger( "upgrade" );

    protected final void log( final String message )
    {
        LOG.info( message );
    }
}
