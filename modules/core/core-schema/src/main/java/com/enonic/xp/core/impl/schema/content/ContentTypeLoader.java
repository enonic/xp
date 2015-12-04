package com.enonic.xp.core.impl.schema.content;

import java.time.Instant;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.impl.schema.SchemaLoader;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.xml.parser.XmlContentTypeParser;

final class ContentTypeLoader
    extends SchemaLoader<ContentTypeName, ContentType>
{
    public ContentTypeLoader( final ResourceService resourceService )
    {
        super( resourceService, "/site/content-types" );
    }

    @Override
    protected ContentType load( final ContentTypeName name, final Resource resource )
    {
        final ContentType.Builder builder = ContentType.create();
        parseXml( resource, builder );

        final Instant modifiedTime = Instant.ofEpochMilli( resource.getTimestamp() );
        builder.modifiedTime( modifiedTime );
        builder.createdTime( modifiedTime );

        builder.icon( loadIcon( name ) );
        return builder.name( name ).build();
    }

    private void parseXml( final Resource resource, final ContentType.Builder builder )
    {
        final XmlContentTypeParser parser = new XmlContentTypeParser();
        parser.currentApplication( resource.getKey().getApplicationKey() );
        parser.source( resource.readString() );
        parser.builder( builder );
        parser.parse();
    }

    @Override
    protected ContentTypeName newName( final ApplicationKey appKey, final String name )
    {
        return ContentTypeName.from( appKey, name );
    }
}
