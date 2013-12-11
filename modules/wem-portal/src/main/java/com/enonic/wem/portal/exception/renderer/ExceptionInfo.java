package com.enonic.wem.portal.exception.renderer;

import java.util.List;

import com.google.common.collect.Lists;

public final class ExceptionInfo
{
    private final static int NUM_STACK_ELEMENTS = 14;

    private final Throwable error;

    public ExceptionInfo( final Throwable error )
    {
        this.error = error;
    }

    public String getMessage()
    {
        final String message = this.error.getMessage();
        return message != null ? message : "Empty message in exception";
    }

    public List<String> getTrace()
    {
        List<String> list = Lists.newArrayList();
        for ( final StackTraceElement item : this.error.getStackTrace() )
        {
            list.add( item.toString() );
        }

        if ( list.size() > NUM_STACK_ELEMENTS )
        {
            list = list.subList( 0, NUM_STACK_ELEMENTS );
            list.add( "..." );
        }

        return list;
    }
}
