package com.enonic.xp.web.impl.multipart;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.google.common.net.MediaType;

import com.enonic.xp.util.UnicodeFormNormalizer;
import com.enonic.xp.web.multipart.MultipartItem;

final class MultipartItemImpl
    implements MultipartItem
{
    private final FileItem item;

    public MultipartItemImpl( final FileItem item )
    {
        this.item = item;
    }

    @Override
    public String getFileName()
    {
        return this.item.getName();
    }

    @Override
    public String getName()
    {
        return this.item.getFieldName();
    }

    @Override
    public MediaType getContentType()
    {
        return MediaType.parse( this.item.getContentType() );
    }

    @Override
    public ByteSource getBytes()
    {
        if ( this.item.isInMemory() )
        {
            return ByteSource.wrap( this.item.get() );
        }

        return Files.asByteSource( ( (DiskFileItem) this.item ).getStoreLocation() );
    }

    @Override
    public String getAsString()
    {
        final String rawString = new String( this.item.get(), Charsets.UTF_8 );
        return UnicodeFormNormalizer.normalize( rawString );
    }

    @Override
    public long getSize()
    {
        return this.item.getSize();
    }
}
