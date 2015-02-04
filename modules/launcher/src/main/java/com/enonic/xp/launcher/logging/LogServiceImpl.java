package com.enonic.xp.launcher.logging;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class LogServiceImpl
    implements LogService
{
    private final Logger logger;

    public LogServiceImpl( final Bundle bundle )
    {
        this.logger = LoggerFactory.getLogger( bundle.getSymbolicName() );
    }

    @Override
    public void log( final int level, final String message )
    {
        switch ( level )
        {
            case LogService.LOG_DEBUG:
                this.logger.debug( message );
                break;
            case LogService.LOG_INFO:
                this.logger.info( message );
                break;
            case LogService.LOG_WARNING:
                this.logger.warn( message );
                break;
            case LogService.LOG_ERROR:
                this.logger.error( message );
                break;
        }
    }

    @Override
    public void log( final int level, final String message, final Throwable cause )
    {
        switch ( level )
        {
            case LogService.LOG_DEBUG:
                this.logger.debug( message, cause );
                break;
            case LogService.LOG_INFO:
                this.logger.info( message, cause );
                break;
            case LogService.LOG_WARNING:
                this.logger.warn( message, cause );
                break;
            case LogService.LOG_ERROR:
                this.logger.error( message, cause );
                break;
        }
    }

    @Override
    public void log( final ServiceReference ref, final int level, final String message )
    {
        log( level, message );
    }

    @Override
    public void log( final ServiceReference ref, final int level, final String message, final Throwable cause )
    {
        log( level, message, cause );
    }
}
