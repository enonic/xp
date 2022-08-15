package com.enonic.xp.lib.schema.mapper;

import java.time.Instant;
import java.util.Optional;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.site.SiteDescriptor;
import com.enonic.xp.site.XDataMapping;
import com.enonic.xp.site.XDataMappings;

public class SiteDescriptorMapper
    implements MapSerializable
{
    private final SiteDescriptor descriptor;

    private final Resource resource;

    public SiteDescriptorMapper( final SiteDescriptor descriptor, final Resource resource )
    {
        this.descriptor = descriptor;
        this.resource = resource;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "application", descriptor.getApplicationKey() );
        gen.value( "resource", resource.readString() );
        gen.value( "modifiedTime",
                   Optional.ofNullable( descriptor.getModifiedTime() ).orElse( Instant.ofEpochMilli( resource.getTimestamp() ) ) );

        DynamicSchemaSerializer.serializeForm( gen, descriptor.getForm() );
        serializeXDataMappings( gen, descriptor.getXDataMappings() );

    }

    private void serializeXDataMappings( final MapGenerator gen, final XDataMappings xDataMappings )
    {
        if ( xDataMappings != null && xDataMappings.getSize() > 0 )
        {
            gen.array( "xDataMappings" );
            for ( XDataMapping xDataMapping : xDataMappings )
            {
                gen.map();

                gen.value( "name", xDataMapping.getXDataName() );
                gen.value( "optional", xDataMapping.getOptional() );
                gen.value( "allowContentTypes", xDataMapping.getAllowContentTypes() );

                gen.end();

            }
            gen.end();
        }
    }
}
