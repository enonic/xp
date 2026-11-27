package com.enonic.xp.repo.impl.dump;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

class DumpZipFilesTest
{
    private static final int ENTRY_COUNT = 5_000;

    @TempDir
    Path tempDir;

    private Path zipPath;

    @BeforeEach
    void setUp()
        throws IOException
    {
        zipPath = tempDir.resolve( "dump.zip" );
        try (ZipArchiveOutputStream out = new ZipArchiveOutputStream( Files.newOutputStream( zipPath ) ))
        {
            for ( int i = 0; i < ENTRY_COUNT; i++ )
            {
                writeEntry( out, "blobs/" + i, "blob-" + i );
            }
            writeEntry( out, "dump.json", "{\"xpVersion\":\"8.0.0\"}" );
        }
    }

    @Test
    void open_reads_a_fixed_number_of_regions_regardless_of_entry_count()
        throws IOException
    {
        try (SeekCountingChannel channel = new SeekCountingChannel( FileChannel.open( zipPath, StandardOpenOption.READ ) );
             ZipFile zipFile = DumpZipFiles.open( channel ))
        {
            assertThat( zipFile.getEntry( "dump.json" ) ).isNotNull();
            assertThat( channel.seeks ).isLessThan( 10 );
        }
    }

    @Test
    void reads_entry_content()
        throws IOException
    {
        try (ZipFile zipFile = DumpZipFiles.open( zipPath ))
        {
            assertThat( readEntry( zipFile, "dump.json" ) ).isEqualTo( "{\"xpVersion\":\"8.0.0\"}" );
            assertThat( readEntry( zipFile, "blobs/1234" ) ).isEqualTo( "blob-1234" );
        }
    }

    private static void writeEntry( final ZipArchiveOutputStream out, final String name, final String content )
        throws IOException
    {
        out.putArchiveEntry( new ZipArchiveEntry( name ) );
        out.write( content.getBytes( StandardCharsets.UTF_8 ) );
        out.closeArchiveEntry();
    }

    private static String readEntry( final ZipFile zipFile, final String name )
        throws IOException
    {
        try (InputStream stream = zipFile.getInputStream( zipFile.getEntry( name ) ))
        {
            return new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }
    }

    private static final class SeekCountingChannel
        implements SeekableByteChannel
    {
        private final SeekableByteChannel delegate;

        private long nextSequentialPosition = -1;

        private int seeks;

        SeekCountingChannel( final SeekableByteChannel delegate )
        {
            this.delegate = delegate;
        }

        @Override
        public int read( final ByteBuffer dst )
            throws IOException
        {
            final long position = delegate.position();
            if ( position != nextSequentialPosition )
            {
                seeks++;
            }
            final int read = delegate.read( dst );
            nextSequentialPosition = position + Math.max( read, 0 );
            return read;
        }

        @Override
        public int write( final ByteBuffer src )
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public long position()
            throws IOException
        {
            return delegate.position();
        }

        @Override
        public SeekableByteChannel position( final long newPosition )
            throws IOException
        {
            delegate.position( newPosition );
            return this;
        }

        @Override
        public long size()
            throws IOException
        {
            return delegate.size();
        }

        @Override
        public SeekableByteChannel truncate( final long size )
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isOpen()
        {
            return delegate.isOpen();
        }

        @Override
        public void close()
            throws IOException
        {
            delegate.close();
        }
    }
}
