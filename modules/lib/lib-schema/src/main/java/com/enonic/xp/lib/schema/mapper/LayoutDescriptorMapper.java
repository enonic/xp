package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.script.serializer.MapGenerator;

public final class LayoutDescriptorMapper
    extends DescriptorMapper<LayoutDescriptor>
{
    public LayoutDescriptorMapper( final DynamicSchemaResult<LayoutDescriptor> schema )
    {
        super( schema );
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        super.serialize( gen );
        DynamicSchemaSerializer.serializeRegions( gen, descriptor.getRegions() );
    }

    @Override
    protected String getType()
    {
        return "LAYOUT";
    }
}
