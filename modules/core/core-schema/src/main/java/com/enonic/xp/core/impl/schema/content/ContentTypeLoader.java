package com.enonic.xp.core.impl.schema.content;

import java.time.Instant;

import com.enonic.xp.core.impl.schema.SchemaLoader;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.xml.parser.XmlContentTypeParser;

final class ContentTypeLoader
    extends SchemaLoader<ContentTypeName, ContentType>
{
    private final ContentTypeService contentTypeService;
    ContentTypeLoader( final ResourceService resourceService, ContentTypeService contentTypeService )
    {
        super( resourceService, "/site/content-types" );
        this.contentTypeService = contentTypeService;
    }

    @Override
    protected ContentType load( final ContentTypeName name, final Resource resource )
    {
        ContentType.Builder builder;
        if ( name.getExtension().equals( "yml" ) )
        {
            builder = contentTypeService.parseYml( resource.readString() );
        }
        else
        {
            builder = ContentType.create();
            parseXml( resource, builder );
            builder.icon( loadIcon( name ) );
        }

        final Instant modifiedTime = Instant.ofEpochMilli( resource.getTimestamp() );
        builder.modifiedTime( modifiedTime );
        builder.createdTime( modifiedTime );
        builder.name( name );
        return builder.build();
    }

    @Override
    protected ContentTypeName newName( final DescriptorKey descriptorKey )
    {
        return ContentTypeName.from( descriptorKey.getApplicationKey(), descriptorKey.getName(), descriptorKey.getExtension() );
    }

    private void parseXml( final Resource resource, final ContentType.Builder builder )
    {
        final XmlContentTypeParser parser = new XmlContentTypeParser();
        parser.currentApplication( resource.getKey().getApplicationKey() );
        parser.source( resource.readString() );
        parser.builder( builder );
        parser.parse();
    }
}
