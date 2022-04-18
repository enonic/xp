package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.schema.BaseSchema;
import com.enonic.xp.schema.BaseSchemaName;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public abstract class SchemaMapper
    implements MapSerializable
{
    private final BaseSchema<? extends BaseSchemaName> descriptor;

    private final String resource;

    SchemaMapper( final DynamicSchemaResult<? extends BaseSchema<?>> descriptor )
    {
        this.descriptor = descriptor.getSchema();
        this.resource = descriptor.getResource().readString();
    }

    public void serialize( final MapGenerator gen )
    {
        gen.value( "name", descriptor.getName() );
        gen.value( "displayName", descriptor.getDisplayName() );
        gen.value( "displayNameI18nKey", descriptor.getDisplayNameI18nKey() );
        gen.value( "description", descriptor.getDescription() );
        gen.value( "descriptionI18nKey", descriptor.getDescriptionI18nKey() );
        gen.value( "createdTime", descriptor.getCreatedTime() );
        gen.value( "creator", descriptor.getCreator() );
        gen.value( "modifiedTime", descriptor.getModifiedTime() );
        gen.value( "modifier", descriptor.getModifier() );
        gen.value( "resource", resource );
        gen.value( "type", getType() );

    }

    protected abstract String getType();
}
