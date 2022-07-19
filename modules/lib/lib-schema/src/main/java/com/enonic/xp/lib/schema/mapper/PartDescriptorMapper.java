package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.resource.DynamicSchemaResult;

public final class PartDescriptorMapper
    extends DescriptorMapper
{
    public PartDescriptorMapper( final DynamicSchemaResult<PartDescriptor> schema )
    {
        super( schema );
    }

    @Override
    protected String getType()
    {
        return "PART";
    }
}
