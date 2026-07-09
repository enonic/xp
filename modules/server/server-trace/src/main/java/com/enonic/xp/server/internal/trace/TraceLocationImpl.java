package com.enonic.xp.server.internal.trace;

import java.util.Set;

import com.enonic.xp.trace.TraceLocation;
import com.enonic.xp.trace.Tracer;

final class TraceLocationImpl
    implements TraceLocation
{
    private static final Set<String> IGNORED_CLASSES =
        Set.of( TraceLocationImpl.class.getName(), Tracer.class.getName(), TraceService.class.getName() );

    private final String method;

    private final String className;

    private final int lineNumber;

    private TraceLocationImpl( final StackWalker.StackFrame elem )
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
        return StackWalker.getInstance()
            .walk( frames -> frames.filter( frame -> !IGNORED_CLASSES.contains( frame.getClassName() ) )
                .findFirst()
                .map( TraceLocationImpl::new )
                .orElse( null ) );
    }
}
