package com.enonic.wem.jaxrs.internal.multipart;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

import com.enonic.wem.jaxrs.multipart.MultipartForm;
import com.enonic.wem.jaxrs.multipart.MultipartItem;

final class MultipartFormImpl
    implements MultipartForm
{
    private final Map<String, MultipartItem> map;

    public MultipartFormImpl( final List<FileItem> items )
    {
        this.map = new HashMap<>();
        for ( final FileItem item : items )
        {
            this.map.put( item.getFieldName(), new MultipartItemImpl( item ) );
        }
    }

    @Override
    public MultipartItem get( final String name )
    {
        return this.map.get( name );
    }

    @Override
    public void delete()
    {
        for ( final MultipartItem item : this.map.values() )
        {
            item.delete();
        }
    }

    @Override
    public Iterator<MultipartItem> iterator()
    {
        return this.map.values().iterator();
    }
}
