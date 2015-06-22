package com.enonic.xp.portal.impl.jslib.mapper;

import com.enonic.xp.portal.script.serializer.MapGenerator;
import com.enonic.xp.portal.script.serializer.MapSerializable;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.DescriptorBasedComponent;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.Region;

public final class ComponentMapper
    implements MapSerializable
{
    private final Component value;

    public ComponentMapper( final Component value )
    {
        this.value = value;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        serialize( gen, this.value );
    }

    private static void serialize( final MapGenerator gen, final Component value )
    {
        gen.value( "name", value.getName() );
        gen.value( "path", value.getPath() );
        gen.value( "type", value.getType() );

        if ( value instanceof DescriptorBasedComponent )
        {
            serialize( gen, (DescriptorBasedComponent) value );
        }

        if ( value instanceof LayoutComponent )
        {
            serialize( gen, (LayoutComponent) value );
        }
    }

    private static void serialize( final MapGenerator gen, final DescriptorBasedComponent comp )
    {
        gen.value( "descriptor", comp.getDescriptor() );
        if ( comp.getConfig() != null )
        {
            gen.map( "config" );
            new PropertyTreeMapper( comp.getConfig() ).serialize( gen );
            gen.end();
        }
    }

    private static void serialize( final MapGenerator gen, final LayoutComponent comp )
    {
        gen.map( "regions" );
        for ( final Region region : comp.getRegions() )
        {
            new RegionMapper( region ).serialize( gen );
        }
        gen.end();
    }
}
