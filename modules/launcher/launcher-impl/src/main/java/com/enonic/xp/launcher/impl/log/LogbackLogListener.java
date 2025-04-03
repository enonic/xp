package com.enonic.xp.launcher.impl.log;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
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

    private volatile LoggerContext loggerContext;

    private volatile Logger rootLogger;

    private final Map<String, LogLevel> initialLogLevels;

    private final org.osgi.service.log.admin.LoggerContext osgiLoggerContext;

    public LogbackLogListener( final LoggerAdmin loggerAdmin )
    {
        this.osgiLoggerContext = loggerAdmin.getLoggerContext( null );
        this.initialLogLevels = osgiLoggerContext.getLogLevels();

        setLoggerContext( (LoggerContext) LoggerFactory.getILoggerFactory() );
        final LevelChangePropagator levelChangePropagator = new LevelChangePropagator();
        levelChangePropagator.setResetJUL( true );
        this.loggerContext.addListener( levelChangePropagator );
        this.loggerContext.addListener( this );
    }

    private void setLoggerContext( final LoggerContext context )
    {
        this.loggerContext = context;
        this.rootLogger = context.getLogger( Logger.ROOT_LOGGER_NAME );
        this.osgiLoggerContext.setLogLevels( updateLevels( context, initialLogLevels ) );
    }

    @Override
    public boolean isResetResistant()
    {
        return true;
    }

    @Override
    public void logged( final LogEntry entry )
    {
        String loggerName = entry.getLoggerName();
        final String message;
        final Object[] arguments;
        boolean avoidCallerData;
        if ( EVENTS_BUNDLE.equals( loggerName ) || EVENTS_FRAMEWORK.equals( loggerName ) || LOG_SERVICE.equals( loggerName ) )
        {
            Bundle bundle = entry.getBundle();
            loggerName = loggerName + "." + bundle.getSymbolicName();
            message = entry.getMessage();
            arguments = null;
            avoidCallerData = true;
        }
        else if ( loggerName.startsWith( EVENTS_BUNDLE ) || loggerName.startsWith( EVENTS_FRAMEWORK ) ||
            loggerName.startsWith( LOG_SERVICE ) )
        {
            message = entry.getMessage();
            arguments = null;
            avoidCallerData = true;
        }
        else if ( EVENTS_SERVICE.equals( loggerName ) )
        {
            Bundle bundle = entry.getBundle();
            loggerName = loggerName + "." + bundle.getSymbolicName();
            message = entry.getMessage() + " {}";
            arguments = new Object[]{entry.getServiceReference()};
            avoidCallerData = true;
        }
        else if ( loggerName.startsWith( EVENTS_SERVICE ) )
        {
            message = entry.getMessage() + " {}";
            arguments = new Object[]{entry.getServiceReference()};
            avoidCallerData = true;
        }
        else
        {
            message = entry.getMessage();
            arguments = null;
            avoidCallerData = false;
        }

        final Logger logger = loggerContext.getLogger( loggerName );
        final Level level = toLogbackLevel( entry.getLogLevel() );

        if ( !logger.equals( rootLogger ) && !logger.isEnabledFor( level ) )
        {
            return;
        }

        final LoggingEvent loggingEvent =
            new LoggingEvent( org.osgi.service.log.Logger.class.getName(), logger, level, message, entry.getException(), arguments )
            {
                @Override
                public StackTraceElement[] getCallerData()
                {
                    return avoidCallerData ? super.getCallerData() : null;
                }
            };
        loggingEvent.setThreadName( entry.getThreadInfo() );
        loggingEvent.setTimeStamp( entry.getTime() );

        rootLogger.callAppenders( loggingEvent );
    }

    @Override
    public void onLevelChange( final Logger logger, final Level level )
    {
        final Map<String, LogLevel> updatedLevels = this.osgiLoggerContext.getLogLevels();

        if ( Level.OFF.equals( level ) )
        {
            updatedLevels.remove( logger.getName() );
        }
        else
        {
            updatedLevels.put( logger.getName(), toOsgiLogLevel( level ) );
        }

        this.osgiLoggerContext.setLogLevels( updatedLevels );
    }

    @Override
    public void onStart( final LoggerContext context )
    {
        setLoggerContext( context );
    }

    @Override
    public void onStop( final LoggerContext context )
    {
        this.osgiLoggerContext.setLogLevels( initialLogLevels );
    }

    @Override
    public void onReset( final LoggerContext context )
    {
        setLoggerContext( context );
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

    private static Map<String, LogLevel> updateLevels( LoggerContext loggerContext, final Map<String, LogLevel> levels )
    {
        final LogLevel rootLevel = toOsgiLogLevel( loggerContext.getLogger( Logger.ROOT_LOGGER_NAME ).getLevel() );

        final Map<String, LogLevel> updatedLevels = new HashMap<>( levels );
        updatedLevels.put( org.osgi.service.log.Logger.ROOT_LOGGER_NAME, rootLevel );
        updatedLevels.put( EVENTS_BUNDLE, LogLevel.TRACE );
        updatedLevels.put( EVENTS_FRAMEWORK, LogLevel.TRACE );
        updatedLevels.put( EVENTS_SERVICE, LogLevel.TRACE );
        updatedLevels.put( LOG_SERVICE, LogLevel.TRACE );

        for ( Logger logger : loggerContext.getLoggerList() )
        {
            final Level level = logger.getLevel();
            if ( level != null )
            {
                final String name = logger.getName();
                if ( level != Level.OFF )
                {
                    updatedLevels.put( name, toOsgiLogLevel( level ) );
                }
                else
                {
                    updatedLevels.remove( name );
                }
            }
        }
        return updatedLevels;
    }
}
