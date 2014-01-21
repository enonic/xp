package com.enonic.wem.core.schema.content.dao;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;

import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.api.schema.SchemaIcon;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.core.schema.content.serializer.ContentTypeXmlSerializer;
import com.enonic.wem.util.MediaTypes;

import static com.enonic.wem.api.schema.content.ContentTypes.newContentTypes;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isRegularFile;

public final class ContentTypeDaoImpl
    implements ContentTypeDao
{
    private static final String CONTENT_TYPE_XML = "content-type.xml";

    private static final String ICON_FILE_NAME = "thumb";

    private final ImmutableMap<MediaType, String> imageTypeExtensions =
        ImmutableMap.of( MediaType.JPEG, "jpeg", MediaType.GIF, "gif", MediaType.BMP, "bmp", MediaType.PNG, "png" );

    private Path basePath;

    @Override
    public ContentType createContentType( final ContentType contentType )
    {
        final Path contentTypePath = pathForContentType( contentType.getName() );

        writeContentTypeXml( contentType, contentTypePath );
        writeContentTypeIcon( contentType, contentTypePath );

        return contentType;
    }

    @Override
    public void updateContentType( final ContentType contentType )
    {
        final Path contentTypePath = pathForContentType( contentType.getName() );

        writeContentTypeXml( contentType, contentTypePath );
        writeContentTypeIcon( contentType, contentTypePath );
    }

    @Override
    public ContentTypes getAllContentTypes()
    {
        final ContentTypes.Builder contentTypes = newContentTypes();

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream( this.basePath ))
        {
            for ( Path schemaDir : directoryStream )
            {
                final ContentType.Builder contentTypeBuilder = readContentType( schemaDir );
                final ContentType contentType = contentTypeBuilder != null ? contentTypeBuilder.build() : null;
                if ( contentType != null )
                {
                    contentTypes.add( contentType );
                }
            }
        }
        catch ( IOException e )
        {
            throw new SystemException( e, "Could not retrieve content types" );
        }
        return contentTypes.build();
    }

    @Override
    public ContentType.Builder getContentType( final ContentTypeName contentTypeName )
    {
        final Path contentTypePath = pathForContentType( contentTypeName );
        return readContentType( contentTypePath );
    }

    @Override
    public boolean deleteContentType( final ContentTypeName contentTypeName )
    {
        final Path contentTypePath = pathForContentType( contentTypeName );
        final boolean contentTypeDirExists = isDirectory( contentTypePath ) && isRegularFile( contentTypePath.resolve( CONTENT_TYPE_XML ) );
        if ( contentTypeDirExists )
        {
            try
            {
                FileUtils.deleteDirectory( contentTypePath.toFile() );
                return true;
            }
            catch ( IOException e )
            {
                throw new SystemException( e, "Could not delete content type [{0}]", contentTypeName );
            }
        }
        else
        {
            return false;
        }
    }

    private ContentType.Builder readContentType( final Path contentTypePath )
    {
        final boolean isContentTypeDir = isDirectory( contentTypePath ) && isRegularFile( contentTypePath.resolve( CONTENT_TYPE_XML ) );
        if ( isContentTypeDir )
        {
            final ContentType.Builder contentType = readContentTypeXml( contentTypePath );
            final SchemaIcon icon = readContentTypeIcon( contentTypePath );
            contentType.schemaIcon( icon );
            return contentType;
        }
        return null;
    }

    private void writeContentTypeIcon( final ContentType contentType, final Path contentTypePath )
    {
        final SchemaIcon icon = contentType.getSchemaIcon();
        if ( icon != null )
        {
            try
            {
                try (final DirectoryStream<Path> iconFiles = Files.newDirectoryStream( contentTypePath, ICON_FILE_NAME + ".*" ))
                {
                    for ( Path iconFile : iconFiles )
                    {
                        Files.deleteIfExists( iconFile );
                    }
                }
                final String iconFileName = getIconFileName( icon );
                final Path iconFile = contentTypePath.resolve( iconFileName );
                Files.copy( icon.asInputStream(), iconFile, StandardCopyOption.REPLACE_EXISTING );
            }
            catch ( IOException e )
            {
                throw new SystemException( e, "Could not store content type [{0}] icon", contentType.getName() );
            }
        }
    }

    private void writeContentTypeXml( final ContentType contentType, final Path contentTypePath )
    {
        final ContentTypeXmlSerializer xmlSerializer = new ContentTypeXmlSerializer();
        final String serializedContentType = xmlSerializer.toString( contentType );
        final Path xmlFile = contentTypePath.resolve( CONTENT_TYPE_XML );

        try
        {
            Files.createDirectories( contentTypePath );
            Files.write( xmlFile, serializedContentType.getBytes( Charsets.UTF_8 ), StandardOpenOption.CREATE );
        }
        catch ( IOException e )
        {
            throw new SystemException( e, "Could not store content type [{0}]", contentType.getName() );
        }
    }

    private ContentType.Builder readContentTypeXml( final Path contentTypePath )
    {
        final Path xmlFile = contentTypePath.resolve( CONTENT_TYPE_XML );
        try
        {
            final String serializedContentType = new String( Files.readAllBytes( xmlFile ), Charsets.UTF_8 );
            final ContentTypeXmlSerializer xmlSerializer = new ContentTypeXmlSerializer();
            final ContentType contentType = xmlSerializer.toContentType( serializedContentType );
            // TODO make content type xml parser return ContentType.Builder
            return ContentType.newContentType( contentType );
        }
        catch ( IOException e )
        {
            throw new SystemException( e, "Could not read content type [{0}]", contentTypePath.getFileName() );
        }
    }

    private SchemaIcon readContentTypeIcon( final Path contentTypePath )
    {
        try (final DirectoryStream<Path> iconFiles = Files.newDirectoryStream( contentTypePath, ICON_FILE_NAME + ".*" ))
        {
            for ( Path iconFile : iconFiles )
            {
                if ( Files.isRegularFile( iconFile ) )
                {
                    final String extension = FilenameUtils.getExtension( iconFile.getFileName().toString() );
                    final MediaType mediaType = MediaTypes.instance().fromExt( extension );
                    return SchemaIcon.from( Files.newInputStream( iconFile ), mediaType.toString() );
                }
            }
            return null;
        }
        catch ( IOException e )
        {
            throw new SystemException( e, "Could not read content type icon form [{0}]", contentTypePath );
        }
    }

    private Path pathForContentType( final ContentTypeName contentTypeName )
    {
        return this.basePath.resolve( contentTypeName.toString() );
    }

    private String getIconFileName( final SchemaIcon icon )
    {
        final String ext = imageTypeExtensions.get( MediaType.parse( icon.getMimeType() ) );
        return ext == null ? ICON_FILE_NAME : ICON_FILE_NAME + "." + ext;
    }

    @Inject
    public void setSystemConfig( final SystemConfig systemConfig )
        throws IOException
    {
        this.basePath = systemConfig.getSchemasDir();
        Files.createDirectories( this.basePath );
    }
}
