package com.enonic.wem.core.exporters;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Map;
import java.util.zip.ZipFile;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import com.enonic.wem.api.Identity;
import com.enonic.wem.api.exception.SystemException;

/**
 * export either to zip file or directory
 *
 * @param <T> - external entity type to export
 */
public abstract class AbstractEntityExporter<T>
    implements EntityExporter<T>
{
    private final static Map<String, String> ZIP_FS_ENV = ImmutableMap.of( "create", "true" );

    public Path exportToZip( final T object, final Path targetDirectory )
        throws IOException
    {
        final Identity identity = Identity.class.cast( object );

        final Path zipLocation = targetDirectory.resolve( identity.getKey().toString() + ".zip" );
        if ( Files.exists( zipLocation ) )
        {
            throw new FileAlreadyExistsException( zipLocation.toString() );
        }

        final URI fileUri = zipLocation.toUri();

        final URI zipUri;
        try
        {
            zipUri = new URI( "jar:" + fileUri.getScheme(), fileUri.getPath(), null );
        }
        catch ( URISyntaxException e )
        {
            throw new RuntimeException( e );
        }

        try (final FileSystem zipFs = FileSystems.newFileSystem( zipUri, ZIP_FS_ENV ))
        {
            exportObject( object, zipFs.getPath( "/" ) );
        }

        return zipLocation;
    }

    public Path exportToDirectory( final T object, final Path exportLocation )
        throws IOException
    {
        if ( !Files.isDirectory( exportLocation ) )
        {
            throw new FileNotFoundException( exportLocation.toString() );
        }

        final Identity identity = Identity.class.cast( object );

        final String directoryName = identity.getKey().toString();
        final Path rootPath = exportLocation.resolve( directoryName );
        createPath( rootPath );

        exportObject( object, rootPath );

        return rootPath;
    }

    protected Path createPath( final Path rootPath )
        throws IOException
    {
        if ( !Files.isDirectory( rootPath ) )
        {
            Files.createDirectory( rootPath );
        }

        return rootPath;
    }

    public T importFromZip( final Path zipFile )
        throws IOException
    {
        if ( !Files.exists( zipFile ) )
        {
            throw new NoSuchFileException( zipFile.toString() );
        }

        Preconditions.checkArgument( Files.isRegularFile( zipFile ), "Module file is not a file: " + zipFile );

        if ( !isValidZipFile( zipFile ) )
        {
            throw new SystemException( "Invalid zip file [{0}]", zipFile.getFileName() );
        }

        try (final FileSystem zipFs = FileSystems.newFileSystem( zipFile, null ))
        {
            return importFromDirectory( zipFs.getPath( "/" ).getRoot() );
        }
    }

    public T importFromDirectory( final Path directoryPath )
        throws IOException
    {
        if ( !Files.exists( directoryPath ) )
        {
            throw new NoSuchFileException( directoryPath.toString() );
        }

        try (final DirectoryStream<Path> ds = Files.newDirectoryStream( directoryPath ))
        {
            for ( final Path file : ds )
            {
                if ( !Files.isDirectory( file ) )
                {
                    final T object = importObject( directoryPath, file );

                    if ( object != null )
                    {
                        return object;
                    }
                }
            }
        }

        return null;
    }

    protected String getXmlFileName()
    {
        return getClass().getAnnotation( XMLFilename.class ).value();
    }

    protected T importObject( final Path directoryPath, final Path file )
        throws IOException
    {
        final String xml = new String( Files.readAllBytes( file ), Charset.forName( "UTF-8" ) );
        final AbstractEntityExporter<T> entityExporter = EntityExporters.<T>getByFilename( file.getFileName().toString() );
        return entityExporter != null ? entityExporter.fromXMLString( xml, directoryPath ) : null;
    }

    protected void exportObject( final T object, final Path rootPath )
        throws IOException
    {
        final Path xmlFile = rootPath.resolve( getXmlFileName() );
        final String xml = toXMLString( object );
        Files.write( xmlFile, xml.getBytes( Charset.forName( "UTF-8" ) ) );
    }

    private boolean isValidZipFile( final Path zipFilePath )
    {
        try (final ZipFile ignored = new ZipFile( zipFilePath.toFile() ))
        {
            return true;
        }
        catch ( IOException e )
        {
            return false;
        }
    }

    protected String resolveId( final Path directoryPath )
    {
        String externalFilename = directoryPath.toString();

        if ( "/".equals( externalFilename ) ) // ZIP archive
        {
            externalFilename = directoryPath.getFileSystem().toString();
        }

        return externalFilename.replaceAll( ".*[\\\\/]", "" ).replaceAll( "\\.zip$", "" );
    }

    protected abstract String toXMLString( final T object );

    protected abstract T fromXMLString( final String xml, final Path directoryPath )
        throws IOException;
}
