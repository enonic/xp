package com.enonic.xp.web.impl.multipart;

import java.util.Iterator;

import jakarta.servlet.http.Part;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;

import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;

final class MultipartFormImpl
    implements MultipartForm
{
    private final ImmutableListMultimap<String, MultipartItem> map;

    MultipartFormImpl( final Iterable<Part> parts )
    {
        final ImmutableListMultimap.Builder<String, MultipartItem> builder = ImmutableListMultimap.builder();
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
        return this.get( name, 0 );
    }

    @Override
    public MultipartItem get( final String name, final int index )
    {
        final ImmutableList<MultipartItem> values = this.map.get( name );
        if ( values == null )
        {
            return null;
        }
        return values.size() > index ? values.get( index ) : null;
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
