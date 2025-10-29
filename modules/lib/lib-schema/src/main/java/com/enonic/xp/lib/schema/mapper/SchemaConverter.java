package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.formfragment.FormFragmentDescriptor;
import com.enonic.xp.schema.mixin.MixinDescriptor;

public class SchemaConverter
{
    private SchemaConverter()
    {

    }

    public static SchemaMapper convert( final DynamicSchemaResult<? extends BaseSchema<?>> result )
    {
        final Object dynamicSchema = result.getSchema();
        if ( dynamicSchema instanceof ContentType )
        {
            return new ContentTypeMapper( (DynamicSchemaResult<ContentType>) result );
        }
        if ( dynamicSchema instanceof FormFragmentDescriptor )
        {
            return new FormFragmentMapper( (DynamicSchemaResult<FormFragmentDescriptor>) result );
        }
        if ( dynamicSchema instanceof MixinDescriptor )
        {
            return new MixinDescriptorMapper( (DynamicSchemaResult<MixinDescriptor>) result );
        }

        throw new IllegalArgumentException( "invalid component type: " + result.getClass() );
    }
}
