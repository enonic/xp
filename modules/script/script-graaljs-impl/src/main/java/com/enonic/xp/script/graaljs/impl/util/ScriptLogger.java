package com.enonic.xp.script.graaljs.impl.util;

import java.util.HashMap;
import java.util.Map;

import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.resource.ResourceKey;

public final class ScriptLogger
{
    private final ResourceKey source;

    private final Logger log;

    public ScriptLogger( final ResourceKey source )
    {
        this.source = source;
        this.log = LoggerFactory.getLogger( this.source.getApplicationKey().toString() );
    }

    public ProxyObject asProxyObject()
    {
        final Map<String, Object> proxyAsMap = new HashMap<>();

        proxyAsMap.put( "info", new LogProxyExecutable( source, log, LogLevelType.INFO ) );
        proxyAsMap.put( "warning", new LogProxyExecutable( source, log, LogLevelType.WARN ) );
        proxyAsMap.put( "debug", new LogProxyExecutable( source, log, LogLevelType.DEBUG ) );
        proxyAsMap.put( "error", new LogProxyExecutable( source, log, LogLevelType.ERROR ) );

        return ProxyObject.fromMap( proxyAsMap );
    }

    private enum LogLevelType
    {
        INFO( "info" ), WARN( "warning" ), DEBUG( "debug" ), ERROR( "error" );

        private final String level;

        LogLevelType( final String level )
        {
            this.level = level;
        }
    }

    private static class LogProxyExecutable
        implements ProxyExecutable
    {
        private final ResourceKey source;

        private final Logger log;

        private final LogLevelType logLevel;

        private LogProxyExecutable( final ResourceKey source, final Logger log, final LogLevelType logLevel )
        {
            this.source = source;
            this.log = log;
            this.logLevel = logLevel;
        }

        @Override
        public Object execute( final Value... arguments )
        {
            log( arguments );
            return null;
        }

        void log( final Value... arguments )
        {
            if ( arguments.length == 0 )
            {
                throw new IllegalArgumentException( "log." + logLevel.level + "(...) must have at least one parameter" );
            }
            else if ( arguments.length == 2 && arguments[1].isException() )
            {
                PolyglotException ex = arguments[1].as( PolyglotException.class );
                doLog( arguments[0].asString(), ex );
            }
            else
            {
                doLog( arguments );
            }
        }

        void doLog( final String message, final PolyglotException ex )
        {
            switch ( logLevel )
            {
                case INFO:
                    log.info( message, ex );
                    break;
                case WARN:
                    log.warn( message, ex );
                    break;
                case DEBUG:
                    log.debug( message, ex );
                    break;
                default:
                    log.error( message, ex );
            }
        }

        void doLog( final Value... arguments )
        {
            switch ( logLevel )
            {
                case INFO:
                    log.info( format( arguments ) );
                    break;
                case WARN:
                    log.warn( format( arguments ) );
                    break;
                case DEBUG:
                    log.debug( format( arguments ) );
                    break;
                default:
                    log.error( format( arguments ) );
            }
        }

        String format( final Value... arguments )
        {
            final String prefix = "(" + source.getPath() + ") ";
            final String message = arguments[0].asString();

            if ( arguments.length == 1 )
            {
                return prefix + message;
            }
            else
            {
                final Object[] convertedArgs = new Object[arguments.length - 1];
                for ( int i = 0; i < arguments.length - 1; i++ )
                {
                    convertedArgs[i] = arguments[i + 1].asString();
                }
                return prefix + String.format( message, convertedArgs );
            }
        }
    }
}
