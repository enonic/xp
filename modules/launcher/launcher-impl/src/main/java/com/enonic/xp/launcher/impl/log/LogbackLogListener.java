package com.enonic.xp.launcher.impl.log;

import java.util.List;
import java.util.Map;

import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogLevel;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.admin.LoggerAdmin;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.jul.LevelChangePropagator;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.classic.spi.LoggingEvent;

public class LogbackLogListener
    implements LogListener, LoggerContextListener
{
    private static final String EVENTS_BUNDLE = "Events.Bundle";

    private static final String EVENTS_FRAMEWORK = "Events.Framework";

    private static final String EVENTS_SERVICE = "Events.Service";

    private static final String LOG_SERVICE = "LogService";

    private static final List<String> OSGI_LOGGERS = List.of( EVENTS_BUNDLE, EVENTS_FRAMEWORK, EVENTS_SERVICE, LOG_SERVICE );

    private final LoggerContext loggerContext;

    private final org.osgi.service.log.admin.LoggerContext osgiLoggerContext;

    public LogbackLogListener( final LoggerAdmin loggerAdmin )
    {
        this.osgiLoggerContext = loggerAdmin.getLoggerContext( null );
        this.loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        updateLoggerContext( this.loggerContext );

        final LevelChangePropagator levelChangePropagator = new LevelChangePropagator();
        levelChangePropagator.setResetJUL( true );
        this.loggerContext.addListener( levelChangePropagator );

        this.loggerContext.addListener( this );
    }

    @Override
    public void logged( final LogEntry entry )
    {
        final String loggerName = entry.getLoggerName();

        if ( !osgiLoggerContext.getEffectiveLogLevel( loggerName ).implies( entry.getLogLevel() ) )
        {
            return;
        }

        final Logger logger = loggerContext.getLogger( loggerName );
        final Level level = toLogbackLevel( entry.getLogLevel() );

        if ( !logger.isEnabledFor( level ) )
        {
            return;
        }

        String message = entry.getMessage();
        Object[] arguments = null;
        boolean noCallerData = false;
        if ( loggerName.startsWith( EVENTS_BUNDLE ) || loggerName.startsWith( EVENTS_FRAMEWORK ) || loggerName.startsWith( LOG_SERVICE ) )
        {
            noCallerData = true;
        }
        else if ( loggerName.startsWith( EVENTS_SERVICE ) )
        {
            message = message + " {}";
            arguments = new Object[]{entry.getServiceReference()};
            noCallerData = true;
        }

        final LoggingEvent loggingEvent = new OsgiLoggingEvent( logger, level, message, entry, arguments, noCallerData );
        loggingEvent.setThreadName( entry.getThreadInfo() );
        loggingEvent.setTimeStamp( entry.getTime() );

        logger.callAppenders( loggingEvent );
    }

    @Override
    public void onLevelChange( final Logger logger, final Level level )
    {
        final Map<String, LogLevel> updatedLevels = this.osgiLoggerContext.getLogLevels();

        updateLevel( updatedLevels, level, logger.getName() );

        this.osgiLoggerContext.setLogLevels( updatedLevels );
    }

    @Override
    public boolean isResetResistant()
    {
        return true;
    }

    @Override
    public void onStart( final LoggerContext context )
    {
    }

    @Override
    public void onStop( final LoggerContext context )
    {
    }

    @Override
    public void onReset( final LoggerContext context )
    {
        updateLoggerContext( context );
    }

    private void updateLoggerContext( final LoggerContext context )
    {
        final Map<String, LogLevel> updatedLevels = osgiLoggerContext.getLogLevels();
        updatedLevels.put( org.osgi.service.log.Logger.ROOT_LOGGER_NAME, toOsgiLogLevel( context.getLogger( Logger.ROOT_LOGGER_NAME ).getLevel() ) );

        final List<Logger> loggerList = context.getLoggerList();

        OSGI_LOGGERS.stream()
            .map( context::getLogger )
            .filter( l -> l.getLevel() == null )
            .forEach( l -> updatedLevels.put( l.getName(), LogLevel.WARN ) );

        for ( Logger logger : loggerList )
        {
            updateLevel( updatedLevels, logger.getLevel(), logger.getName() );
        }

        this.osgiLoggerContext.setLogLevels( updatedLevels );
    }

    private static void updateLevel( final Map<String, LogLevel> updatedLevels, final Level level, final String loggerName )
    {
        if ( level == null )
        {
            return;
        }
        if ( level.toInt() == Level.OFF_INT )
        {
            updatedLevels.remove( loggerName );
        }
        else
        {
            updatedLevels.put( loggerName, toOsgiLogLevel( level ) );
        }
    }

    private static LogLevel toOsgiLogLevel( final Level level )
    {
        if ( level == null )
        {
            return LogLevel.WARN;
        }

        return switch ( level.toInt() )
        {
            case Level.ALL_INT, Level.TRACE_INT -> LogLevel.TRACE;
            case Level.DEBUG_INT -> LogLevel.DEBUG;
            case Level.ERROR_INT -> LogLevel.ERROR;
            case Level.INFO_INT -> LogLevel.INFO;
            default -> LogLevel.WARN;
        };
    }

    private static Level toLogbackLevel( final LogLevel logLevel )
    {
        return switch ( logLevel )
        {
            case AUDIT, TRACE -> Level.TRACE;
            case DEBUG -> Level.DEBUG;
            case ERROR -> Level.ERROR;
            case INFO -> Level.INFO;
            default -> Level.WARN;
        };
    }

    private static class OsgiLoggingEvent
        extends LoggingEvent
    {
        private final boolean noCallerData;

        OsgiLoggingEvent( final Logger logger, final Level level, final String message, final LogEntry entry,
                                 final Object[] arguments, final boolean noCallerData )
        {
            super( org.osgi.service.log.Logger.class.getName(), logger, level, message, entry.getException(), arguments );
            this.noCallerData = noCallerData;
        }

        @Override
        public StackTraceElement[] getCallerData()
        {
            return noCallerData ? super.getCallerData() : null;
        }
    }
}
