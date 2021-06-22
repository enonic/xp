package com.enonic.xp.script.graaljs.impl.util;

import java.util.Arrays;
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
    private final LogArgConverter converter;

    private final ResourceKey source;

    private final Logger log;

    public ScriptLogger( final ResourceKey source, final JavascriptHelper helper )
    {
        this.source = source;
        this.log = LoggerFactory.getLogger( this.source.getApplicationKey().toString() );
        this.converter = new LogArgConverter( helper );
    }

    public ProxyObject asProxyObject()
    {
        final Map<String, Object> proxyAsMap = new HashMap<>();

        proxyAsMap.put( "info", new LogProxyExecutable( source, log, converter, LogLevelType.INFO ) );
        proxyAsMap.put( "warning", new LogProxyExecutable( source, log, converter, LogLevelType.WARN ) );
        proxyAsMap.put( "debug", new LogProxyExecutable( source, log, converter, LogLevelType.DEBUG ) );
        proxyAsMap.put( "error", new LogProxyExecutable( source, log, converter, LogLevelType.ERROR ) );

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

        private final LogArgConverter converter;

        private final LogLevelType logLevel;

        private LogProxyExecutable( final ResourceKey source, final Logger log, final LogArgConverter converter,
                                    final LogLevelType logLevel )
        {
            this.source = source;
            this.log = log;
            this.converter = converter;
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
                doLog( arguments[0].asString(), Arrays.stream( arguments ).skip( 1 ).toArray() );
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

        void doLog( final String message, final Object... arguments )
        {
            switch ( logLevel )
            {
                case INFO:
                    log.info( format( message, arguments ) );
                    break;
                case WARN:
                    log.warn( format( message, arguments ) );
                    break;
                case DEBUG:
                    log.debug( format( message, arguments ) );
                    break;
                default:
                    log.error( format( message, arguments ) );
            }
        }

        String format( final String message, final Object... arguments )
        {
            String prefix = "(" + source.getPath() + ") ";

            if ( arguments.length == 1 )
            {
                return prefix + message;
            }
            else
            {
                Object[] convertedArgs = this.converter.convertArgs( arguments );
                return prefix + String.format( message, convertedArgs );
            }
        }
    }
}
