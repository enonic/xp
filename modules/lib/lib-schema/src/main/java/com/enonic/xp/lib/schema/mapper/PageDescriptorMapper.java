package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.page.PageDescriptor;
import com.enonic.xp.schema.SchemaResult;
import com.enonic.xp.script.serializer.MapGenerator;

public final class PageDescriptorMapper
    extends DescriptorMapper<PageDescriptor>
{
    public PageDescriptorMapper( final SchemaResult<PageDescriptor> schema )
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
        return "PAGE";
    }
}
