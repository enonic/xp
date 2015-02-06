package com.enonic.xp.launcher.framework;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class FrameworkLogger
    extends org.apache.felix.framework.Logger
{
    private final Logger logger;

    public FrameworkLogger()
    {
        this.logger = LoggerFactory.getLogger( getClass() );
    }

    private Logger getLogger( final Bundle bundle )
    {
        return bundle != null ? LoggerFactory.getLogger( bundle.getSymbolicName() ) : this.logger;
    }

    @Override
    protected void doLog( final Bundle bundle, final ServiceReference sr, final int level, final String msg, final Throwable cause )
    {
        final Logger logger = getLogger( bundle );
        doLog( logger, level, msg, cause );
    }

    private void doLog( final Logger logger, final int level, final String msg, final Throwable cause )
    {
        switch ( level )
        {
            case LOG_DEBUG:
                logger.debug( msg, cause );
                break;
            case LOG_INFO:
                logger.info( msg, cause );
                break;
            case LOG_WARNING:
                logger.warn( msg, cause );
                break;
            case LOG_ERROR:
                logger.error( msg, cause );
                break;
        }
    }
}
