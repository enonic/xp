package com.enonic.xp.repo.impl.dump;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public final class FileUtils
{
    private FileUtils()
    {
    }

    public static void copyDirectoryRecursively( final Path source, final Path destination )
        throws Exception
    {
        Files.walkFileTree( source, new DirectoryVisitor( source, destination, false ) );
    }

    public static void moveDirectory( final Path source, final Path destination )
        throws IOException
    {
        try
        {
            Files.move( source, destination );
        }
        catch ( DirectoryNotEmptyException e )
        {
            Files.walkFileTree( source, new DirectoryVisitor( source, destination, true ) );
        }
    }

    private static class DirectoryVisitor
        extends SimpleFileVisitor<Path>
    {
        private final Path source;

        private final Path target;

        private final boolean move;

        DirectoryVisitor( final Path source, final Path target, final boolean move )
        {
            this.source = source;
            this.target = target;
            this.move = move;
        }

        @Override
        public FileVisitResult visitFile( final Path file, final BasicFileAttributes attributes )
            throws IOException
        {
            final Path targetFile = target.resolve( source.relativize( file ) );
            if ( move )
            {
                Files.move( file, targetFile );
            }
            else
            {
                Files.copy( file, targetFile );
            }

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory( final Path dir, final BasicFileAttributes attributes )
            throws IOException
        {
            final Path newDir = target.resolve( source.relativize( dir ) );
            Files.copy( dir, newDir );

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory( final Path dir, final IOException exc )
            throws IOException
        {
            if ( exc != null )
            {
                throw exc;
            }
            if ( move )
            {
                Files.delete( dir );
            }
            return FileVisitResult.CONTINUE;
        }
    }
}
