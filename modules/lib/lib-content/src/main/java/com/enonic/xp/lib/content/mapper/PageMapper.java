package com.enonic.xp.lib.content.mapper;

import com.enonic.xp.lib.common.PropertyTreeMapper;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageRegions;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.ComponentPath;
import com.enonic.xp.region.Region;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

import static com.enonic.xp.lib.content.mapper.ComponentMapper.CONFIG;
import static com.enonic.xp.lib.content.mapper.ComponentMapper.DESCRIPTOR;
import static com.enonic.xp.lib.content.mapper.ComponentMapper.FRAGMENT;
import static com.enonic.xp.lib.content.mapper.ComponentMapper.PATH;
import static com.enonic.xp.lib.content.mapper.ComponentMapper.REGIONS;
import static com.enonic.xp.lib.content.mapper.ComponentMapper.TYPE;

public final class PageMapper
    implements MapSerializable
{
    static final String PAGE = "page";

    static final String TEMPLATE = "template";

    private final Page value;

    public PageMapper( final Page value )
    {
        this.value = value;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        serialize( gen, this.value );
    }

    private void serialize( final MapGenerator gen, final Page value )
    {
        if ( value.isFragment() )
        {
            serializeFragment( gen, value.getFragment() );
        }
        else
        {
            serializePage( gen, value );
        }
    }

    private void serializeFragment( final MapGenerator gen, final Component value )
    {
        gen.map( FRAGMENT );

        new ComponentMapper( value ).serialize( gen );

        gen.end();
    }

    private void serializePage( final MapGenerator gen, final Page value )
    {
        gen.map( PAGE );

        gen.value( TYPE, PAGE );
        gen.value( PATH, ComponentPath.DIVIDER );
        gen.value( TEMPLATE, value.getTemplate() );
        gen.value( DESCRIPTOR, value.getDescriptor() );

        if ( value.hasConfig() )
        {
            gen.map( CONFIG );
            new PropertyTreeMapper( value.getConfig() ).serialize( gen );
            gen.end();
        }
        if ( value.hasRegions() )
        {
            serializeRegions( gen, value.getRegions() );
        }

        gen.end();
    }

    private void serializeRegions( final MapGenerator gen, final PageRegions values )
    {
        gen.map( REGIONS );

        if ( values != null )
        {
            for ( final Region region : values )
            {
                new RegionMapper( region ).serialize( gen );
            }
        }

        gen.end();
    }
}
