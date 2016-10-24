package com.enonic.xp.status;

import java.io.IOException;

import com.google.common.net.MediaType;

public interface StatusReporter
{
    String getName();

    MediaType getMediaType();

    void report( StatusContext context )
        throws IOException;
}
