package com.enonic.wem.core.schema.content.dao;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Charsets;

import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.api.schema.SchemaIcon;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.core.schema.SchemaIconDao;
import com.enonic.wem.core.schema.content.serializer.ContentTypeXmlSerializer;

import static com.enonic.wem.api.schema.content.ContentTypes.newContentTypes;
import static java.nio.file.Files.isDirectory;
import static java.nio.file.Files.isRegularFile;

public final class ContentTypeDaoImpl
    implements ContentTypeDao
{
    private static final String CONTENT_TYPE_XML = "content-type.xml";

    private Path basePath;

    @Override
    public ContentType createContentType( final ContentType contentType )
    {
        final Path contentTypePath = pathForContentType( contentType.getName() );

        writeContentTypeXml( contentType, contentTypePath );
        new SchemaIconDao().writeSchemaIcon( contentType.getIcon(), contentTypePath );

        return contentType;
    }

    @Override
    public void updateContentType( final ContentType contentType )
    {
        final Path contentTypePath = pathForContentType( contentType.getName() );

        writeContentTypeXml( contentType, contentTypePath );
        new SchemaIconDao().writeSchemaIcon( contentType.getIcon(), contentTypePath );
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
            final SchemaIcon icon = new SchemaIconDao().readSchemaIcon( contentTypePath );
            contentType.icon( icon );
            return contentType;
        }
        return null;
    }

    private void writeContentTypeXml( final ContentType contentType, final Path contentTypePath )
    {
        final ContentTypeXmlSerializer xmlSerializer = new ContentTypeXmlSerializer().prettyPrint( true ).generateName( false );
        final String serializedContentType = xmlSerializer.toString( contentType );
        final Path xmlFile = contentTypePath.resolve( CONTENT_TYPE_XML );

        try
        {
            Files.createDirectories( contentTypePath );
            Files.write( xmlFile, serializedContentType.getBytes( Charsets.UTF_8 ) );
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
            final String contentTypeName = contentTypePath.getFileName().toString();
            final ContentTypeXmlSerializer xmlSerializer = new ContentTypeXmlSerializer().overrideName( contentTypeName );
            final ContentType contentType = xmlSerializer.toContentType( serializedContentType );
            // TODO make content type xml parser return ContentType.Builder
            return ContentType.newContentType( contentType );
        }
        catch ( IOException e )
        {
            throw new SystemException( e, "Could not read content type [{0}]", contentTypePath.getFileName() );
        }
    }

    private Path pathForContentType( final ContentTypeName contentTypeName )
    {
        return this.basePath.resolve( contentTypeName.toString() );
    }

    @Inject
    public void setSystemConfig( final SystemConfig systemConfig )
        throws IOException
    {
        this.basePath = systemConfig.getContentTypesDir();
        Files.createDirectories( this.basePath );
    }
}
