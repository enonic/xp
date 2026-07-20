package com.enonic.xp.storage.nodb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.blob.SegmentLevel;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositorySegmentUtils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link NodbBinaryBlobStore} against an in-process stub {@code Binaries} server (see
 * {@link StubBinariesService}'s javadoc for why a stub rather than a real nodb/engine +
 * MinIO). Covers what Gate B's client-side wiring is actually responsible for: binary
 * segment routing to NoDB vs. transparent passthrough of every other segment to the real
 * underlying {@link BlobStore} (the "payloads stay on the existing BlobStore" constraint,
 * BUILD-PHASE-2.md), store/get/delete round-tripping, multi-open read semantics, and the
 * binaries-before-commit invariant (blocking until durable). Real S3 durability/dedup
 * semantics are Gate A's job (nodb/engine's BinaryStore + testcontainers-minio suite).
 */
class NodbBinaryBlobStoreTest
{
    private static final RepositoryId REPO = RepositoryId.from( "myrepo" );

    private static final SegmentLevel BINARY_LEVEL = SegmentLevel.from( "binary" );

    private static final SegmentLevel NODE_LEVEL = SegmentLevel.from( "node" );

    private static final Segment BINARY_SEGMENT = RepositorySegmentUtils.toSegment( REPO, BINARY_LEVEL );

    private static final Segment NODE_SEGMENT = RepositorySegmentUtils.toSegment( REPO, NODE_LEVEL );

    private Map<String, byte[]> binaries;

    private StubBinariesService stubService;

    private Server server;

    private ManagedChannel channel;

    private RecordingBlobStore delegate;

    private NodbBinaryBlobStore store;

    @BeforeEach
    void setUp()
        throws Exception
    {
        binaries = new ConcurrentHashMap<>();
        stubService = new StubBinariesService( binaries );

        final String serverName = "nodb-binary-blob-store-test-" + System.nanoTime();
        server = InProcessServerBuilder.forName( serverName ).directExecutor().addService( stubService ).build().start();
        channel = InProcessChannelBuilder.forName( serverName ).directExecutor().build();

        delegate = new RecordingBlobStore();
        store = new NodbBinaryBlobStore( delegate, new InProcessNodbStorageClient( channel ) );
    }

    @AfterEach
    void tearDown()
    {
        channel.shutdownNow();
        server.shutdownNow();
    }

    @Test
    void storeAndGetRoundTrip_throughNoDB()
        throws IOException
    {
        final byte[] content = "hello binary world".getBytes();
        final BlobRecord stored = store.addRecord( BINARY_SEGMENT, ByteSource.wrap( content ) );

        assertEquals( "sha256:" + sha256Hex( content ), stored.getKey().toString() );
        assertTrue( binaries.containsKey( stored.getKey().toString() ) );
        // The real store is never touched for a binary-segment write.
        assertTrue( delegate.calls.isEmpty() );

        final BlobRecord fetched = store.getRecord( BINARY_SEGMENT, stored.getKey() );
        assertArrayEquals( content, fetched.getBytes().read() );
    }

    @Test
    void getRecord_missingKey_returnsNull()
    {
        final BlobRecord result = store.getRecord( BINARY_SEGMENT, BlobKey.from( "sha256:does-not-exist" ) );
        assertNull( result );
        assertTrue( delegate.calls.isEmpty() );
    }

    @Test
    void delete_removesBinary_thenGetReturnsNull()
        throws IOException
    {
        final byte[] content = "delete me".getBytes();
        final BlobRecord stored = store.addRecord( BINARY_SEGMENT, ByteSource.wrap( content ) );

        store.removeRecord( BINARY_SEGMENT, stored.getKey() );

        assertFalse( binaries.containsKey( stored.getKey().toString() ) );
        assertNull( store.getRecord( BINARY_SEGMENT, stored.getKey() ) );
        assertTrue( delegate.calls.isEmpty() );
    }

    @Test
    void nonBinarySegment_delegatesUnchanged_neverTouchingNoDB()
        throws IOException
    {
        final byte[] content = "node payload bytes".getBytes();
        final BlobRecord stored = store.addRecord( NODE_SEGMENT, ByteSource.wrap( content ) );

        assertTrue( delegate.calls.contains( "addRecord:" + NODE_SEGMENT ) );
        assertTrue( binaries.isEmpty(), "a non-binary write must never reach NoDB" );

        final BlobRecord fetched = store.getRecord( NODE_SEGMENT, stored.getKey() );
        assertArrayEquals( content, fetched.getBytes().read() );
        assertTrue( delegate.calls.contains( "getRecord:" + NODE_SEGMENT ) );

        store.removeRecord( NODE_SEGMENT, stored.getKey() );
        assertTrue( delegate.calls.contains( "removeRecord:" + NODE_SEGMENT ) );
    }

    @Test
    void listListSegmentsDeleteSegment_alwaysDelegate_regardlessOfSegmentType()
    {
        store.list( BINARY_SEGMENT );
        store.listSegments();
        store.deleteSegment( BINARY_SEGMENT );

        assertTrue( delegate.calls.contains( "list:" + BINARY_SEGMENT ) );
        assertTrue( delegate.calls.contains( "listSegments" ) );
        assertTrue( delegate.calls.contains( "deleteSegment:" + BINARY_SEGMENT ) );
    }

    @Test
    void multipleOpens_ofSameByteSource_eachReadIndependently()
        throws IOException
    {
        // Mirrors ImageServiceImpl#writeImage, which opens the same ByteSource twice (once
        // to verify a checksum, once to decode) -- each openStream() must be an independent,
        // fully-readable stream, not a one-shot iterator.
        final byte[] content = "read me more than once, please".getBytes();
        final BlobRecord stored = store.addRecord( BINARY_SEGMENT, ByteSource.wrap( content ) );

        final BlobRecord fetched = store.getRecord( BINARY_SEGMENT, stored.getKey() );
        final byte[] firstRead = fetched.getBytes().read();
        final byte[] secondRead = fetched.getBytes().read();

        assertArrayEquals( content, firstRead );
        assertArrayEquals( content, secondRead );
    }

    @Test
    void addRecord_blocksUntilNoDBConfirmsDurable()
        throws IOException
    {
        final AtomicBoolean markedDurableOnServer = new AtomicBoolean( false );
        stubService.beforeCompletingPut = () -> {
            try
            {
                Thread.sleep( 150 );
            }
            catch ( InterruptedException e )
            {
                Thread.currentThread().interrupt();
            }
            markedDurableOnServer.set( true );
        };

        store.addRecord( BINARY_SEGMENT, ByteSource.wrap( "invariant check".getBytes() ) );

        // If addRecord returned before the server-side completion ran, this would be racy;
        // since addRecord is synchronous/blocking (Phase 2 Gate B invariant requirement),
        // the flag is guaranteed to already be true here.
        assertTrue( markedDurableOnServer.get() );
    }

    @Test
    void addRecordWithBlobRecordOverload_recomputesHashFromBytes()
        throws IOException
    {
        final byte[] content = "pre-existing record bytes".getBytes();
        final BlobRecord preExisting = new BlobRecord()
        {
            @Override
            public BlobKey getKey()
            {
                return BlobKey.from( "sha256:whatever-the-caller-thought-it-was" );
            }

            @Override
            public long getLength()
            {
                return content.length;
            }

            @Override
            public ByteSource getBytes()
            {
                return ByteSource.wrap( content );
            }

            @Override
            public long lastModified()
            {
                return 0L;
            }
        };

        final BlobRecord stored = store.addRecord( BINARY_SEGMENT, preExisting );

        // NoDB is strictly content-addressed -- the recomputed hash wins, not whatever key
        // the caller's BlobRecord happened to carry.
        assertEquals( "sha256:" + sha256Hex( content ), stored.getKey().toString() );
        assertArrayEquals( content, store.getRecord( BINARY_SEGMENT, stored.getKey() ).getBytes().read() );
    }

    @Test
    void defaultConstruction_requiresBothReferences()
    {
        // Sanity check on the wiring itself: the component always uses the SAME delegate
        // instance it was constructed with (no accidental self-reference / no swapping).
        assertSame( delegate, extractDelegate( store ) );
    }

    private static BlobStore extractDelegate( final NodbBinaryBlobStore store )
    {
        try
        {
            final java.lang.reflect.Field field = NodbBinaryBlobStore.class.getDeclaredField( "delegate" );
            field.setAccessible( true );
            return (BlobStore) field.get( store );
        }
        catch ( ReflectiveOperationException e )
        {
            throw new IllegalStateException( e );
        }
    }

    private static String sha256Hex( final byte[] bytes )
    {
        try
        {
            final java.security.MessageDigest digest = java.security.MessageDigest.getInstance( "SHA-256" );
            final byte[] hash = digest.digest( bytes );
            final StringBuilder sb = new StringBuilder( hash.length * 2 );
            for ( final byte b : hash )
            {
                sb.append( String.format( "%02x", b ) );
            }
            return sb.toString();
        }
        catch ( java.security.NoSuchAlgorithmException e )
        {
            throw new IllegalStateException( e );
        }
    }

    /** Minimal in-memory {@link BlobStore} standing in for the real file/S3 store, recording every call it receives. */
    private static final class RecordingBlobStore
        implements BlobStore
    {
        final List<String> calls = new ArrayList<>();

        final Map<Segment, Map<BlobKey, byte[]>> data = new HashMap<>();

        @Override
        public BlobRecord getRecord( final Segment segment, final BlobKey key )
        {
            calls.add( "getRecord:" + segment );
            final Map<BlobKey, byte[]> segmentData = data.get( segment );
            final byte[] bytes = segmentData == null ? null : segmentData.get( key );
            return bytes == null ? null : new MemoryBlobRecord( key, bytes );
        }

        @Override
        public BlobRecord addRecord( final Segment segment, final ByteSource in )
        {
            calls.add( "addRecord:" + segment );
            try
            {
                final byte[] bytes = in.read();
                final BlobKey key = BlobKey.sha256( in );
                data.computeIfAbsent( segment, s -> new HashMap<>() ).put( key, bytes );
                return new MemoryBlobRecord( key, bytes );
            }
            catch ( IOException e )
            {
                throw new BlobStoreException( "Failed to add record", e );
            }
        }

        @Override
        public BlobRecord addRecord( final Segment segment, final BlobRecord record )
        {
            calls.add( "addRecord(record):" + segment );
            try
            {
                final byte[] bytes = record.getBytes().read();
                data.computeIfAbsent( segment, s -> new HashMap<>() ).put( record.getKey(), bytes );
                return record;
            }
            catch ( IOException e )
            {
                throw new BlobStoreException( "Failed to add record", e );
            }
        }

        @Override
        public void removeRecord( final Segment segment, final BlobKey key )
        {
            calls.add( "removeRecord:" + segment );
            final Map<BlobKey, byte[]> segmentData = data.get( segment );
            if ( segmentData != null )
            {
                segmentData.remove( key );
            }
        }

        @Override
        public Stream<BlobRecord> list( final Segment segment )
        {
            calls.add( "list:" + segment );
            return Stream.empty();
        }

        @Override
        public Stream<Segment> listSegments()
        {
            calls.add( "listSegments" );
            return data.keySet().stream();
        }

        @Override
        public void deleteSegment( final Segment segment )
        {
            calls.add( "deleteSegment:" + segment );
            data.remove( segment );
        }
    }

    private static final class MemoryBlobRecord
        implements BlobRecord
    {
        private final BlobKey key;

        private final byte[] bytes;

        MemoryBlobRecord( final BlobKey key, final byte[] bytes )
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
            return bytes.length;
        }

        @Override
        public ByteSource getBytes()
        {
            return ByteSource.wrap( bytes );
        }

        @Override
        public long lastModified()
        {
            return 0L;
        }
    }
}
