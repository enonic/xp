package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.macro.MacroDescriptor;
import com.enonic.xp.resource.DynamicSchemaResult;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class MacroDescriptorMapper
    implements MapSerializable
{
    private final MacroDescriptor descriptor;

    private final Resource resource;

    public MacroDescriptorMapper( final DynamicSchemaResult<MacroDescriptor> result )
    {
        this.descriptor = result.getSchema();
        this.resource = result.getResource();
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "key", descriptor.getKey().toString() );
        gen.value( "name", descriptor.getName() );
        gen.value( "title", descriptor.getTitle() );
        gen.value( "titleI18nKey", descriptor.getTitleI18nKey() );
        gen.value( "description", descriptor.getDescription() );
        gen.value( "descriptionI18nKey", descriptor.getDescriptionI18nKey() );
        gen.value( "modifiedTime", descriptor.getModifiedTime() );
        gen.value( "resource", resource.readString() );
        DynamicSchemaSerializer.serializeForm( gen, descriptor.getForm() );
        gen.value( "config", descriptor.getSchemaConfig().toRawJs() );
        DynamicSchemaSerializer.serializeIcon( gen, descriptor.getIcon() );
    }
}
