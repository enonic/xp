package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.resource.DynamicContentSchemaType;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.script.serializer.MapGenerator;

public final class MixinDescriptorMapper
    extends SchemaMapper<MixinDescriptor>
{
    public MixinDescriptorMapper( final DynamicSchemaResult<MixinDescriptor> schema )
    {
        super( schema );
    }

    public void serialize( final MapGenerator gen )
    {
        super.serialize( gen );
        DynamicSchemaSerializer.serializeForm( gen, descriptor.getForm() );
    }

    @Override
    protected String getType()
    {
        return DynamicContentSchemaType.MIXIN.name();
    }
}
