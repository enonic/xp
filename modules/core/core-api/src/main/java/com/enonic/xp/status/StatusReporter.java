package com.enonic.xp.status;

import java.io.IOException;
import java.io.OutputStream;

import com.google.common.net.MediaType;

public interface StatusReporter
{
    String getName();

    MediaType getMediaType();

    void report( OutputStream stream )
        throws IOException;

    default void report( StatusContext context )
        throws IOException
    {
        this.report( context.getOutputStream() );
    }
}
