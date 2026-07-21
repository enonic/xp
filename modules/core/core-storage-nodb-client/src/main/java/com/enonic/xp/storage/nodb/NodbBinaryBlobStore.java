package com.enonic.xp.storage.nodb;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;
import com.google.protobuf.ByteString;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import com.enonic.nodb.proto.v1.BinaryExistsRequest;
import com.enonic.nodb.proto.v1.DeleteBinaryRequest;
import com.enonic.nodb.proto.v1.GetBinaryChunk;
import com.enonic.nodb.proto.v1.GetBinaryRequest;
import com.enonic.nodb.proto.v1.GetPayloadRequest;
import com.enonic.nodb.proto.v1.Payload;
import com.enonic.nodb.proto.v1.PutBinaryChunk;
import com.enonic.nodb.proto.v1.PutBinaryResponse;
import com.enonic.nodb.proto.v1.PutPayloadRequest;
import com.enonic.nodb.proto.v1.PutPayloadResponse;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.blob.SegmentLevel;
import com.enonic.xp.repository.RepositorySegmentUtils;

/**
 * {@link BlobStore} that diverts the BINARY segment (Phase 2 Gate B, nodb/BUILD-PHASE-2.md)
 * AND the three node-payload segments -- node data, index config, ACL (Phase 3 Gate B,
 * nodb/BUILD-PHASE-3.md) -- to NoDB; everything else is delegated straight through to
 * {@link #delegate}, the plain file/S3 {@code BlobStore} this component wraps.
 * <p>
 * <b>Payload segments, read side only:</b> {@link #getRecord} routes NODE/INDEX_CONFIG/
 * ACCESS_CONTROL segment reads to {@code GetPayload} (unary, unlike binaries' streaming --
 * payload rows are small JSON documents, never chunked). {@link #addRecord} also routes
 * these segments to {@code PutPayload} defensively, but is not expected to be reached in
 * practice: Phase 3 Gate B moved payload PERSISTENCE into the storage SPI itself
 * ({@code NodeStore#storeVersion}/{@code #storeNode}, carrying the bytes alongside the
 * version/branch-entry row so nodb can bundle them into one {@code WriteBatch} transaction)
 * -- no production caller writes these three segments through {@code BlobStore#addRecord}
 * any more, in either mode. Implementing the write branch anyway keeps this decorator
 * correct by construction rather than "correct because nothing calls it".
 * <p>
 * <b>One decorator, not two:</b> a SECOND {@code @Component(service = BlobStore.class,
 * property = {"storage.backend=nodb", ...})} sibling would compete with this one for the
 * same {@code @Reference BlobStore} consumers (DS picks exactly one same-ranked match per
 * static reference) -- and if that sibling copied this class's
 * {@code @Reference(target = "(!(storage.backend=nodb))")} delegate filter verbatim, it
 * would bind straight to the real file/S3 store rather than chaining through THIS
 * component, silently losing whichever segment kind the other component was meant to
 * divert. Extending this single class sidesteps the whole problem.
 *
 * <p><b>Why a decorator, not a second BlobStore SPI:</b> {@link BlobStore} has no built-in
 * per-segment provider selection -- {@code BlobStoreActivator} (core-blobstore) picks
 * exactly ONE {@code BlobStoreProvider} and registers exactly one {@code BlobStore} OSGi
 * service; every consumer ({@code BinaryServiceImpl}, {@code NodeVersionServiceImpl},
 * {@code AbstractBlobVacuumCommand}, {@code VersionTableVacuumCommand}, dump reader/writer)
 * holds a single {@code @Reference BlobStore}. Introducing a second, differently-scoped
 * service would require editing every one of those consumers to pick the right store per
 * call. A transparent decorator needs none of that: it registers as the (sole, highest-
 * ranked) {@code BlobStore}, and every existing {@code @Reference BlobStore} in core-repo
 * rebinds to it automatically (same static-reference-rebind-on-higher-ranking mechanism
 * Phase 1 already uses for {@code NodeStore}/{@code RepositoryStorageAdmin}, see {@link
 * NodbStorageClient}'s class javadoc) -- zero core-repo edits, and every call for a
 * non-binary segment is forwarded byte-for-byte unchanged to the real store.
 *
 * <p><b>Segment scoping</b>: a {@link Segment} is {@code [repositoryId, blobType]}
 * ({@code RepositorySegmentUtils.BLOB_TYPE_LEVEL}); the binary blob type is the string
 * {@code "binary"}, mirroring {@code com.enonic.xp.repo.impl.node.NodeConstants
 * .BINARY_SEGMENT_LEVEL} 1:1 (that constant lives in core-repo, which this module does not
 * -- and should not, to avoid a reverse compile dependency -- depend on; it is a plain
 * string literal wrapped in a {@link SegmentLevel}, not a moving target).
 *
 * <p><b>{@link #list}/{@link #listSegments}/{@link #deleteSegment} are always delegated
 * unchanged</b>, never routed to NoDB, deliberately: the {@code Binaries} RPC surface
 * (nodb.proto) has no listing/enumeration RPC at all -- NoDB's binary store is per-tenant,
 * hash-addressed, with no per-repository index to enumerate (Gate 0's "Nuance" on dedup
 * scope). This is not a functionality gap for the two real callers of these methods:
 * <ul>
 *   <li>{@code AbstractBlobVacuumCommand} (binary-blob mark-and-sweep GC) lists blob
 *   records to find delete candidates by AGE, then double-checks each against live
 *   versions before calling {@link #removeRecord} -- in nodb mode, binaries are never
 *   written to the real underlying store any more (this class diverts every binary write
 *   to NoDB), so {@code delegate.list()}/{@code listSegments()} correctly, harmlessly
 *   return nothing for the binary blob type: there is nothing stale left locally to find.
 *   Binary GC candidates in nodb mode are only ever discovered the OTHER way (below).
 *   <li>{@code VersionTableVacuumCommand}'s inline binary delete (BUILD-PHASE-2.md's
 *   "second GC path" surprise) never lists at all -- it already knows the exact
 *   {@code BlobKey}s to check from {@code node_version.binary_keys} of the version rows it
 *   is pruning, and calls {@link #removeRecord} directly for the ones no longer referenced.
 *   This path fully works in nodb mode via the {@link #removeRecord} routing below.
 * </ul>
 * Both GC paths' actual DELETE call is what matters for BUILD-PHASE-2.md item 5 ("both...
 * must route through the NoDB provider's delete") -- both go through {@link #removeRecord},
 * which this class intercepts for the binary segment regardless of which command called it.
 *
 * <p><b>Binaries-before-commit invariant</b> (BUILD-PHASE-2.md risk #4): {@link #addRecord}
 * blocks the calling thread until NoDB's {@code PutBinary} RPC response arrives (i.e. until
 * NoDB has confirmed the S3 {@code PutObject}/dedup-HEAD durably completed) before
 * returning -- see {@link #uploadViaPutBinary}. {@code CreateNodeCommand}/{@code
 * PatchNodeCommand} (core-repo, unmodified) already call {@code binaryService.store(...)}
 * (which reaches this class through {@code BlobStore.addRecord}) strictly before {@code
 * nodeStorageService.store(...)}; a synchronous, blocking {@code addRecord} is all that is
 * needed to preserve that pre-existing ordering (Gate 0 §5's finding) -- no new
 * synchronization primitive, no change to the command layer.
 *
 * <p><b>Streaming, not buffering</b>: {@link #addRecord} streams the {@link ByteSource} in
 * fixed-size chunks directly into the {@code PutBinary} client-streaming call ({@link
 * #uploadViaPutBinary}); {@link #getRecord}'s returned {@link BlobRecord#getBytes()} opens a
 * FRESH {@code GetBinary} server-streaming call on every {@code openStream()} (not a
 * one-shot cached iterator) -- required because real callers (e.g. {@code
 * ImageServiceImpl#writeImage}, which reads the same {@code ByteSource} once to verify a
 * sha512 checksum and again to decode the image) open the same {@link ByteSource} more than
 * once, and {@link ByteSource}'s contract requires each {@code openStream()} to yield an
 * independent stream. Neither direction ever buffers the whole binary in XP's heap.
 */
@Component(service = BlobStore.class, property = { "storage.backend=nodb", "service.ranking:Integer=100" })
public class NodbBinaryBlobStore
    implements BlobStore
{
    /**
     * Mirrors {@code com.enonic.xp.repo.impl.node.NodeConstants.BINARY_SEGMENT_LEVEL} (see
     * class javadoc for why this module does not depend on core-repo to reuse that constant
     * directly).
     */
    private static final SegmentLevel BINARY_SEGMENT_LEVEL = SegmentLevel.from( "binary" );

    /**
     * Mirror {@code com.enonic.xp.repo.impl.node.NodeConstants.NODE_SEGMENT_LEVEL}/
     * {@code INDEX_CONFIG_SEGMENT_LEVEL}/{@code ACCESS_CONTROL_SEGMENT_LEVEL} (see class
     * javadoc's note on {@code BINARY_SEGMENT_LEVEL} for why this module does not depend on
     * core-repo to reuse those constants directly).
     */
    private static final SegmentLevel NODE_SEGMENT_LEVEL = SegmentLevel.from( "node" );

    private static final SegmentLevel INDEX_CONFIG_SEGMENT_LEVEL = SegmentLevel.from( "index" );

    private static final SegmentLevel ACCESS_CONTROL_SEGMENT_LEVEL = SegmentLevel.from( "access" );

    /** Chunk size for the outbound PutBinary stream -- matches BinariesService.CHUNK_SIZE (nodb/server), not shared as a constant across the two separate Gradle builds. */
    private static final int CHUNK_SIZE = 256 * 1024;

    private final BlobStore delegate;

    private final NodbStorageClient client;

    @Activate
    public NodbBinaryBlobStore( @Reference(target = "(!(storage.backend=nodb))") final BlobStore delegate,
                                @Reference final NodbStorageClient client )
    {
        this.delegate = delegate;
        this.client = client;
    }

    @Override
    public BlobRecord getRecord( final Segment segment, final BlobKey key )
        throws BlobStoreException
    {
        if ( isPayloadSegment( segment ) )
        {
            return getPayloadRecord( key );
        }
        if ( !isBinarySegment( segment ) )
        {
            return delegate.getRecord( segment, key );
        }

        final String hash = key.toString();
        // Eager existence check (BlobStore contract: a missing record returns null
        // synchronously, same as FileBlobStore#getRecord's Files.exists check) -- see class
        // javadoc for why this is a fresh call every time rather than folded into the
        // GetBinary stream itself (multi-open ByteSource semantics).
        final boolean exists =
            NodbStatusMapper.existsCheck( () -> client.binaries().binaryExists( BinaryExistsRequest.newBuilder().setHash( hash ).build() )
                .getExists() );
        if ( !exists )
        {
            return null;
        }
        return new NodbBinaryBlobRecord( key, -1L, 0L, binaryByteSource( hash ) );
    }

    /**
     * Unary {@code GetPayload} (unlike binaries' streaming {@code GetBinary} -- payload rows
     * are small JSON documents, read whole). {@code NodeVersionServiceImpl}'s three Guava
     * caches sit in front of this (unchanged, §2.1) so a hot version's segments are fetched
     * once per process, not once per read.
     */
    private BlobRecord getPayloadRecord( final BlobKey key )
    {
        final String hash = key.toString();
        final Payload payload =
            NodbStatusMapper.pointGet( () -> client.nodeStore().getPayload( GetPayloadRequest.newBuilder().setHash( hash ).build() ) );
        if ( payload == null )
        {
            return null;
        }
        final byte[] bytes = payload.getBytes().toByteArray();
        return new NodbBinaryBlobRecord( key, bytes.length, -1L, ByteSource.wrap( bytes ) );
    }

    @Override
    public BlobRecord addRecord( final Segment segment, final ByteSource in )
        throws BlobStoreException
    {
        if ( isPayloadSegment( segment ) )
        {
            return addPayloadRecord( in );
        }
        if ( !isBinarySegment( segment ) )
        {
            return delegate.addRecord( segment, in );
        }
        try (InputStream content = in.openStream())
        {
            return uploadViaPutBinary( content );
        }
        catch ( IOException e )
        {
            throw new BlobStoreException( "Failed to read binary content for NoDB upload", e );
        }
    }

    /**
     * Defensive only -- see class javadoc's "payload segments, read side only" note: no
     * production caller writes a payload segment through {@link BlobStore#addRecord} any
     * more, but this keeps the decorator correct if one ever did.
     */
    private BlobRecord addPayloadRecord( final ByteSource in )
        throws BlobStoreException
    {
        final byte[] bytes;
        try
        {
            bytes = in.read();
        }
        catch ( IOException e )
        {
            throw new BlobStoreException( "Failed to read payload content for NoDB upload", e );
        }
        final PutPayloadResponse response = NodbStatusMapper.repoScoped(
            () -> client.nodeStore().putPayload( PutPayloadRequest.newBuilder().setBytes( ByteString.copyFrom( bytes ) ).build() ) );
        return new NodbBinaryBlobRecord( BlobKey.from( response.getHash() ), bytes.length, System.currentTimeMillis(),
                                          ByteSource.wrap( bytes ) );
    }

    @Override
    public BlobRecord addRecord( final Segment segment, final BlobRecord record )
        throws BlobStoreException
    {
        // Unlike FileBlobStore (which trusts record.getKey() and writes under that exact
        // key), NoDB is strictly content-addressed -- there is no "write at this key" RPC.
        // The hash is always recomputed server-side from the bytes; harmless in practice,
        // since a BlobKey is always sha256-of-bytes throughout this codebase (BlobKey.sha256
        // is the only production constructor for a binary attachment's key) and no
        // production caller invokes this overload for the binary segment today (grepped:
        // the only BINARY_SEGMENT_LEVEL caller of addRecord, AbstractEntryProcessor#addBinary,
        // uses the ByteSource overload above, not this one).
        return addRecord( segment, record.getBytes() );
    }

    @Override
    public void removeRecord( final Segment segment, final BlobKey key )
        throws BlobStoreException
    {
        if ( isPayloadSegment( segment ) )
        {
            // Payload GC deferred to Phase 5 (nodb/BUILD-PHASE-3.md Gate 0 decision (d)):
            // no DeletePayload RPC exists yet, and the only caller that would reach this
            // (VersionTableVacuumCommand's version-sweep, mirroring its binary GC path) is
            // already blocked in nodb mode for the same reason binary GC is --
            // AbstractBlobVacuumCommand#findVersions has no nodb-mode implementation, so
            // this branch is unreachable in practice today. An accepted no-op, not a silent
            // bug: nothing is lost since nothing was ever discovered to delete.
            return;
        }
        if ( !isBinarySegment( segment ) )
        {
            delegate.removeRecord( segment, key );
            return;
        }
        final String hash = key.toString();
        NodbStatusMapper.repoScopedVoid(
            () -> client.binaries().deleteBinary( DeleteBinaryRequest.newBuilder().setHash( hash ).build() ) );
    }

    /**
     * Always delegated: see class javadoc's "list/listSegments/deleteSegment" note. NoDB has
     * no per-segment binary listing RPC; in nodb mode no binary bytes are ever written to
     * {@link #delegate} any more, so its own listing of the binary blob type is correctly
     * empty rather than wrong.
     */
    @Override
    public Stream<BlobRecord> list( final Segment segment )
    {
        return delegate.list( segment );
    }

    @Override
    public Stream<Segment> listSegments()
    {
        return delegate.listSegments();
    }

    @Override
    public void deleteSegment( final Segment segment )
    {
        delegate.deleteSegment( segment );
    }

    private static boolean isBinarySegment( final Segment segment )
    {
        return RepositorySegmentUtils.hasBlobTypeLevel( segment, BINARY_SEGMENT_LEVEL );
    }

    private static boolean isPayloadSegment( final Segment segment )
    {
        return RepositorySegmentUtils.hasBlobTypeLevel( segment, NODE_SEGMENT_LEVEL ) ||
            RepositorySegmentUtils.hasBlobTypeLevel( segment, INDEX_CONFIG_SEGMENT_LEVEL ) ||
            RepositorySegmentUtils.hasBlobTypeLevel( segment, ACCESS_CONTROL_SEGMENT_LEVEL );
    }

    // ---- write path: client-streaming PutBinary, blocking until durable ------------------

    /**
     * Streams {@code content} into NoDB's {@code PutBinary} RPC (client-streaming) and BLOCKS
     * the calling thread until NoDB's single response arrives -- i.e. until the S3 write (or
     * dedup HEAD) is confirmed durable -- before returning. This is what gives {@link
     * #addRecord} the binaries-before-commit invariant (see class javadoc): the calling
     * thread cannot proceed to build/commit a version referencing this binary until this
     * method returns normally.
     * <p>
     * grpc-java's blocking stub flavor does not support client-streaming calls at all (only
     * unary and server-streaming) -- this bridges the async stub's callback shape back to a
     * synchronous call via a {@link CompletableFuture} that the response observer completes.
     */
    private BlobRecord uploadViaPutBinary( final InputStream content )
        throws IOException
    {
        final CompletableFuture<PutBinaryResponse> completion = new CompletableFuture<>();
        final StreamObserver<PutBinaryChunk> requestObserver = client.binariesAsync().putBinary( new StreamObserver<>()
        {
            @Override
            public void onNext( final PutBinaryResponse value )
            {
                completion.complete( value );
            }

            @Override
            public void onError( final Throwable t )
            {
                completion.completeExceptionally( t );
            }

            @Override
            public void onCompleted()
            {
                // A well-behaved PutBinary call always delivers exactly one onNext before
                // onCompleted; this guards against a server bug that completes the stream
                // without ever responding, so the caller does not block forever.
                completion.completeExceptionally(
                    new NodbClientException( "NoDB PutBinary completed without a response" ) );
            }
        } );

        long total = 0;
        final byte[] buffer = new byte[CHUNK_SIZE];
        int read;
        try
        {
            while ( ( read = content.read( buffer ) ) >= 0 )
            {
                if ( read == 0 )
                {
                    continue;
                }
                requestObserver.onNext( PutBinaryChunk.newBuilder().setChunk( ByteString.copyFrom( buffer, 0, read ) ).build() );
                total += read;
            }
            requestObserver.onCompleted();
        }
        catch ( IOException | RuntimeException e )
        {
            requestObserver.onError( e );
            throw e;
        }

        final PutBinaryResponse response;
        try
        {
            response = completion.get();
        }
        catch ( ExecutionException e )
        {
            throw NodbStatusMapper.translateThrowable( e.getCause() != null ? e.getCause() : e );
        }
        catch ( InterruptedException e )
        {
            Thread.currentThread().interrupt();
            throw new NodbClientException( "Interrupted while waiting for NoDB PutBinary to complete", e );
        }
        return new NodbBinaryBlobRecord( BlobKey.from( response.getHash() ), total, System.currentTimeMillis() );
    }

    // ---- read path: server-streaming GetBinary, re-opened per ByteSource#openStream ------

    private ByteSource binaryByteSource( final String hash )
    {
        return new ByteSource()
        {
            @Override
            public InputStream openStream()
            {
                return openBinaryStream( hash );
            }
        };
    }

    private InputStream openBinaryStream( final String hash )
    {
        final GetBinaryRequest request = GetBinaryRequest.newBuilder().setHash( hash ).build();
        final Iterator<GetBinaryChunk> chunks;
        try
        {
            chunks = client.binaries().getBinary( request );
        }
        catch ( StatusRuntimeException e )
        {
            throw NodbStatusMapper.translateThrowable( e );
        }
        return new GrpcChunkInputStream( chunks );
    }

    /** Adapts a {@code GetBinary} response iterator to a plain, chunk-draining {@link InputStream}. */
    private static final class GrpcChunkInputStream
        extends InputStream
    {
        private final Iterator<GetBinaryChunk> chunks;

        private ByteString current = ByteString.EMPTY;

        private int pos;

        GrpcChunkInputStream( final Iterator<GetBinaryChunk> chunks )
        {
            this.chunks = chunks;
        }

        @Override
        public int read()
            throws IOException
        {
            if ( !ensureData() )
            {
                return -1;
            }
            return current.byteAt( pos++ ) & 0xFF;
        }

        @Override
        public int read( final byte[] b, final int off, final int len )
            throws IOException
        {
            if ( len == 0 )
            {
                return 0;
            }
            if ( !ensureData() )
            {
                return -1;
            }
            final int available = current.size() - pos;
            final int toCopy = Math.min( available, len );
            current.substring( pos, pos + toCopy ).copyTo( b, off );
            pos += toCopy;
            return toCopy;
        }

        private boolean ensureData()
            throws IOException
        {
            while ( pos >= current.size() )
            {
                if ( !hasNextChunk() )
                {
                    return false;
                }
                current = chunks.next().getChunk();
                pos = 0;
            }
            return true;
        }

        private boolean hasNextChunk()
            throws IOException
        {
            try
            {
                return chunks.hasNext();
            }
            catch ( StatusRuntimeException e )
            {
                throw new IOException( "NoDB GetBinary stream failed: " + e.getMessage(), NodbStatusMapper.translateThrowable( e ) );
            }
        }
    }

    /**
     * {@link BlobRecord} for the NoDB-routed binary segment. {@code length}/{@code
     * lastModified} are best-effort: known exactly on the write path (bytes actually
     * streamed, timestamp of the just-completed write) but NOT available on the read path
     * without either buffering the whole binary or an extra RPC the wire protocol does not
     * offer (NoDB's GetBinary/BinaryExists carry no size/mtime metadata) -- verified
     * (grep across core-repo/core-image/portal-impl) that no production caller reads {@link
     * #getLength()}/{@link #lastModified()} on a record obtained for the BINARY segment
     * specifically (those two accessors are only read by {@code NodeVersionServiceImpl} for
     * node/index/ACL segments, and by {@code CachedBlobStore}/{@code ReadThroughBlobStore},
     * both of which sit on {@link #delegate}, never on binary-segment records produced by
     * this class) -- so -1/0 sentinels here are inert in practice, documented rather than
     * silently wrong.
     */
    private static final class NodbBinaryBlobRecord
        implements BlobRecord
    {
        private final BlobKey key;

        private final long length;

        private final long lastModified;

        private final ByteSource bytes;

        /**
         * Write-path convenience: {@link #addRecord} callers (see class javadoc) never read
         * {@link #getBytes()} back off the record they just wrote (only {@link #getKey()}),
         * so an empty placeholder {@link ByteSource} is correct here, not a shortcut that
         * loses data -- the bytes were already fully streamed to NoDB by the time this is
         * constructed.
         */
        NodbBinaryBlobRecord( final BlobKey key, final long length, final long lastModified )
        {
            this( key, length, lastModified, ByteSource.empty() );
        }

        NodbBinaryBlobRecord( final BlobKey key, final long length, final long lastModified, final ByteSource bytes )
        {
            this.key = key;
            this.length = length;
            this.lastModified = lastModified;
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
            return length;
        }

        @Override
        public ByteSource getBytes()
        {
            return bytes;
        }

        @Override
        public long lastModified()
        {
            return lastModified;
        }
    }
}
