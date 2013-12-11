package com.enonic.wem.portal.exception.renderer;

import java.io.IOException;
import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import com.enonic.wem.portal.script.loader.ScriptSource;

public final class ScriptSourceInfo
{
    private final static int NUM_SOURCE_LINES = 5;

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
        return Math.max( 0, this.line - NUM_SOURCE_LINES ) + 1;
    }

    public List<String> getLines()
        throws IOException
    {
        final String str = this.source.getScriptAsString();
        final Iterable<String> allLines = Splitter.onPattern( "\r?\n" ).split( str );

        final int numLines = Math.max( 0, this.line - NUM_SOURCE_LINES );
        return Lists.newArrayList( allLines ).subList( numLines, this.line );
    }
}
