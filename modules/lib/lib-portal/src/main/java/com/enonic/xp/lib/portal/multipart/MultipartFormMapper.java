package com.enonic.xp.lib.portal.multipart;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    @Override
    public void serialize( final MapGenerator gen )
    {
        StreamSupport.stream( this.form.spliterator(), false )
            .collect( Collectors.groupingBy( MultipartItem::getName, LinkedHashMap::new, Collectors.toList() ) )
            .forEach( ( name, values ) -> {
                if ( values.size() == 1 )
                {
                    gen.map( name );
                    MultipartItemMapper.serialize( gen, values.getFirst() );
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
            } );
    }
}
