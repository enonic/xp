package com.enonic.xp.core.impl.export.reader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

import com.enonic.xp.util.Exceptions;
import com.enonic.xp.vfs.VirtualFile;
import com.enonic.xp.vfs.VirtualFilePath;
import com.enonic.xp.vfs.VirtualFilePaths;

public class ZipVirtualFile
    implements VirtualFile
{
    private final ZipFile zipFile;

    private final String entryPath;

    private final VirtualFilePath virtualFilePath;

    private final String basePath;

    private final Path zipFilePath;

    private ZipVirtualFile( final ZipFile zipFile, final String basePath, final String entryPath, final Path zipFilePath )
    {
        this.zipFile = zipFile;
        this.basePath = basePath;
        this.entryPath = entryPath;
        this.virtualFilePath = VirtualFilePaths.from( entryPath, "/" );
        this.zipFilePath = zipFilePath;
    }

    /**
     * Creates a VirtualFile from a zip archive.
     * Note: The ZipFile takes ownership of the SeekableByteChannel and will close it when the ZipFile is closed.
     *
     * @param zipPath path to the zip file
     * @return VirtualFile representing the root of the zip archive
     * @throws IOException if an I/O error occurs
     */
    public static VirtualFile from( final Path zipPath )
        throws IOException
    {
        final SeekableByteChannel seekableByteChannel = Files.newByteChannel( zipPath, StandardOpenOption.READ );
        final ZipFile zipFile = ZipFile.builder().setSeekableByteChannel( seekableByteChannel ).get();

        final String basePath = resolveBasePath( zipFile, zipPath );

        return new ZipVirtualFile( zipFile, basePath, basePath, zipPath );
    }

    private static String resolveBasePath( final ZipFile zipFile, final Path zipPath )
    {
        final Set<String> foldersWithExportProperties = new HashSet<>();
        final Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();

        while ( entries.hasMoreElements() )
        {
            final ZipArchiveEntry entry = entries.nextElement();
            final String name = entry.getName();

            final int slashIndex = name.indexOf( '/' );
            if ( slashIndex > 0 )
            {
                final String rootFolder = name.substring( 0, slashIndex );

                // Check if this is an export.properties file in a root folder
                if ( name.equals( rootFolder + "/export.properties" ) )
                {
                    foldersWithExportProperties.add( rootFolder );
                    if ( foldersWithExportProperties.size() > 2 )
                    {
                        throw new IllegalArgumentException( "Cannot determine base path for zip archive '" + zipPath.getFileName() +
                                                                "'. No folder with export.properties found." );
                    }
                }
            }
        }

        if ( foldersWithExportProperties.size() == 1 )
        {
            return foldersWithExportProperties.iterator().next();
        }

        throw new IllegalArgumentException(
            "Cannot determine base path for zip archive '" + zipPath.getFileName() + "'. Found multiple folders with export.properties: " +
                foldersWithExportProperties );
    }

    @Override
    public String getName()
    {
        if ( entryPath.isEmpty() || entryPath.equals( basePath ) )
        {
            return basePath;
        }
        final int lastSlash = entryPath.lastIndexOf( '/' );
        return lastSlash >= 0 ? entryPath.substring( lastSlash + 1 ) : entryPath;
    }

    @Override
    public VirtualFilePath getPath()
    {
        return this.virtualFilePath;
    }

    @Override
    public URL getUrl()
    {
        try
        {
            return new URL( "jar:file:" + zipFilePath.toAbsolutePath() + "!/" + entryPath );
        }
        catch ( MalformedURLException e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    @Override
    public boolean isFolder()
    {
        final ZipArchiveEntry entry = zipFile.getEntry( entryPath );
        if ( entry != null )
        {
            return entry.isDirectory();
        }

        // Check if there are any entries with this path as prefix
        final Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
        final String prefix = entryPath.isEmpty() ? "" : entryPath + "/";

        while ( entries.hasMoreElements() )
        {
            final ZipArchiveEntry e = entries.nextElement();
            if ( e.getName().startsWith( prefix ) && !e.getName().equals( prefix ) )
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isFile()
    {
        final ZipArchiveEntry entry = zipFile.getEntry( entryPath );
        return entry != null && !entry.isDirectory();
    }

    @Override
    public List<VirtualFile> getChildren()
    {
        final List<VirtualFile> children = new ArrayList<>();

        if ( !isFolder() )
        {
            return children;
        }

        final String prefix = entryPath.isEmpty() ? "" : entryPath + "/";
        final Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
        final Set<String> addedPaths = new HashSet<>();

        while ( entries.hasMoreElements() )
        {
            final ZipArchiveEntry entry = entries.nextElement();
            final String name = entry.getName();

            if ( name.startsWith( prefix ) && !name.equals( prefix ) )
            {
                final String relativeName = name.substring( prefix.length() );

                // Get the first path element (immediate child name)
                final int slashIndex = relativeName.indexOf( '/' );
                final String childName;

                if ( slashIndex == -1 )
                {
                    // It's a file directly in this folder
                    childName = relativeName;
                }
                else
                {
                    // It's a subdirectory (either explicit or implicit from nested file paths)
                    childName = relativeName.substring( 0, slashIndex );
                }

                final String childPath = prefix + childName;

                if ( !addedPaths.contains( childPath ) )
                {
                    addedPaths.add( childPath );
                    children.add( new ZipVirtualFile( zipFile, basePath, childPath, zipFilePath ) );
                }
            }
        }

        return children;
    }


    @Override
    public CharSource getCharSource()
    {
        return getByteSource().asCharSource( StandardCharsets.UTF_8 );
    }

    @Override
    public ByteSource getByteSource()
    {
        return new ByteSource()
        {
            @Override
            public java.io.InputStream openStream()
                throws IOException
            {
                final ZipArchiveEntry entry = zipFile.getEntry( entryPath );
                if ( entry == null )
                {
                    throw new IOException( "Entry not found: " + entryPath );
                }
                return zipFile.getInputStream( entry );
            }
        };
    }

    @Override
    public VirtualFile resolve( final VirtualFilePath path )
    {
        final String pathStr = path.getPath();

        // If the path starts with basePath, treat it as an absolute path within the zip
        // This mimics Path.resolve() behavior where resolve(absolutePath) returns absolutePath
        if ( pathStr.startsWith( basePath + "/" ) || pathStr.equals( basePath ) )
        {
            return new ZipVirtualFile( zipFile, basePath, pathStr, zipFilePath );
        }

        // Otherwise, resolve relative to current entryPath
        final String newPath = entryPath.isEmpty() ? pathStr : entryPath + "/" + pathStr;
        return new ZipVirtualFile( zipFile, basePath, newPath, zipFilePath );
    }

    @Override
    public boolean exists()
    {
        return zipFile.getEntry( entryPath ) != null || isFolder();
    }
}
