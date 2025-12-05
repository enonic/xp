package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public abstract class DescriptorMapper<T extends ComponentDescriptor>
    implements MapSerializable
{
    protected final T descriptor;

    private final Resource resource;

    DescriptorMapper( final DynamicSchemaResult<T> descriptor )
    {
        this.descriptor = descriptor.getSchema();
        this.resource = descriptor.getResource();
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "key", descriptor.getKey() );
        gen.value( "displayName", descriptor.getDisplayName() );
        gen.value( "displayNameI18nKey", descriptor.getDisplayNameI18nKey() );
        gen.value( "description", descriptor.getDescription() );
        gen.value( "descriptionI18nKey", descriptor.getDescriptionI18nKey() );
        gen.value( "componentPath", descriptor.getComponentPath() );
        gen.value( "modifiedTime", descriptor.getModifiedTime() );
        gen.value( "resource", resource.readString() );
        gen.value( "type", getType() );

        DynamicSchemaSerializer.serializeForm( gen, descriptor.getConfig() );
        DynamicSchemaSerializer.serializeDescriptorConfig( gen, descriptor.getSchemaConfig() );
    }

    protected abstract String getType();

}
