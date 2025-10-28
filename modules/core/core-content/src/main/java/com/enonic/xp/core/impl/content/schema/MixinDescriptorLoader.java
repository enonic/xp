package com.enonic.xp.core.impl.content.schema;

import java.time.Instant;

import com.enonic.xp.core.impl.content.parser.YmlMixinDescriptorParser;
import com.enonic.xp.core.impl.schema.SchemaLoader;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.xdata.MixinDescriptor;
import com.enonic.xp.schema.xdata.MixinName;

final class MixinDescriptorLoader
    extends SchemaLoader<MixinName, MixinDescriptor>
{
    MixinDescriptorLoader( final ResourceService resourceService )
    {
        super( resourceService, "/cms/mixins" );
    }

    @Override
    protected MixinDescriptor load( final MixinName name, final Resource resource )
    {
        final MixinDescriptor.Builder builder = YmlMixinDescriptorParser.parse( resource.readString(), name.getApplicationKey() );

        final Instant modifiedTime = Instant.ofEpochMilli( resource.getTimestamp() );
        builder.modifiedTime( modifiedTime );
        builder.createdTime( modifiedTime );
        builder.icon( loadIcon( name ) );
        return builder.name( name ).build();
    }

    @Override
    protected MixinName newName( final DescriptorKey descriptorKey )
    {
        return MixinName.from( descriptorKey.getApplicationKey(), descriptorKey.getName() );
    }
}
