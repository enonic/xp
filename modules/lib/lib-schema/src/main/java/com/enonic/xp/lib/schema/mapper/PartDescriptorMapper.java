package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.schema.SchemaResult;
import com.enonic.xp.script.serializer.MapGenerator;

public final class PartDescriptorMapper
    extends DescriptorMapper<PartDescriptor>
{
    public PartDescriptorMapper( final SchemaResult<PartDescriptor> schema )
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
