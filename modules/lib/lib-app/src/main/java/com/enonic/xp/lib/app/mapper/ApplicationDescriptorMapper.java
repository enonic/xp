package com.enonic.xp.lib.app.mapper;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

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
        serializeIcon( gen, descriptor.getIcon() );
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
