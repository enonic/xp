package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.script.serializer.MapGenerator;

public final class PartDescriptorMapper
    extends DescriptorMapper<PartDescriptor>
{
    public PartDescriptorMapper( final DynamicSchemaResult<PartDescriptor> schema )
    {
        super( schema );
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        super.serialize( gen );
        DynamicSchemaSerializer.serializeIcon( gen, descriptor.getIcon() );
    }

    @Override
    protected String getType()
    {
        return "PART";
    }
}
