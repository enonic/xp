package com.enonic.xp.admin.impl.rest.multipart;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.util.UnicodeFormNormalizer;

final class MultipartFormImpl
    implements MultipartForm
{
    private final ImmutableMap<String, FileItem> map;


    public MultipartFormImpl( final List<FileItem> items )
    {
        final ImmutableMap.Builder<String, FileItem> builder = ImmutableMap.builder();
        for ( final FileItem item : items )
        {
            builder.put( item.getFieldName(), item );
        }

        this.map = builder.build();
    }

    @Override
    public FileItem get( final String name )
    {
        return this.map.get( name );
    }

    @Override
    public String getAsString( final String name )
    {
        final FileItem fileItem = this.map.get( name );
        if ( fileItem == null )
        {
            return null;
        }

        final String rawString = new String( fileItem.get(), Charsets.UTF_8 );

        return UnicodeFormNormalizer.normalize( rawString );
    }

    @Override
    public void delete()
    {
        for ( final FileItem item : this.map.values() )
        {
            item.delete();
        }
    }

    @Override
    public Iterator<FileItem> iterator()
    {
        return this.map.values().iterator();
    }
}
