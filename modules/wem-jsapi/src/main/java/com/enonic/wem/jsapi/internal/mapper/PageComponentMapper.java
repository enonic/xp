package com.enonic.wem.jsapi.internal.mapper;

import com.enonic.wem.api.content.page.DescriptorBasedPageComponent;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.layout.LayoutComponent;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.script.serializer.MapGenerator;
import com.enonic.wem.script.serializer.MapSerializable;

final class PageComponentMapper
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
        gen.value( "name", this.value.getName() );
        gen.value( "path", this.value.getPath() );
        gen.value( "type", this.value.getType() );

        if ( this.value instanceof DescriptorBasedPageComponent )
        {
            serialize( gen, (DescriptorBasedPageComponent) this.value );
        }

        if ( this.value instanceof LayoutComponent )
        {
            serialize( gen, (LayoutComponent) this.value );
        }
    }

    private void serialize( final MapGenerator gen, final DescriptorBasedPageComponent comp )
    {
        gen.value( "descriptor", comp.getDescriptor() );
        gen.value( "config", ResultMappers.mapper( comp.getConfig() ) );
    }

    private void serialize( final MapGenerator gen, final LayoutComponent comp )
    {
        gen.array( "regions" );
        for ( final Region region : comp.getRegions() )
        {
            gen.value( ResultMappers.mapper( region ) );
        }
        gen.end();
    }
}
