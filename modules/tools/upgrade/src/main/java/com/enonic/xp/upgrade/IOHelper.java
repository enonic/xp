package com.enonic.xp.upgrade;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;

final class IOHelper
{
    public static CharSource getCharSource( final Path path )
    {
        try
        {
            return com.google.common.io.Files.asCharSource( path.toFile(), Charsets.UTF_8 );
        }
        catch ( Exception e )
        {
            throw new UpgradeException( "Failed to open file with path '" + path + "'", e );
        }
    }

    public static void copy( final Path source, final Path target )
    {
        try
        {
            Files.createDirectories( target.getParent() );
            Files.copy( source, target, StandardCopyOption.REPLACE_EXISTING );
        }
        catch ( IOException e )
        {
            throw new UpgradeException( "Failed to copy file with path '" + source + "' to '" + target + "'", e );
        }
    }

    public static Stream<Path> getChildren( final Path path )
    {
        try
        {
            return Files.list( path );
        }
        catch ( IOException e )
        {
            throw new UpgradeException( "Failed to get children of path '" + path + "'", e );
        }
    }

    public static void write( final Path path, final CharSource source )
    {
        try
        {
            if ( !Files.exists( path ) )
            {
                Files.createDirectories( path.getParent() );
                Files.createFile( path );
            }

            final CharSink charSink = com.google.common.io.Files.asCharSink( path.toFile(), Charsets.UTF_8 );

            source.copyTo( charSink );
        }
        catch ( IOException e )
        {
            throw new UpgradeException( "Failed to write upgrade-file to path '" + path + "'", e );
        }
    }
}
