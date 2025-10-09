package com.enonic.xp.core.impl.form.mixin;

import java.time.Instant;

import com.enonic.xp.core.impl.schema.SchemaLoader;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;

final class MixinLoader
    extends SchemaLoader<MixinName, Mixin>
{
    MixinLoader( final ResourceService resourceService )
    {
        super( resourceService, "/form-fragments" );
    }

    @Override
    protected Mixin load( final MixinName name, final Resource resource )
    {
        final Mixin.Builder builder = YmlMixinParser.parse( resource.readString(), name.getApplicationKey() );

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
