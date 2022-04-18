package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.schema.mixin.Mixin;

public final class MixinMapper
    extends SchemaMapper
{
    public MixinMapper( final DynamicSchemaResult<Mixin> schema )
    {
        super( schema );
    }

    @Override
    protected String getType()
    {
        return "Mixin";
    }
}
