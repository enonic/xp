package com.enonic.xp.script.graal.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Map;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyExecutable;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.impl.util.LogArgConverter;
import com.enonic.xp.script.impl.util.ObjectConverter;

public final class GraalScriptLogger
{
    private final Context context;

    private final LogArgConverter converter;

    private final ResourceKey source;

    private final Logger log;

    public GraalScriptLogger( final Context context, final ResourceKey source, final ObjectConverter objectConverter )
    {
        this( context, source, objectConverter, LoggerFactory.getLogger( source.getApplicationKey().toString() ) );
    }

    GraalScriptLogger( final Context context, final ResourceKey source, final ObjectConverter objectConverter, final Logger log )
    {
        this.context = context;
        this.source = source;
        this.log = log;
        this.converter = new LogArgConverter( objectConverter );
    }

    public ProxyObject asProxyObject()
    {
        final Map<String, Object> proxyAsMap = new HashMap<>();

        proxyAsMap.put( "info", new LogProxyExecutable( LogLevelType.INFO ) );
        proxyAsMap.put( "warning", new LogProxyExecutable( LogLevelType.WARN ) );
        proxyAsMap.put( "debug", new LogProxyExecutable( LogLevelType.DEBUG ) );
        proxyAsMap.put( "error", new LogProxyExecutable( LogLevelType.ERROR ) );
        // the other engine exposes the logger bean itself, format() included
        proxyAsMap.put( "format", (ProxyExecutable) arguments -> {
            synchronized ( context )
            {
                return format( arguments );
            }
        } );

        return ProxyObject.fromMap( proxyAsMap );
    }

    private String format( final Value... arguments )
    {
        if ( arguments.length == 0 )
        {
            throw new IllegalArgumentException( "log.format(...) must have at least one parameter" );
        }

        final Value message = arguments[0];
        final String text = message.isString() ? message.asString() : message.toString();
        return format( text, Arrays.stream( arguments ).skip( 1 ).toArray() );
    }

    private String format( final String message, final Object[] args )
    {
        final String prefix = "(" + source.getPath() + ") ";
        if ( args.length == 0 )
        {
            return prefix + message;
        }
        final Object[] converted = converter.convertArgs( args );
        try
        {
            return prefix + String.format( message, converted );
        }
        catch ( IllegalFormatException e )
        {
            return prefix + message + " " + Arrays.toString( converted );
        }
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

    private final class LogProxyExecutable
        implements ProxyExecutable
    {
        private final LogLevelType logLevel;

        private LogProxyExecutable( final LogLevelType logLevel )
        {
            this.logLevel = logLevel;
        }

        @Override
        public Object execute( final Value... arguments )
        {
            synchronized ( context )
            {
                log( arguments );
            }
            return null;
        }

        void log( final Value... arguments )
        {
            if ( arguments.length == 0 )
            {
                throw new IllegalArgumentException( "log." + logLevel.level + "(...) must have at least one parameter" );
            }
            else
            {
                final Value argument = arguments[0];
                if ( arguments.length == 2 && arguments[1].isException() )
                {
                    PolyglotException ex = arguments[1].as( PolyglotException.class );
                    doLog( argument.isString() ? argument.asString() : argument.toString(), ex );
                }
                else
                {
                    doLog( argument.isString() ? argument.asString() : argument.toString(),
                           Arrays.stream( arguments ).skip( 1 ).toArray() );
                }
            }
        }

        void doLog( final String message, final PolyglotException ex )
        {
            final String text = format( message, new Object[0] );
            switch ( logLevel )
            {
                case INFO:
                    log.info( text, ex );
                    break;
                case WARN:
                    log.warn( text, ex );
                    break;
                case DEBUG:
                    log.debug( text, ex );
                    break;
                default:
                    log.error( text, ex );
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
    }
}
