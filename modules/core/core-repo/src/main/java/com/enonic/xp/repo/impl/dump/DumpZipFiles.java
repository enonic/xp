package com.enonic.xp.repo.impl.dump;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.apache.commons.compress.archivers.zip.ZipFile;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class DumpZipFiles
{
    private DumpZipFiles()
    {
    }

    public static ZipFile open( final Path zipPath )
        throws IOException
    {
        return open( Files.newByteChannel( zipPath, StandardOpenOption.READ ) );
    }

    public static ZipFile open( final SeekableByteChannel channel )
        throws IOException
    {
        return ZipFile.builder().setSeekableByteChannel( channel ).setIgnoreLocalFileHeader( true ).get();
    }
}
