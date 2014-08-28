package com.enonic.wem.portal.internal.exception.renderer;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceProblemException;

final class SourceInfo
{
    private final static int NUM_DELTA_LINES = 3;

    private final ResourceProblemException error;

    public SourceInfo( final ResourceProblemException error )
    {
        this.error = error;
    }

    public String getName()
    {
        return this.error.getResource().toString();
    }

    public int getLine()
    {
        return this.error.getLineNumber();
    }

    public List<LineInfo> getLines()
    {
        final List<String> allLines = getAllLines();
        final List<String> subList = sliceLines( allLines );

        final int errorLine = getLine();
        int currentLine = Math.max( 0, getLine() - NUM_DELTA_LINES ) + 1;

        final List<LineInfo> list = Lists.newArrayList();
        for ( final String line : subList )
        {
            final String str = line.replaceAll( "\t", "    " );
            list.add( new LineInfo( currentLine, str, ( errorLine == currentLine ) ) );
            currentLine++;
        }

        return list;
    }

    private List<String> getAllLines()
    {
        final Resource resource = Resource.from( this.error.getResource() );
        return resource.readLines();
    }

    private List<String> sliceLines( final List<String> all )
    {
        final int firstLine = Math.max( 0, getLine() - NUM_DELTA_LINES );
        final int lastLine = Math.min( all.size(), getLine() + NUM_DELTA_LINES );
        return all.subList( firstLine, lastLine );
    }
}
