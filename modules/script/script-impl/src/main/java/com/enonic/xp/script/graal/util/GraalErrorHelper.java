package com.enonic.xp.script.graal.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.script.ScriptException;

import org.graalvm.polyglot.PolyglotException;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProblemException;
import com.enonic.xp.util.Exceptions;

public final class GraalErrorHelper
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

    private static RuntimeException doHandleException( final RuntimeException e )
    {
        final StackTraceElement elem = findScriptTraceElement( e );
        if ( elem == null )
        {
            return e;
        }

        if ( e instanceof PolyglotException )
        {
            PolyglotException polyglotException = (PolyglotException) e;
            if ( polyglotException.isHostException() )
            {
                final Throwable hostException = polyglotException.asHostException();
                if ( hostException instanceof RuntimeException && !( hostException instanceof IllegalArgumentException ) )
                {
                    return (RuntimeException) hostException;
                }
                else
                {
                    return buildResourceProblemException( elem, hostException );
                }
            }
            else
            {
                return buildResourceProblemException( elem, e );
            }
        }
        else
        {
            return buildResourceProblemException( elem, e );
        }
    }

    private static ResourceProblemException buildResourceProblemException( StackTraceElement elem, final Throwable e )
    {
        final ResourceProblemException.Builder builder = ResourceProblemException.create();
        builder.cause( e );
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

    private static StackTraceElement findScriptTraceElement( final Throwable e )
    {
        final List<StackTraceElement> elements = getScriptFrames( e );
        return elements.isEmpty() ? null :elements.get( 0 );
    }

    private static List<StackTraceElement> getScriptFrames( final Throwable exception )
    {
        return Arrays.stream( exception.getStackTrace() ).filter( GraalErrorHelper::isScriptFrame ).map( stackTraceElement -> {
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

        final int nestedSeparator = name.lastIndexOf( '#' );
        if ( nestedSeparator >= 0 )
        {
            name = name.substring( nestedSeparator + 1 );
        }

        final int idSeparator = name.indexOf( '-' );
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
