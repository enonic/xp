package com.enonic.xp.web.jaxrs.impl.rest.multipart;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.MediaType;

import org.apache.commons.fileupload.RequestContext;

final class MultipartFormContext
    implements RequestContext
{
    private final InputStream in;

    private final MediaType mediaType;

    public MultipartFormContext( final InputStream in, final MediaType mediaType )
    {
        this.in = in;
        this.mediaType = mediaType;
    }

    @Override
    public String getCharacterEncoding()
    {
        return "UTF-8";
    }

    @Override
    public String getContentType()
    {
        return this.mediaType.toString();
    }

    @Override
    public int getContentLength()
    {
        return -1;
    }

    @Override
    public InputStream getInputStream()
        throws IOException
    {
        return this.in;
    }
}
