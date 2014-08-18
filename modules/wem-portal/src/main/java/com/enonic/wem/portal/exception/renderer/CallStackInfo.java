package com.enonic.wem.portal.exception.renderer;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import com.enonic.wem.script.SourceException;

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
        list.addAll(
            this.error.getCallStack().stream().map( line -> new LineInfo( list.size() + 1, line ) ).collect( Collectors.toList() ) );

        return list;
    }
}
