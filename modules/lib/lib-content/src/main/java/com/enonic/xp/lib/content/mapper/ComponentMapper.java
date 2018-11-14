package com.enonic.xp.lib.content.mapper;

import com.enonic.xp.lib.common.PropertyTreeMapper;
import com.enonic.xp.region.Component;
import com.enonic.xp.region.DescriptorBasedComponent;
import com.enonic.xp.region.FragmentComponent;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.TextComponent;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class ComponentMapper
    implements MapSerializable
{
    static final String TYPE = "type";

    static final String PATH = "path";

    static final String DESCRIPTOR = "descriptor";

    static final String CONFIG = "config";

    static final String TEXT = "text";

    static final String REGIONS = "regions";

    static final String FRAGMENT = "fragment";

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
        gen.value( PATH, value.getPath() );
        gen.value( TYPE, value.getType() );

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
        else if ( value instanceof FragmentComponent )
        {
            serialize( gen, (FragmentComponent) value );
        }
    }

    private void serialize( final MapGenerator gen, final DescriptorBasedComponent comp )
    {
        gen.value( DESCRIPTOR, comp.getDescriptor() );

        if ( comp.getConfig() != null )
        {
            gen.map( CONFIG );
            new PropertyTreeMapper( comp.getConfig() ).serialize( gen );
            gen.end();
        }
    }

    private void serialize( final MapGenerator gen, final LayoutComponent comp )
    {
        gen.map( REGIONS );

        for ( final Region region : comp.getRegions() )
        {
            new RegionMapper( region ).serialize( gen );
        }

        gen.end();
    }

    private void serialize( final MapGenerator gen, final FragmentComponent comp )
    {
        gen.value( FRAGMENT, comp.getFragment() );
    }

    private void serialize( final MapGenerator gen, final TextComponent comp )
    {
        gen.value( TEXT, comp.getText() );
    }
}
