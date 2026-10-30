package com.enonic.xp.core.impl.content.schema;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.core.impl.content.parser.YmlContentTypeParser;
import com.enonic.xp.core.impl.schema.SchemaLoader;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.content.CmsFormFragmentService;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;

final class ContentTypeLoader
    extends SchemaLoader<ContentTypeName, ContentType>
{
    private static final Logger LOG = LoggerFactory.getLogger( ContentTypeLoader.class );

    private final CmsFormFragmentService formFragmentService;

    ContentTypeLoader( final ResourceService resourceService, final CmsFormFragmentService formFragmentService )
    {
        super( resourceService, "/cms/content-types" );
        this.formFragmentService = formFragmentService;
    }

    @Override
    protected ContentType load( final ContentTypeName name, final Resource resource )
    {
        final ContentType contentType;
        try
        {
            contentType = parse( name, resource );
        }
        catch ( final Exception e )
        {
            // a content type that fails to parse is logged and left out
            LOG.error( "Error loading content type: '{}'", name, e );
            return null;
        }

        // the form fragments are inlined here, into the value cached: a change of a fragment moves the version of the
        // application node, which recomputes the content type. A failure of the expansion is the caller's to see
        return ContentType.create( contentType ).form( formFragmentService.inlineFormItems( contentType.getForm() ) ).build();
    }

    private ContentType parse( final ContentTypeName name, final Resource resource )
    {
        final ContentType.Builder builder = YmlContentTypeParser.parse( resource.readString(), name.getApplicationKey() );

        builder.name( name );
        builder.icon( loadIcon( name, resource ) );

        final Instant modifiedTime = Instant.ofEpochMilli( resource.getTimestamp() );
        builder.modifiedTime( modifiedTime );
        builder.createdTime( modifiedTime );

        return builder.build();
    }

    @Override
    protected ContentTypeName newName( final DescriptorKey descriptorKey )
    {
        return ContentTypeName.from( descriptorKey.getApplicationKey(), descriptorKey.getName() );
    }
}
