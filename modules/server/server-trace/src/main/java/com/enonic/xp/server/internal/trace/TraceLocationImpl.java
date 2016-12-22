package com.enonic.xp.server.internal.trace.manager;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.xp.trace.TraceLocation;
import com.enonic.xp.trace.Tracer;

final class TraceLocationImpl
    implements TraceLocation
{
    private final static Set<String> IGNORED_CLASSES =
        Sets.newHashSet( TraceLocationImpl.class.getName(), Tracer.class.getName(), TraceService.class.getName() );

    private final String method;

    private final String className;

    private final int lineNumber;

    private TraceLocationImpl( final StackTraceElement elem )
    {
        this.method = elem.getMethodName();
        this.className = elem.getClassName();
        this.lineNumber = elem.getLineNumber();
    }

    @Override
    public String getMethod()
    {
        return this.method;
    }

    @Override
    public String getClassName()
    {
        return this.className;
    }

    @Override
    public int getLineNumber()
    {
        return this.lineNumber;
    }

    @Override
    public String toString()
    {
        return this.className + "." + this.method + ":" + this.lineNumber;
    }

    static TraceLocation findLocation()
    {
        final StackTraceElement elem = findStackTraceElement();
        return elem != null ? new TraceLocationImpl( elem ) : null;
    }

    private static StackTraceElement findStackTraceElement()
    {
        for ( final StackTraceElement elem : new Throwable().getStackTrace() )
        {
            final String name = elem.getClassName();
            if ( !IGNORED_CLASSES.contains( name ) )
            {
                return elem;
            }

        }

        return null;
    }
}
