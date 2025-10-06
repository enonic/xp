package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.site.XDataMapping;
import com.enonic.xp.site.XDataMappings;

public class CmsDescriptorMapper
    implements MapSerializable
{
    private final CmsDescriptor descriptor;

    private final Resource resource;

    public CmsDescriptorMapper( final CmsDescriptor descriptor, final Resource resource )
    {
        this.descriptor = descriptor;
        this.resource = resource;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "application", descriptor.getApplicationKey() );
        gen.value( "resource", resource.readString() );
        gen.value( "modifiedTime", descriptor.getModifiedTime() );

        DynamicSchemaSerializer.serializeForm( gen, descriptor.getForm() );
        serializeXDataMappings( gen, descriptor.getXDataMappings() );
    }

    private void serializeXDataMappings( final MapGenerator gen, final XDataMappings xDataMappings )
    {
        if ( !xDataMappings.isEmpty() )
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
