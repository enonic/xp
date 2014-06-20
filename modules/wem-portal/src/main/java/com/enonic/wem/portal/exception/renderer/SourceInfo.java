package com.enonic.wem.portal.exception.renderer;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import com.enonic.wem.portal.script.SourceException;

final class SourceInfo
{
    private final static int NUM_DELTA_LINES = 3;

    private final SourceException error;

    public SourceInfo( final SourceException error )
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
        throws Exception
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
        throws IOException
    {
        final URL path = this.error.getPath();
        if ( path == null )
        {
            return Collections.emptyList();
        }
        return Resources.readLines( path, Charsets.UTF_8 );
    }

    private List<String> sliceLines( final List<String> all )
    {
        final int firstLine = Math.max( 0, getLine() - NUM_DELTA_LINES );
        final int lastLine = Math.min( all.size(), getLine() + NUM_DELTA_LINES );
        return all.subList( firstLine, lastLine );
    }
}
