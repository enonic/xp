package com.enonic.wem.portal.internal.exception.renderer;

final class LineInfo
{
    private final int line;

    private final boolean mark;

    private final String message;

    public LineInfo( final int line, final String message )
    {
        this( line, message, false );
    }

    public LineInfo( final int line, final String message, final boolean mark )
    {
        this.line = line;
        this.mark = mark;
        this.message = message;
    }

    public int getLine()
    {
        return this.line;
    }

    public boolean isMark()
    {
        return this.mark;
    }

    public String getMessage()
    {
        return this.message;
    }
}
