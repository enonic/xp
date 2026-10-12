package com.enonic.xp.internal.blobstore.file;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.blob.Segment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBlobStoreTest
{
    @TempDir
    public Path temporaryFolder;


    private FileBlobStore blobStore;

    private final Segment segment = Segment.from( "test", "blob" );

    @BeforeEach
    public void setup()
    {
        this.blobStore = new FileBlobStore( this.temporaryFolder );
    }

    @Test
    public void getRecord()
    {
        final BlobKey key = createRecord( "hello" ).getKey();
        final BlobRecord record = this.blobStore.getRecord( this.segment, key );
        assertNotNull( record );
    }

    @Test
    public void getRecord_notFound()
    {
        final BlobRecord record = this.blobStore.getRecord( this.segment, BlobKey.from( "aaf4c61ddcc5e8a2dabede0f3b482cd9aea9434d" ) );
        assertNull( record );
    }

    @Test
    public void addRecord()
        throws Exception
    {
        final BlobRecord record = this.blobStore.addRecord( this.segment, ByteSource.wrap( "hello".getBytes() ) );
        assertNotNull( record );
        assertNotNull( record.getKey() );
        assertEquals( "aaf4c61ddcc5e8a2dabede0f3b482cd9aea9434d", record.getKey().toString() );
        assertEquals( 5, record.getLength() );
        assertEquals( "hello", new String( record.getBytes().read() ) );
    }

    @Test
    public void removeRecord()
        throws Exception
    {
        final BlobRecord createdRecord = createRecord( "bye" );
        final BlobRecord retrievedRecord = this.blobStore.getRecord( this.segment, createdRecord.getKey() );
        assertNotNull( retrievedRecord );
        assertEquals( createdRecord.getKey(), retrievedRecord.getKey() );

        this.blobStore.removeRecord( this.segment, createdRecord.getKey() );
        final BlobRecord removedRecord = this.blobStore.getRecord( this.segment, createdRecord.getKey() );
        assertNull( removedRecord );
    }

    @Test
    public void removeRecord_deletesEmptyLastLevelDirectory()
    {
        final BlobKey key = createRecord( "bye" ).getKey();
        final Path lastLevelDir = blobPath( key ).getParent();

        this.blobStore.removeRecord( this.segment, key );

        assertFalse( Files.exists( lastLevelDir ), "empty last level directory must be deleted" );
        assertTrue( Files.isDirectory( lastLevelDir.getParent() ), "higher level directories must be kept" );
        assertTrue( Files.isDirectory( lastLevelDir.getParent().getParent() ), "higher level directories must be kept" );
        assertEquals( List.of( segment ), blobStore.listSegments().collect( Collectors.toList() ), "segment directories must be kept" );
    }

    @Test
    public void removeRecord_keepsLastLevelDirectoryWithOtherBlobs()
        throws Exception
    {
        // keys sharing the first 6 characters end up in the same last level directory
        final BlobKey key1 = BlobKey.from( "aabbcc0000000000000000000000000000000001" );
        final BlobKey key2 = BlobKey.from( "aabbcc0000000000000000000000000000000002" );
        this.blobStore.addRecord( this.segment, new TestBlobRecord( key1, ByteSource.wrap( "one".getBytes() ) ) );
        this.blobStore.addRecord( this.segment, new TestBlobRecord( key2, ByteSource.wrap( "two".getBytes() ) ) );
        final Path lastLevelDir = blobPath( key1 ).getParent();
        assertEquals( lastLevelDir, blobPath( key2 ).getParent() );

        this.blobStore.removeRecord( this.segment, key1 );

        assertTrue( Files.isDirectory( lastLevelDir ), "non-empty last level directory must be kept" );
        assertNull( this.blobStore.getRecord( this.segment, key1 ) );
        assertEquals( "two", new String( this.blobStore.getRecord( this.segment, key2 ).getBytes().read() ) );

        this.blobStore.removeRecord( this.segment, key2 );

        assertFalse( Files.exists( lastLevelDir ), "empty last level directory must be deleted" );
    }

    @Test
    public void list_includesEmptyLastLevelDirectories()
        throws Exception
    {
        final BlobRecord record = createRecord( "hello" );
        // empty last level directories, e.g. left behind by blobs removed before this fix
        final Path emptyDir1 = blobPath( BlobKey.from( "aabbcc0000000000000000000000000000000008" ) ).getParent();
        final Path emptyDir2 = blobPath( BlobKey.from( "aabbdd0000000000000000000000000000000009" ) ).getParent();
        // an empty directory of a higher level is not listed
        final Path emptyHigherLevelDir = this.temporaryFolder.resolve( "test" ).resolve( "blob" ).resolve( "ee" ).resolve( "ff" );
        Files.createDirectories( emptyDir1 );
        Files.createDirectories( emptyDir2 );
        Files.createDirectories( emptyHigherLevelDir );

        final List<BlobKey> listed;
        try (Stream<BlobRecord> stream = this.blobStore.list( this.segment ))
        {
            listed = stream.map( BlobRecord::getKey ).collect( Collectors.toList() );
        }
        assertThat( listed ).containsExactlyInAnyOrder( record.getKey(), BlobKey.from( "aabbcc" ), BlobKey.from( "aabbdd" ) );

        // removing the record of an empty directory removes the directory, as vacuum does
        this.blobStore.removeRecord( this.segment, BlobKey.from( "aabbcc" ) );
        this.blobStore.removeRecord( this.segment, BlobKey.from( "aabbdd" ) );

        assertFalse( Files.exists( emptyDir1 ) );
        assertFalse( Files.exists( emptyDir2 ) );
        assertTrue( Files.isDirectory( emptyDir1.getParent() ) );
        assertTrue( Files.isDirectory( emptyHigherLevelDir ) );
        assertEquals( "hello", new String( this.blobStore.getRecord( this.segment, record.getKey() ).getBytes().read() ) );
        try (Stream<BlobRecord> stream = this.blobStore.list( this.segment ))
        {
            assertThat( stream.map( BlobRecord::getKey ) ).containsExactly( record.getKey() );
        }
    }

    @Test
    public void list_ignoresStrayFilesInHigherLevels()
        throws Exception
    {
        final BlobRecord record = createRecord( "hello" );
        final Path segmentDir = this.temporaryFolder.resolve( "test" ).resolve( "blob" );
        Files.write( segmentDir.resolve( "stray-level-1" ), "x".getBytes() );
        Files.write( blobPath( record.getKey() ).getParent().getParent().resolve( "stray-level-2" ), "x".getBytes() );

        try (Stream<BlobRecord> stream = this.blobStore.list( this.segment ))
        {
            assertThat( stream.map( BlobRecord::getKey ) ).containsExactly( record.getKey() );
        }
    }

    @Test
    public void addRecord_retriesWhenDirectoryRemovedConcurrently()
        throws Exception
    {
        final BlobKey key = BlobKey.from( "aabbcc0000000000000000000000000000000003" );
        final Path file = blobPath( key );

        // simulates concurrent cleanup deleting the last level directory right before the blob file gets created
        // (relies on FileBlobStore opening the source stream after creating the directory and before creating the file)
        final DirectoryRemovingByteSource source = new DirectoryRemovingByteSource( "hello", file.getParent(), 1 );

        final BlobRecord record = this.blobStore.addRecord( this.segment, new TestBlobRecord( key, source ) );

        assertEquals( 2, source.openCount.get() );
        assertTrue( Files.isRegularFile( file ) );
        assertEquals( "hello", new String( record.getBytes().read() ) );
        assertEquals( "hello", new String( this.blobStore.getRecord( this.segment, key ).getBytes().read() ) );
    }

    @Test
    public void addRecord_failsAfterLimitedNumberOfAttempts()
    {
        final BlobKey key = BlobKey.from( "aabbcc0000000000000000000000000000000004" );
        final Path file = blobPath( key );

        // directory keeps disappearing on every attempt
        final DirectoryRemovingByteSource source = new DirectoryRemovingByteSource( "hello", file.getParent(), Integer.MAX_VALUE );

        assertThrows( BlobStoreException.class, () -> this.blobStore.addRecord( this.segment, new TestBlobRecord( key, source ) ) );

        assertEquals( 3, source.openCount.get(), "number of attempts must be limited" );
        assertFalse( Files.exists( file ) );
        assertNull( this.blobStore.getRecord( this.segment, key ) );
    }

    @Test
    public void addRecord_concurrentWriterWinsDuringRetry()
        throws Exception
    {
        final BlobKey key = BlobKey.from( "aabbcc0000000000000000000000000000000007" );
        final Path file = blobPath( key );
        final AtomicInteger openCount = new AtomicInteger();
        final ByteSource source = new ByteSource()
        {
            @Override
            public InputStream openStream()
                throws IOException
            {
                if ( openCount.incrementAndGet() == 1 )
                {
                    // directory removed concurrently, write gets re-attempted
                    Files.delete( file.getParent() );
                }
                else
                {
                    // concurrent writer created the same blob first
                    Files.write( file, "other".getBytes() );
                }
                return new ByteArrayInputStream( "mine".getBytes() );
            }
        };

        final BlobRecord record = this.blobStore.addRecord( this.segment, new TestBlobRecord( key, source ) );

        assertEquals( 2, openCount.get() );
        assertEquals( "other", new String( record.getBytes().read() ) );
        assertEquals( "other", new String( this.blobStore.getRecord( this.segment, key ).getBytes().read() ) );
    }

    @Test
    public void addRecord_doesNotRetryWhenSourceFails()
    {
        final BlobKey key = BlobKey.from( "aabbcc0000000000000000000000000000000005" );
        final AtomicInteger openCount = new AtomicInteger();
        // source fails while being read, i.e. after the blob file got created
        final ByteSource source = new ByteSource()
        {
            @Override
            public InputStream openStream()
            {
                openCount.incrementAndGet();
                return new InputStream()
                {
                    @Override
                    public int read()
                        throws IOException
                    {
                        throw new NoSuchFileException( "source" );
                    }
                };
            }
        };

        assertThrows( BlobStoreException.class, () -> this.blobStore.addRecord( this.segment, new TestBlobRecord( key, source ) ) );

        assertEquals( 1, openCount.get(), "failure of the source must not be re-attempted" );
    }

    @Test
    public void list()
        throws Exception
    {
        List<BlobRecord> stored = new ArrayList<>();
        stored.add( createRecord( "f1" ) );
        stored.add( createRecord( "f2" ) );
        stored.add( createRecord( "f3" ) );
        stored.add( createRecord( "f4" ) );
        stored.add( createRecord( "f5" ) );
        stored.add( createRecord( "f6" ) );
        stored.add( createRecord( "f7" ) );
        try (Stream<BlobRecord> stream = this.blobStore.list( this.segment )) {
            assertThat(stream).containsAll( stored );
        }
    }

    @Test
    public void listSegments()
    {
        final Segment secondSegment = Segment.from( "test", "blob2" );
        assertEquals( 0, blobStore.listSegments().count() );

        createRecord( "hello" ).getKey();
        assertEquals( 1, blobStore.listSegments().count() );
        assertEquals( segment, blobStore.listSegments().findFirst().get() );

        createRecord( secondSegment, "hello" ).getKey();
        assertEquals( 2, blobStore.listSegments().count() );
    }

    @Test
    public void deleteSegment()
    {
        final Segment secondSegment = Segment.from( "test", "blob2" );
        createRecord( "hello" ).getKey();
        createRecord( secondSegment, "hello" ).getKey();
        blobStore.deleteSegment( secondSegment );
        assertEquals( 1, blobStore.listSegments().count() );
        assertEquals( segment, blobStore.listSegments().findFirst().get() );
    }

    private BlobRecord createRecord( final String str )
    {
        return createRecord( segment, str );
    }

    private BlobRecord createRecord( final Segment segment, final String str )
    {
        final ByteSource source = ByteSource.wrap( str.getBytes() );
        return this.blobStore.addRecord( segment, source );
    }

    private Path blobPath( final BlobKey key )
    {
        final String id = key.toString();
        return this.temporaryFolder.resolve( "test" )
            .resolve( "blob" )
            .resolve( id.substring( 0, 2 ) )
            .resolve( id.substring( 2, 4 ) )
            .resolve( id.substring( 4, 6 ) )
            .resolve( id );
    }

    private static final class TestBlobRecord
        implements BlobRecord
    {
        private final BlobKey key;

        private final ByteSource bytes;

        TestBlobRecord( final BlobKey key, final ByteSource bytes )
        {
            this.key = key;
            this.bytes = bytes;
        }

        @Override
        public BlobKey getKey()
        {
            return key;
        }

        @Override
        public long getLength()
        {
            return bytes.sizeIfKnown().or( 0L );
        }

        @Override
        public ByteSource getBytes()
        {
            return bytes;
        }

        @Override
        public long lastModified()
        {
            return 0;
        }
    }

    private static final class DirectoryRemovingByteSource
        extends ByteSource
    {
        private final byte[] content;

        private final Path directory;

        private final int removeTimes;

        final AtomicInteger openCount = new AtomicInteger();

        DirectoryRemovingByteSource( final String content, final Path directory, final int removeTimes )
        {
            this.content = content.getBytes();
            this.directory = directory;
            this.removeTimes = removeTimes;
        }

        @Override
        public InputStream openStream()
            throws IOException
        {
            if ( openCount.incrementAndGet() <= removeTimes )
            {
                Files.delete( directory );
            }
            return new ByteArrayInputStream( content );
        }
    }
}
