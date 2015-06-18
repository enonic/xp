package com.enonic.xp.tools.gradle.watch;

import java.io.OutputStream;
import java.io.PrintStream;

final class NopPrintStream
    extends PrintStream
{
    public NopPrintStream()
    {
        super( new OutputStream()
        {
            public void write( int b )
            {
            }

            public void write( byte[] b )
            {
            }

            public void write( byte[] b, int off, int len )
            {
            }
        } );
    }
}
