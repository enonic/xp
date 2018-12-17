package com.enonic.xp.status;

import java.io.IOException;
import java.io.OutputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Charsets;
import com.google.common.net.MediaType;

public abstract class JsonStatusReporter
    implements StatusReporter
{
    public final MediaType getMediaType()
    {
        return MediaType.JSON_UTF_8;
    }

    @Override
    public final void report( final OutputStream outputStream )
        throws IOException
    {
        outputStream.write( getReport().toString().getBytes( Charsets.UTF_8 ) );
    }

    public abstract JsonNode getReport();
}
