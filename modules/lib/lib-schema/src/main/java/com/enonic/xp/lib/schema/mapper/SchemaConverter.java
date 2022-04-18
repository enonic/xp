package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.xdata.XData;

public class SchemaConverter
{
    public static SchemaMapper convert( final DynamicSchemaResult<? extends BaseSchema<?>> result )
    {
        final Object dynamicSchema = result.getSchema();
        if ( dynamicSchema instanceof ContentType )
        {
            return new ContentTypeMapper( (DynamicSchemaResult<ContentType>) result );
        }
        if ( dynamicSchema instanceof Mixin )
        {
            return new MixinMapper( (DynamicSchemaResult<Mixin>) result );
        }
        if ( dynamicSchema instanceof XData )
        {
            return new XDataMapper( (DynamicSchemaResult<XData>) result );
        }

        throw new IllegalArgumentException( "invalid component type: " + result.getClass() );
    }
}
