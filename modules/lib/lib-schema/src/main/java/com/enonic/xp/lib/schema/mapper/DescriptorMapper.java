package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.form.Form;
import com.enonic.xp.region.ComponentDescriptor;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public abstract class DescriptorMapper
    implements MapSerializable
{
    private final ComponentDescriptor descriptor;

    private final String resource;

    DescriptorMapper( final DynamicSchemaResult<? extends ComponentDescriptor> descriptor )
    {
        this.descriptor = descriptor.getSchema();
        this.resource = descriptor.getResource().readString();
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "key", descriptor.getKey() );
        gen.value( "name", descriptor.getName() );
        gen.value( "displayName", descriptor.getDisplayName() );
        gen.value( "displayNameI18nKey", descriptor.getDisplayNameI18nKey() );
        gen.value( "description", descriptor.getDescription() );
        gen.value( "descriptionI18nKey", descriptor.getDescriptionI18nKey() );
        gen.value( "componentPath", descriptor.getComponentPath() );
        gen.value( "resource", resource );
        gen.value( "type", getType() );

        serializeConfig( gen );
    }

    protected abstract String getType();

    private void serializeConfig( final MapGenerator gen )
    {
        final Form config = descriptor.getConfig();
    }
}
