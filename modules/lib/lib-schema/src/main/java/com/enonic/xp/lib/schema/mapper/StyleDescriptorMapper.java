package com.enonic.xp.lib.schema.mapper;

import java.util.List;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.style.StyleDescriptor;

public class StyleDescriptorMapper
    implements MapSerializable
{
    private final StyleDescriptor descriptor;

    private final Resource resource;

    public StyleDescriptorMapper( final StyleDescriptor descriptor, final Resource resource )
    {
        this.descriptor = descriptor;
        this.resource = resource;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "application", descriptor.getApplicationKey() );
        gen.value( "cssPath", descriptor.getCssPath() );
        gen.value( "modifiedTime", descriptor.getModifiedTime() );
        gen.value( "resource", resource.readString() );

        serializeElements( gen, descriptor.getElements() );
    }

    private void serializeElements( final MapGenerator gen, final List<ImageStyle> elementStyles )
    {
        if ( elementStyles != null )
        {
            gen.array( "elements" );

            for ( final ImageStyle element : elementStyles )
            {
                gen.map();

                gen.value( "displayName", element.getDisplayName() );
                gen.value( "name", element.getName() );

                gen.end();
            }
            gen.end();
        }
    }
}
