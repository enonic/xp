package com.enonic.xp.lib.app.mapper;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.util.GenericValue;

public class ApplicationDescriptorMapper
    implements MapSerializable
{
    private final ApplicationDescriptor descriptor;

    public ApplicationDescriptorMapper( final ApplicationDescriptor descriptor )
    {
        this.descriptor = descriptor;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "key", descriptor.getKey() );
        gen.value( "description", descriptor.getDescription() );
        gen.value( "descriptionI18nKey", descriptor.getDescriptionI18nKey() );
        gen.value( "title", descriptor.getTitle() );
        gen.value( "titleI18nKey", descriptor.getTitleI18nKey() );
        gen.value( "vendorName", descriptor.getVendorName() );
        gen.value( "vendorUrl", descriptor.getVendorUrl() );
        gen.value( "url", descriptor.getUrl() );
        serializeConfig( gen, descriptor.getSchemaConfig() );
        serializeIcon( gen, descriptor.getIcon() );
    }

    private void serializeConfig( final MapGenerator gen, final GenericValue config )
    {
        gen.map( "config" );
        config.properties().forEach( e -> gen.value( e.getKey(), e.getValue().toRawJs() ) );
        gen.end();
    }

    private void serializeIcon( final MapGenerator gen, final Icon icon )
    {
        if ( icon == null || icon.getSize() == 0 )
        {
            return;
        }

        gen.map( "icon" );
        gen.value( "data", new IconByteSource( icon ) );
        gen.value( "mimeType", icon.getMimeType() );
        gen.value( "modifiedTime", icon.getModifiedTime() );
        gen.end();
    }
}
