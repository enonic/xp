package com.enonic.xp.script.impl.util;

import javax.script.ScriptException;

import com.oracle.truffle.js.runtime.GraalJSException;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceNotFoundException;
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

        if ( e instanceof ResourceNotFoundException )
        {
            return (ResourceNotFoundException) e;
        }

        if ( e instanceof ScriptException )
        {
            return doHandleException( (ScriptException) e );
        }

        if ( e instanceof GraalJSException )
        {
            return doHandleException( (GraalJSException) e );
        }

        if ( e instanceof RuntimeException )
        {
            return doHandleException( (RuntimeException) e );
        }

        return Exceptions.unchecked( e );
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

    private static StackTraceElement findScriptTraceElement( final RuntimeException e )
    {
        final StackTraceElement[] elements =  e.getStackTrace();
        return elements.length > 0 ? elements[0] : null;
    }
}
