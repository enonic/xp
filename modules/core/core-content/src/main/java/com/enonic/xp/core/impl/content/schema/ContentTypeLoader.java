package com.enonic.xp.core.impl.content.schema;

import java.time.Instant;

import com.enonic.xp.core.impl.content.parser.YmlContentTypeParser;
import com.enonic.xp.core.impl.schema.SchemaLoader;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;

final class ContentTypeLoader
    extends SchemaLoader<ContentTypeName, ContentType>
{
    ContentTypeLoader( final ResourceService resourceService )
    {
        super( resourceService, "/site/content-types" );
    }

    @Override
    protected ContentType load( final ContentTypeName name, final Resource resource )
    {
        final ContentType.Builder builder = YmlContentTypeParser.parse( resource.readString(), name.getApplicationKey() );

        final Instant modifiedTime = Instant.ofEpochMilli( resource.getTimestamp() );
        builder.modifiedTime( modifiedTime );
        builder.createdTime( modifiedTime );
        builder.name( name );
        return builder.build();
    }

    @Override
    protected ContentTypeName newName( final DescriptorKey descriptorKey )
    {
        return ContentTypeName.from( descriptorKey.getApplicationKey(), descriptorKey.getName() );
    }
}
