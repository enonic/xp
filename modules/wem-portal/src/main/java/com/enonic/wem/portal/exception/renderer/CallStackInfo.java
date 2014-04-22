package com.enonic.wem.portal.exception.renderer;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.portal.script.SourceException;

final class CallStackInfo
{
    private final SourceException error;

    public CallStackInfo( final SourceException error )
    {
        this.error = error;
    }

    public List<LineInfo> getLines()
    {
        final List<LineInfo> list = Lists.newArrayList();
        for ( final String line : this.error.getCallStack() )
        {
            list.add( new LineInfo( list.size() + 1, line ) );
        }

        return list;
    }
}
