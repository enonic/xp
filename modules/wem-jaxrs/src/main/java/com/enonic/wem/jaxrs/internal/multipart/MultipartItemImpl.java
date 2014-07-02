package com.enonic.wem.jaxrs.internal.multipart;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.FileItem;

import com.enonic.wem.jaxrs.multipart.MultipartItem;

final class MultipartItemImpl
    implements MultipartItem
{
    private final FileItem item;

    public MultipartItemImpl( final FileItem item )
    {
        this.item = item;
    }

    @Override
    public String getName()
    {
        return this.item.getName();
    }

    @Override
    public String getContentType()
    {
        return this.item.getContentType();
    }

    @Override
    public long getSize()
    {
        return this.item.getSize();
    }

    @Override
    public String getFieldName()
    {
        return this.item.getFieldName();
    }

    @Override
    public boolean isFormField()
    {
        return this.item.isFormField();
    }

    @Override
    public InputStream getInputStream()
        throws IOException
    {
        return this.item.getInputStream();
    }

    @Override
    public void delete()
    {
        this.item.delete();
    }
}
