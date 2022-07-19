package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.resource.DynamicSchemaResult;

public final class LayoutDescriptorMapper
    extends DescriptorMapper
{
    public LayoutDescriptorMapper( final DynamicSchemaResult<LayoutDescriptor> schema )
    {
        super( schema );
    }

    @Override
    protected String getType()
    {
        return "LAYOUT";
    }
}
