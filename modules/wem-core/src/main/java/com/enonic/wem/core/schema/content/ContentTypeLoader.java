package com.enonic.wem.core.schema.content;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.xml.mapper.XmlContentTypeMapper;
import com.enonic.wem.api.xml.model.XmlContentType;
import com.enonic.wem.api.xml.serializer.XmlSerializers;
import com.enonic.wem.core.support.dao.IconDao;

import static java.util.stream.Collectors.toList;

public final class ContentTypeLoader
{
    private final static Logger LOG = LoggerFactory.getLogger( ContentTypeLoader.class );

    private final static Pattern CONTENT_TYPE_PATTERN = Pattern.compile( "content-type/([^/]+)/content-type\\.xml" );

    private final static String CONTENT_TYPE_FILE = "content-type.xml";

    private final static String CONTENT_TYPE_DIRECTORY = "content-type";

    private final IconDao iconDao = new IconDao();

    public ContentTypes loadContentTypes( final Module module )
    {
        final ModuleKey moduleKey = module.getKey();
        final List<ContentTypeName> contentTypeNames = findContentTypeNames( module );

        final List<ContentType> contentTypes = contentTypeNames.stream().
            map( ( contentTypeName ) -> loadContentType( moduleKey, contentTypeName ) ).
            filter( Objects::nonNull ).
            collect( toList() );

        return ContentTypes.from( contentTypes );
    }

    private ContentType loadContentType( final ModuleKey moduleKey, final ContentTypeName contentTypeName )
    {
        final String name = contentTypeName.getLocalName();
        final ResourceKey folderResourceKey = ResourceKey.from( moduleKey, CONTENT_TYPE_DIRECTORY + "/" + name );
        final ResourceKey contentTypeResourceKey = folderResourceKey.resolve( CONTENT_TYPE_FILE );

        final Resource resource = Resource.from( contentTypeResourceKey );
        if ( resource.exists() )
        {
            try
            {
                final String serializedResource = resource.readString();

                final ContentType.Builder contentType = parseContentTypeXml( serializedResource );
                final Instant modifiedTime = Instant.now();
                contentType.modifiedTime( modifiedTime );
                contentType.createdTime( modifiedTime );
                contentType.icon( iconDao.readIcon( folderResourceKey ) );
                return contentType.name( ContentTypeName.from( moduleKey, name ) ).build();
            }
            catch ( Exception e )
            {
                LOG.warn( "Could not load content type [" + contentTypeResourceKey + "]", e );
            }
        }
        return null;
    }

    private List<ContentTypeName> findContentTypeNames( final Module module )
    {
        return module.getResourcePaths().stream().
            map( this::getContentTypeNameFromResourcePath ).
            filter( Objects::nonNull ).
            map( ( localName ) -> ContentTypeName.from( module.getKey(), localName ) ).
            collect( toList() );
    }

    private String getContentTypeNameFromResourcePath( final String resourcePath )
    {
        final Matcher matcher = CONTENT_TYPE_PATTERN.matcher( resourcePath );
        return matcher.matches() ? matcher.group( 1 ) : null;
    }

    private ContentType.Builder parseContentTypeXml( final String serializedContentType )
    {
        final ContentType.Builder builder = ContentType.newContentType();
        final XmlContentType contentTypeXml = XmlSerializers.contentType().parse( serializedContentType );
        XmlContentTypeMapper.fromXml( contentTypeXml, builder );
        return builder;
    }

}
