package com.enonic.xp.web.impl.multipart;

import java.util.Iterator;

import javax.servlet.http.Part;

import com.google.common.collect.ImmutableMap;

import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;

final class MultipartFormImpl
    implements MultipartForm
{
    private final ImmutableMap<String, MultipartItem> map;

    public MultipartFormImpl( final Iterable<Part> parts )
    {
        final ImmutableMap.Builder<String, MultipartItem> builder = ImmutableMap.builder();
        for ( final Part part : parts )
        {
            final MultipartItemImpl item = new MultipartItemImpl( part );
            builder.put( item.getName(), item );
        }

        this.map = builder.build();
    }

    @Override
    public boolean isEmpty()
    {
        return this.map.isEmpty();
    }

    @Override
    public int getSize()
    {
        return this.map.size();
    }

    @Override
    public MultipartItem get( final String name )
    {
        return this.map.get( name );
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
        this.map.values().forEach( this::delete );
    }

    private void delete( final MultipartItem item )
    {
        ( (MultipartItemImpl) item ).delete();
    }

    @Override
    public Iterator<MultipartItem> iterator()
    {
        return this.map.values().iterator();
    }
}
