package com.enonic.xp.server.impl.status;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.net.MediaType;

import com.enonic.xp.status.StatusReporter;

abstract class JsonStatusReporter
    implements StatusReporter
{
    @Override
    public final MediaType getMediaType()
    {
        return MediaType.JSON_UTF_8;
    }

    @Override
    public final void report( final OutputStream outputStream )
        throws IOException
    {
        outputStream.write( getReport().toString().getBytes( StandardCharsets.UTF_8 ) );
    }

    public abstract JsonNode getReport();
}
