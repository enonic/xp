package com.enonic.xp.lib.content.mapper;

import com.enonic.xp.region.Component;
import com.enonic.xp.region.DescriptorBasedComponent;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.TextComponent;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

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

    private void serialize( final MapGenerator gen, final Component value )
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
        else if ( value instanceof TextComponent )
        {
            serialize( gen, (TextComponent) value );
        }
    }

    private void serialize( final MapGenerator gen, final DescriptorBasedComponent comp )
    {
        gen.value( "descriptor", comp.getDescriptor() );
        if ( comp.getConfig() != null )
        {
            gen.map( "config" );
            new PropertyTreeMapper( comp.getConfig() ).serialize( gen );
            gen.end();
        }
    }

    private void serialize( final MapGenerator gen, final LayoutComponent comp )
    {
        gen.map( "regions" );
        for ( final Region region : comp.getRegions() )
        {
            new RegionMapper( region ).serialize( gen );
        }
        gen.end();
    }

    private void serialize( final MapGenerator gen, final TextComponent comp )
    {
        gen.value( "text", comp.getText() );
    }
}
