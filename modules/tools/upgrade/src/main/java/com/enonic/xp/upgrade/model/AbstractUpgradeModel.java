package com.enonic.xp.upgrade.model;

import java.util.logging.Logger;

public abstract class AbstractUpgradeModel
    implements UpgradeModel
{
    protected Logger LOG = Logger.getLogger( "Upgrade" );

    public void log()
    {
        this.LOG.info( getLogMsg() );
    }

    protected abstract String getLogMsg();

}
