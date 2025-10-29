package com.enonic.xp.lib.schema.mapper;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.site.CmsDescriptor;
import com.enonic.xp.site.MixinMapping;
import com.enonic.xp.site.MixinMappings;

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
        serializeMixinMappings( gen, descriptor.getMixinMappings() );
    }

    private void serializeMixinMappings( final MapGenerator gen, final MixinMappings mixinMappings )
    {
        if ( !mixinMappings.isEmpty() )
        {
            gen.array( "mixinMappings" );
            for ( MixinMapping mixinMapping : mixinMappings )
            {
                gen.map();

                gen.value( "name", mixinMapping.getMixinName() );
                gen.value( "optional", mixinMapping.getOptional() );
                gen.value( "allowContentTypes", mixinMapping.getAllowContentTypes() );

                gen.end();

            }
            gen.end();
        }
    }
}
