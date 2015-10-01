package com.enonic.xp.web.impl.multipart;

import java.io.InputStream;

import org.apache.commons.fileupload.RequestContext;

final class RequestContextImpl
    implements RequestContext
{
    private final InputStream in;

    private final String mediaType;

    public RequestContextImpl( final InputStream in, final String mediaType )
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
        return this.mediaType;
    }

    @Override
    public int getContentLength()
    {
        return -1;
    }

    @Override
    public InputStream getInputStream()
    {
        return this.in;
    }
}
