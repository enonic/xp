package com.enonic.xp.core.impl.content.schema;

import java.time.Instant;

import com.enonic.xp.core.impl.content.parser.YmlXDataParser;
import com.enonic.xp.core.impl.schema.SchemaLoader;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.xdata.XData;
import com.enonic.xp.schema.xdata.XDataName;

final class XDataLoader
    extends SchemaLoader<XDataName, XData>
{
    XDataLoader( final ResourceService resourceService )
    {
        super( resourceService, "/site/x-data" );
    }

    @Override
    protected XData load( final XDataName name, final Resource resource )
    {
        final XData.Builder builder = YmlXDataParser.parse( resource.readString(), name.getApplicationKey() );

        final Instant modifiedTime = Instant.ofEpochMilli( resource.getTimestamp() );
        builder.modifiedTime( modifiedTime );
        builder.createdTime( modifiedTime );
        builder.icon( loadIcon( name ) );
        return builder.name( name ).build();
    }

    @Override
    protected XDataName newName( final DescriptorKey descriptorKey )
    {
        return XDataName.from( descriptorKey.getApplicationKey(), descriptorKey.getName() );
    }
}
