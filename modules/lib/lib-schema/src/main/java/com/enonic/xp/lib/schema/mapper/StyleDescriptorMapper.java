package com.enonic.xp.lib.schema.mapper;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.style.ElementStyle;
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
        gen.value( "modifiedTime",
                   Optional.ofNullable( descriptor.getModifiedTime() ).orElse( Instant.ofEpochMilli( resource.getTimestamp() ) ) );
        gen.value( "resource", resource.readString() );

        serializeElements( gen, descriptor.getElements() );
    }

    private void serializeElements( final MapGenerator gen, final List<ElementStyle> elementStyles )
    {
        if ( elementStyles != null )
        {
            gen.array( "elements" );

            for ( final ElementStyle element : elementStyles )
            {
                gen.map();

                gen.value( "element", element.getElement() );
                gen.value( "displayName", element.getDisplayName() );
                gen.value( "name", element.getName() );

                gen.end();
            }
            gen.end();
        }
    }
}
