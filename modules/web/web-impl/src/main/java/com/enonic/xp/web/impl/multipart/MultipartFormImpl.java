package com.enonic.xp.web.impl.multipart;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.fileupload.FileItem;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;

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
    public MultipartItem get( final String name )
    {
        final FileItem item = this.map.get( name );
        return item != null ? new MultipartItemImpl( item ) : null;
    }

    @Override
    public String getAsString( final String name )
    {
        final MultipartItem item = get( name );
        if ( item == null )
        {
            return null;
        }

        return item.getAsString();
    }

    @Override
    public void delete()
    {
        this.map.values().forEach( FileItem::delete );
    }

    @Override
    public Iterator<MultipartItem> iterator()
    {
        final Stream<MultipartItem> items = this.map.values().stream().map( MultipartItemImpl::new );
        return items.iterator();
    }
}
