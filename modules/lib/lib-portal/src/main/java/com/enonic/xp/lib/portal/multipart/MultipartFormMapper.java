package com.enonic.xp.lib.portal.multipart;

import java.util.List;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;

public final class MultipartFormMapper
    implements MapSerializable
{
    private final MultipartForm form;

    public MultipartFormMapper( final MultipartForm form )
    {
        this.form = form;
    }

    private Iterable<MultipartItem> getItems()
    {
        return this.form;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        final ListMultimap<String, MultipartItem> items = LinkedListMultimap.create();
        for ( MultipartItem item : this.getItems() )
        {
            items.put( item.getName(), item );
        }

        for ( final String name : items.keySet() )
        {
            final List<MultipartItem> values = items.get( name );
            if ( values.size() == 1 )
            {
                gen.map( name );
                MultipartItemMapper.serialize( gen, values.get( 0 ) );
                gen.end();
            }
            else
            {
                gen.array( name );
                values.forEach( ( item ) -> {
                    gen.map();
                    MultipartItemMapper.serialize( gen, item );
                    gen.end();
                } );
                gen.end();
            }
        }
    }
}
