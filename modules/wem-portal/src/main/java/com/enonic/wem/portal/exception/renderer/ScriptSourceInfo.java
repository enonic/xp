package com.enonic.wem.portal.exception.renderer;

import java.io.IOException;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import com.enonic.wem.portal.script.EvaluationException;

public final class ScriptSourceInfo
{
    private final static int NUM_DELTA_LINES = 3;

    private final EvaluationException error;

    public ScriptSourceInfo( final EvaluationException error )
    {
        this.error = error;
    }

    public String getName()
    {
        return this.error.getSource().getName();
    }

    public int getLine()
    {
        return this.error.getLineNumber();
    }

    public int getFromLine()
    {
        return Math.max( 0, getLine() - NUM_DELTA_LINES ) + 1;
    }

    public List<String> getLines()
        throws IOException
    {
        final List<String> allLines = getAllLines();
        final List<String> subList = sliceLines( allLines );
        return Lists.newArrayList( Collections2.transform( subList, new TabToSpaces() ) );
    }

    private List<String> getAllLines()
    {
        final String str = this.error.getSource().getScriptAsString();
        final Iterable<String> allLines = Splitter.onPattern( "\r?\n" ).split( str );
        return Lists.newArrayList( allLines );
    }

    private List<String> sliceLines( final List<String> all )
    {
        final int firstLine = Math.max( 0, getLine() - NUM_DELTA_LINES );
        final int lastLine = Math.min( all.size(), getLine() + NUM_DELTA_LINES );
        return all.subList( firstLine, lastLine );
    }

    public List<String> getScriptStack()
    {
        return this.error.getScriptStack();
    }

    private final class TabToSpaces
        implements Function<String, String>
    {
        @Override
        public String apply( final String input )
        {
            return input.replaceAll( "\t", "    " );
        }
    }
}
