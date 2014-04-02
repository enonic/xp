package com.enonic.wem.core.support.export;

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
import com.google.common.collect.ImmutableSet;

import com.enonic.wem.api.Identity;
import com.enonic.wem.api.support.export.InvalidZipFileException;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.apache.commons.lang.StringUtils.stripEnd;

/**
 * export either to zip file or directory
 *
 * @param <I> - external entity type to export from
 * @param <O> - external entity type to import to
 */
public abstract class AbstractEntityExporter<I, O>
    implements EntityExporter<I, O>
{

    protected final static ImmutableSet<String> IGNORE_FILES = ImmutableSet.of( "__MACOSX", ".DS_Store" );

    private final static Map<String, String> ZIP_FS_ENV = ImmutableMap.of( "create", "true" );

    protected static final String NAME_SEPARATOR = ".";

    public Path exportToZip( final I object, final Path targetDirectory )
        throws IOException
    {
        final Identity identity = Identity.class.cast( object );

        final Path zipLocation = targetDirectory.resolve( identity.getName().toString() + ".zip" );
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
            final String rootPath = zipFs.getSeparator();
            final Path parentDirectory = zipFs.getPath( rootPath ).resolve( identity.getKey().toString() );
            createPath( parentDirectory );
            exportObject( object, parentDirectory, "" );
        }

        return zipLocation;
    }

    public Path exportToDirectory( final I object, final Path exportLocation )
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

        exportObject( object, rootPath, "" );

        return rootPath;
    }

    protected Path createPath( final Path rootPath )
        throws IOException
    {
        if ( !Files.isDirectory( rootPath ) )
        {
            Files.createDirectories( rootPath );
        }

        return rootPath;
    }

    public O importFromZip( final Path zipFile )
        throws IOException
    {
        if ( !Files.exists( zipFile ) )
        {
            throw new NoSuchFileException( zipFile.toString() );
        }

        Preconditions.checkArgument( Files.isRegularFile( zipFile ), "Module file is not a file: " + zipFile );

        checkValidZipFile( zipFile );

        try (final FileSystem zipFs = FileSystems.newFileSystem( zipFile, null ))
        {
            final String rootPath = zipFs.getSeparator();
            final Path root = zipFs.getPath( rootPath ).getRoot();
            final Path parentDir = getFirstDirectory( root );

            return importFromDirectory( parentDir );
        }
    }

    private Path getFirstDirectory( final Path parentDirectory )
        throws IOException
    {
        try (final DirectoryStream<Path> ds = Files.newDirectoryStream( parentDirectory ))
        {
            for ( Path file : ds )
            {
                if ( Files.isDirectory( file ) && !IGNORE_FILES.contains( getFileName( file ) ) )
                {
                    return file;
                }
            }
        }
        return null;
    }

    public O importFromDirectory( final Path directoryPath )
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
                if ( !Files.isDirectory( file ) && !IGNORE_FILES.contains( getFileName( file ) ) )
                {
                    final O object = importObject( directoryPath, file );

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

    public O importObject( final Path directoryPath, final Path file )
        throws IOException
    {
        final String xml = new String( Files.readAllBytes( file ), Charset.forName( "UTF-8" ) );
        return fromXMLString( xml, directoryPath );
    }

    public void exportObject( final I object, final Path rootPath, final String objectName )
        throws IOException
    {
        final String fileNameSuffix = getXmlFileName();
        final String xmlFileName = isNullOrEmpty( objectName ) ? fileNameSuffix : objectName + NAME_SEPARATOR + fileNameSuffix;
        final Path xmlFile = rootPath.resolve( xmlFileName );
        final String xml = toXMLString( object );
        Files.write( xmlFile, xml.getBytes( Charset.forName( "UTF-8" ) ) );
    }

    @SuppressWarnings("EmptyTryBlock")
    private void checkValidZipFile( final Path zipFilePath )
    {
        try (final ZipFile ignored = new ZipFile( zipFilePath.toFile() ))
        {
            // Everything OK!
        }
        catch ( IOException e )
        {
            throw new InvalidZipFileException( zipFilePath, e );
        }
    }

    protected String getFileName( final Path path )
    {
        return stripEnd( path.getFileName().toString(), path.getFileSystem().getSeparator() );
    }

    protected String resolveId( final Path directoryPath )
    {
        final String externalFilename;
        if ( directoryPath.getParent() == null ) // ZIP archive
        {
            externalFilename = directoryPath.getFileSystem().toString();
        }
        else
        {
            externalFilename = getFileName( directoryPath );
        }

        return externalFilename.replaceAll( ".*[\\\\/]", "" ).replaceAll( "\\.zip$", "" );
    }

    protected abstract String toXMLString( final I object );

    protected abstract O fromXMLString( final String xml, final Path directoryPath )
        throws IOException;
}
