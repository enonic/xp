package com.enonic.wem.jsapi.internal.mapper;

import com.enonic.wem.api.content.page.DescriptorBasedPageComponent;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.layout.LayoutComponent;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.script.serializer.MapGenerator;

public final class PageComponentMapper
{
    public static void serialize( final MapGenerator gen, final PageComponent value )
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
        PropertyTreeMapper.serialize( gen, comp.getConfig() );
        gen.end();
    }

    private static void serialize( final MapGenerator gen, final LayoutComponent comp )
    {
        gen.array( "regions" );
        for ( final Region region : comp.getRegions() )
        {
            gen.map();
            RegionMapper.serialize( gen, region );
            gen.end();
        }
        gen.end();
    }
}
