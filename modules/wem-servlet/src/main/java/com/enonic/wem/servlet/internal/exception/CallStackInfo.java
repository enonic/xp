package com.enonic.wem.servlet.internal.exception;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import com.enonic.wem.api.resource.ResourceProblemException;

final class CallStackInfo
{
    private final ResourceProblemException error;

    public CallStackInfo( final ResourceProblemException error )
    {
        this.error = error;
    }

    public boolean isEmpty()
    {
        return this.error.getCallStack().isEmpty();
    }

    public List<LineInfo> getLines()
    {
        final List<LineInfo> list = Lists.newArrayList();
        list.addAll(
            this.error.getCallStack().stream().map( line -> new LineInfo( list.size() + 1, line ) ).collect( Collectors.toList() ) );

        return list;
    }
}
