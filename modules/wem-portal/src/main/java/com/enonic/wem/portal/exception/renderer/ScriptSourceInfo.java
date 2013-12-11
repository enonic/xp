package com.enonic.wem.portal.exception.renderer;

import java.io.IOException;
import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import com.enonic.wem.portal.script.loader.ScriptSource;

public final class ScriptSourceInfo
{
    private final static int NUM_DELTA_LINES = 3;

    private final ScriptSource source;

    private final int line;

    private final int column;

    public ScriptSourceInfo( final ScriptSource source, final int line, final int column )
    {
        this.source = source;
        this.line = line;
        this.column = column;
    }

    public String getName()
    {
        return this.source.getName();
    }

    public int getLine()
    {
        return line;
    }

    public int getColumn()
    {
        return column;
    }

    public int getFromLine()
    {
        return Math.max( 0, this.line - NUM_DELTA_LINES ) + 1;
    }

    public List<String> getLines()
        throws IOException
    {
        final String str = this.source.getScriptAsString();
        final Iterable<String> allLines = Splitter.onPattern( "\r?\n" ).split( str );
        final List<String> lineList = Lists.newArrayList( allLines );

        final int firstLine = Math.max( 0, this.line - NUM_DELTA_LINES );
        final int lastLine = Math.min( lineList.size(), this.line + NUM_DELTA_LINES );
        return lineList.subList( firstLine, lastLine );
    }
}
