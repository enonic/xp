package com.enonic.xp.launcher.ui.panel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

final class LoggingOutputStream
    extends PrintStream
{
    private final LoggingTextArea textArea;

    public LoggingOutputStream( final LoggingTextArea textArea )
    {
        super( new ByteArrayOutputStream() );
        this.textArea = textArea;
    }

    @Override
    public void write( final byte[] bytes )
        throws IOException
    {
        final String str = new String( bytes );
        this.textArea.appendText( str );
    }

    @Override
    public void write( final byte[] bytes, final int off, final int len )
    {
        final String str = new String( bytes, off, len );
        this.textArea.appendText( str );
    }
}
