package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.resource.DynamicSchemaResult;

public final class PageDescriptorMapper
    extends DescriptorMapper
{
    public PageDescriptorMapper( final DynamicSchemaResult<PageDescriptor> schema )
    {
        super( schema );
    }

    @Override
    protected String getType()
    {
        return "Page";
    }
}
