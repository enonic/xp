package com.enonic.xp.core.impl.schema.mixin;

import java.time.Instant;

import com.enonic.xp.core.impl.schema.SchemaLoader;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.mixin.Mixin;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.xml.parser.XmlMixinParser;

final class MixinLoader
    extends SchemaLoader<MixinName, Mixin>
{
    MixinLoader( final ResourceService resourceService )
    {
        super( resourceService, "/site/mixins" );
    }

    @Override
    protected Mixin load( final MixinName name, final Resource resource )
    {
        final Mixin.Builder builder = Mixin.create();
        parseXml( resource, builder );

        final Instant modifiedTime = Instant.ofEpochMilli( resource.getTimestamp() );
        builder.modifiedTime( modifiedTime );
        builder.createdTime( modifiedTime );

        builder.icon( loadIcon( name ) );
        return builder.name( name ).build();
    }

    private void parseXml( final Resource resource, final Mixin.Builder builder )
    {
        final XmlMixinParser parser = new XmlMixinParser();
        parser.currentApplication( resource.getKey().getApplicationKey() );
        parser.source( resource.readString() );
        parser.builder( builder );
        parser.parse();
    }

    @Override
    protected MixinName newName( final DescriptorKey descriptorKey )
    {
        return MixinName.from( descriptorKey.getApplicationKey(), descriptorKey.getName() );
    }
}
