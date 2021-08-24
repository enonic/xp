package com.enonic.xp.script.impl.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.script.ScriptException;

import org.graalvm.polyglot.PolyglotException;

import com.oracle.truffle.js.runtime.GraalJSException;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.util.Exceptions;

public final class ErrorHelper
{
    public static RuntimeException handleError( final Exception e )
    {
        if ( e instanceof ResourceProblemException )
        {
            return (ResourceProblemException) e;
        }
        else if ( e instanceof ScriptException )
        {
            return doHandleException( (ScriptException) e );
        }
        else if ( e instanceof GraalJSException )
        {
            return doHandleException( (GraalJSException) e );
        }
        else if ( e instanceof RuntimeException )
        {
            return doHandleException( (RuntimeException) e );
        }
        else
        {
            return Exceptions.unchecked( e );
        }
    }

    private static ResourceProblemException doHandleException( final ScriptException e )
    {
        final ResourceProblemException.Builder builder = ResourceProblemException.create();
        builder.cause( e.getCause() );
        builder.lineNumber( e.getLineNumber() );
        builder.resource( toResourceKey( e.getFileName() ) );
        return builder.build();
    }

    private static RuntimeException doHandleException( final GraalJSException e )
    {
        final StackTraceElement element = findScriptTraceElement( e );

        if ( element == null )
        {
            return e;
        }

        final ResourceProblemException.Builder builder = ResourceProblemException.create();
        builder.cause( e.getCause() );
        builder.lineNumber( element.getLineNumber() );
        builder.resource( toResourceKey( element.getFileName() ) );
        return builder.build();
    }

    private static RuntimeException doHandleException( final RuntimeException e )
    {
        final StackTraceElement elem = findScriptTraceElement( e );
        if ( elem == null )
        {
            return e;
        }

        final ResourceProblemException.Builder builder = ResourceProblemException.create();
        builder.cause( e instanceof PolyglotException && ( (PolyglotException) e ).isHostException()
                           ? ( (PolyglotException) e ).asHostException()
                           : e );
        builder.lineNumber( elem.getLineNumber() );
        builder.resource( toResourceKey( elem.getFileName() ) );
        return builder.build();
    }

    private static ResourceKey toResourceKey( final String name )
    {
        try
        {
            return ResourceKey.from( name );
        }
        catch ( final IllegalArgumentException e )
        {
            return null;
        }
    }

    private static StackTraceElement findScriptTraceElement( final RuntimeException e )
    {
        final List<StackTraceElement> elements = getScriptFrames( e );
        return elements.size() > 0 ? elements.get( 0 ) : null;
    }

    private static List<StackTraceElement> getScriptFrames( final RuntimeException exception )
    {
        return Arrays.stream( exception.getStackTrace() ).
            filter( ErrorHelper::isScriptFrame ).
            map( stackTraceElement -> {
                final String className = "<" + stackTraceElement.getFileName() + ">";
                String methodName = stackTraceElement.getMethodName();
                if ( methodName.equals( ":program" ) )
                {
                    methodName = "<program>";
                }
                else
                {
                    methodName = stripMethodName( methodName );
                }

                return new StackTraceElement( className, methodName, stackTraceElement.getFileName(), stackTraceElement.getLineNumber() );
            } ).collect( Collectors.toList() );
    }

    private static String stripMethodName( final String methodName )
    {
        String name = methodName;

        final int nestedSeparator = name.lastIndexOf( "#" );
        if ( nestedSeparator >= 0 )
        {
            name = name.substring( nestedSeparator + 1 );
        }

        final int idSeparator = name.indexOf( "-" );
        if ( idSeparator >= 0 )
        {
            name = name.substring( 0, idSeparator );
        }

        return name.contains( "L:" ) ? "<anonymous>" : name;
    }

    private static boolean isScriptFrame( final StackTraceElement frame )
    {
        final String className = frame.getClassName();

        if ( className.toLowerCase().startsWith( "<js>" ) && !isInternalMethodName( frame.getMethodName() ) )
        {
            final String source = frame.getFileName();
            return source != null && !source.endsWith( ".java" );
        }
        return false;
    }

    private static boolean isInternalMethodName( final String methodName )
    {
        return methodName.startsWith( ":" ) && !methodName.equals( ":program" );
    }
}
