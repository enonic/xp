package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.schema.mixin.FormFragmentDescriptor;
import com.enonic.xp.script.serializer.MapGenerator;

public final class MixinMapper
    extends SchemaMapper<FormFragmentDescriptor>
{
    public MixinMapper( final DynamicSchemaResult<FormFragmentDescriptor> schema )
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
        return "MIXIN";
    }
}
