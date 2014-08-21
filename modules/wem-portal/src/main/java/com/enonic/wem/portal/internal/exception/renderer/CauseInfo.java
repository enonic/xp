package com.enonic.wem.portal.internal.exception.renderer;

import java.util.List;

import com.google.common.collect.Lists;

final class CauseInfo
{
    private final static int NUM_STACK_ELEMENTS = 14;

    private final Throwable error;

    public CauseInfo( final Throwable error )
    {
        this.error = error;
    }

    public String getMessage()
    {
        final String message = this.error.getMessage();
        return message != null ? message : "Empty message in exception";
    }

    public List<LineInfo> getTrace()
    {
        final List<LineInfo> list = Lists.newArrayList();
        final StackTraceElement[] trace = this.error.getStackTrace();

        for ( int i = 0; i < Math.min( trace.length, NUM_STACK_ELEMENTS ); i++ )
        {
            list.add( new LineInfo( i + 1, trace[i].toString() ) );
        }

        if ( trace.length > NUM_STACK_ELEMENTS )
        {
            list.add( new LineInfo( NUM_STACK_ELEMENTS + 1, "..." ) );
        }

        return list;
    }
}
