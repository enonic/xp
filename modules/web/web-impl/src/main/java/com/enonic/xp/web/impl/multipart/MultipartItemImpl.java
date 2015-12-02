package com.enonic.xp.web.impl.multipart;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.Part;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.google.common.net.MediaType;

import com.enonic.xp.util.Exceptions;
import com.enonic.xp.web.multipart.MultipartItem;

final class MultipartItemImpl
    extends ByteSource
    implements MultipartItem
{
    private final Part item;

    public MultipartItemImpl( final Part item )
    {
        this.item = item;
    }

    @Override
    public String getName()
    {
        return this.item.getName();
    }

    @Override
    public String getFileName()
    {
        return this.item.getSubmittedFileName();
    }

    @Override
    public MediaType getContentType()
    {
        final String itemContentType = this.item.getContentType();
        return itemContentType != null ? MediaType.parse( itemContentType ) : null;
    }

    @Override
    public ByteSource getBytes()
    {
        return this;
    }

    @Override
    public String getAsString()
    {
        try
        {
            return asCharSource( Charsets.UTF_8 ).read();
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    @Override
    public long getSize()
    {
        return this.item.getSize();
    }

    @Override
    public InputStream openStream()
        throws IOException
    {
        return this.item.getInputStream();
    }

    @Override
    public long size()
    {
        return getSize();
    }

    @Override
    public boolean isEmpty()
    {
        return getSize() == 0;
    }

    public void delete()
    {
        try
        {
            this.item.delete();
        }
        catch ( final Exception e )
        {
            // Do nothing
        }
    }
}
