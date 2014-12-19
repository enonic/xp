package com.enonic.wem.script.mapper;

import com.enonic.wem.api.content.page.DescriptorBasedPageComponent;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.layout.LayoutComponent;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.script.serializer.MapGenerator;
import com.enonic.wem.script.serializer.MapSerializable;

public final class PageComponentMapper
    implements MapSerializable
{
    private final PageComponent value;

    public PageComponentMapper( final PageComponent value )
    {
        this.value = value;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        serialize( gen, this.value );
    }

    private static void serialize( final MapGenerator gen, final PageComponent value )
    {
        gen.value( "name", value.getName() );
        gen.value( "path", value.getPath() );
        gen.value( "type", value.getType() );

        if ( value instanceof DescriptorBasedPageComponent )
        {
            serialize( gen, (DescriptorBasedPageComponent) value );
        }

        if ( value instanceof LayoutComponent )
        {
            serialize( gen, (LayoutComponent) value );
        }
    }

    private static void serialize( final MapGenerator gen, final DescriptorBasedPageComponent comp )
    {
        gen.value( "descriptor", comp.getDescriptor() );
        gen.map( "config" );
        new PropertyTreeMapper( comp.getConfig() ).serialize( gen );
        gen.end();
    }

    private static void serialize( final MapGenerator gen, final LayoutComponent comp )
    {
        gen.array( "regions" );
        for ( final Region region : comp.getRegions() )
        {
            gen.map();
            new RegionMapper( region ).serialize( gen );
            gen.end();
        }
        gen.end();
    }
}
