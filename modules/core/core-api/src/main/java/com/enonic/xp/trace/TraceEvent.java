package com.enonic.xp.trace;

public final class TraceEvent
{
    public enum Type
    {
        START, END
    }

    private final Type type;

    private final Trace trace;

    private TraceEvent( final Type type, final Trace trace )
    {
        this.type = type;
        this.trace = trace;
    }

    public Type getType()
    {
        return this.type;
    }

    public Trace getTrace()
    {
        return this.trace;
    }

    public static TraceEvent start( final Trace trace )
    {
        return new TraceEvent( Type.START, trace );
    }

    public static TraceEvent end( final Trace trace )
    {
        return new TraceEvent( Type.END, trace );
    }
}
