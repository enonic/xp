package com.enonic.nodb.server.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import com.google.protobuf.ByteString;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.nodb.engine.binary.BinaryNotFoundException;
import com.enonic.nodb.engine.binary.BinaryStore;
import com.enonic.nodb.proto.v1.Ack;
import com.enonic.nodb.proto.v1.BinariesGrpc;
import com.enonic.nodb.proto.v1.BinaryExistsRequest;
import com.enonic.nodb.proto.v1.DeleteBinaryRequest;
import com.enonic.nodb.proto.v1.ExistsResponse;
import com.enonic.nodb.proto.v1.GetBinaryChunk;
import com.enonic.nodb.proto.v1.GetBinaryRequest;
import com.enonic.nodb.proto.v1.PresignGetRequest;
import com.enonic.nodb.proto.v1.PresignGetResponse;
import com.enonic.nodb.proto.v1.PutBinaryChunk;
import com.enonic.nodb.proto.v1.PutBinaryResponse;
import com.enonic.nodb.server.auth.TenantAuthInterceptor;
import com.enonic.nodb.server.auth.TenantPrincipal;

/**
 * Data-plane {@code Binaries} RPCs (Phase 2 Gate A, BUILD-PHASE-2.md), wired directly to
 * the engine's {@link BinaryStore}. Tenant is taken ONLY from {@link
 * TenantAuthInterceptor#PRINCIPAL_KEY} — request messages never carry a tenant field, same
 * discipline as {@link NodeStoreService}. Accepts runtime OR operator scope (not in {@code
 * TenantAuthInterceptor}'s management-method set).
 *
 * <p><b>Streaming / backpressure</b>: {@link #putBinary} assembles the client-streamed
 * chunks into a local temp file as they arrive (bounded by {@link #MAX_UPLOAD_BYTES}, not
 * buffered in heap), then hands the finished file to {@link BinaryStore#storeStaged} once
 * the client signals completion — this is what gives the binaries-before-commit invariant
 * its "blocking until durable" property from XP's perspective (the RPC's response is not
 * sent until the S3 PUT/HEAD has actually completed). No manual gRPC flow-control tuning
 * (e.g. {@code disableAutoInboundFlowControl}) was needed: each accepted chunk is written
 * synchronously to local disk (always far faster than the network) before the next
 * message is delivered, so gRPC's default HTTP/2-window-based flow control already
 * back-pressures a client that sends faster than this service can drain — there is no slow
 * local sink here for a client to race ahead of. {@link #getBinary} mirrors this in the
 * download direction, emitting fixed {@link #CHUNK_SIZE} chunks.
 */
public final class BinariesService
    extends BinariesGrpc.BinariesImplBase
{
    private static final Logger LOG = LoggerFactory.getLogger( BinariesService.class );

    /** Chunk size used for the GetBinary (server-streaming download) direction. */
    static final int CHUNK_SIZE = 256 * 1024;

    /** Default presigned-URL TTL when the caller passes {@code ttl_seconds <= 0}. */
    private static final Duration DEFAULT_PRESIGN_TTL = Duration.ofMinutes( 15 );

    /**
     * Bounds the assembled temp file's disk usage for a single upload (DESIGN.md/
     * BUILD-PHASE-2.md's "size limits" callout) — 512 MiB comfortably covers today's
     * media/attachment use cases while bounding a single runaway/misbehaving upload.
     */
    private static final long MAX_UPLOAD_BYTES = 512L * 1024 * 1024;

    private final BinaryStore binaryStore;

    public BinariesService( BinaryStore binaryStore )
    {
        this.binaryStore = binaryStore;
    }

    @Override
    public StreamObserver<PutBinaryChunk> putBinary( StreamObserver<PutBinaryResponse> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        return new PutBinaryUpload( binaryStore, principal.tenantContext(), responseObserver );
    }

    @Override
    public void getBinary( GetBinaryRequest request, StreamObserver<GetBinaryChunk> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        try (InputStream content = binaryStore.get( principal.tenantContext(), request.getHash() ))
        {
            byte[] buffer = new byte[CHUNK_SIZE];
            int read;
            while ( ( read = content.read( buffer ) ) >= 0 )
            {
                if ( read == 0 )
                {
                    continue;
                }
                responseObserver.onNext(
                    GetBinaryChunk.newBuilder().setChunk( ByteString.copyFrom( buffer, 0, read ) ).build() );
            }
            responseObserver.onCompleted();
        }
        catch ( BinaryNotFoundException e )
        {
            responseObserver.onError( Status.NOT_FOUND.withDescription( e.getMessage() ).asRuntimeException() );
        }
        catch ( IOException e )
        {
            responseObserver.onError( Status.INTERNAL.withDescription( e.getMessage() ).withCause( e ).asRuntimeException() );
        }
    }

    @Override
    public void binaryExists( BinaryExistsRequest request, StreamObserver<ExistsResponse> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        boolean exists = binaryStore.exists( principal.tenantContext(), request.getHash() );
        responseObserver.onNext( ExistsResponse.newBuilder().setExists( exists ).build() );
        responseObserver.onCompleted();
    }

    @Override
    public void deleteBinary( DeleteBinaryRequest request, StreamObserver<Ack> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        binaryStore.delete( principal.tenantContext(), request.getHash() );
        responseObserver.onNext( Ack.newBuilder().build() );
        responseObserver.onCompleted();
    }

    @Override
    public void presignGet( PresignGetRequest request, StreamObserver<PresignGetResponse> responseObserver )
    {
        TenantPrincipal principal = currentPrincipal();
        Duration ttl = request.getTtlSeconds() > 0 ? Duration.ofSeconds( request.getTtlSeconds() ) : DEFAULT_PRESIGN_TTL;
        try
        {
            URL url = binaryStore.presignGet( principal.tenantContext(), request.getHash(), ttl );
            responseObserver.onNext( PresignGetResponse.newBuilder().setUrl( url.toString() ).build() );
            responseObserver.onCompleted();
        }
        catch ( RuntimeException e )
        {
            // Presigning never confirms the object actually exists (it's a pure signing
            // operation, no round trip to S3 for GetObject presign) -- any failure here is
            // an S3/STS-client-level problem (bad credentials, unreachable endpoint), not a
            // NOT_FOUND case, hence INTERNAL rather than trying to distinguish further.
            responseObserver.onError( Status.INTERNAL.withDescription( e.getMessage() ).withCause( e ).asRuntimeException() );
        }
    }

    private static TenantPrincipal currentPrincipal()
    {
        TenantPrincipal principal = TenantAuthInterceptor.PRINCIPAL_KEY.get();
        if ( principal == null )
        {
            // Defense in depth: every registered service is wrapped with the auth
            // interceptor in NodbServer, so this is unreachable in practice.
            throw Status.UNAUTHENTICATED.withDescription( "No authenticated tenant in context" ).asRuntimeException();
        }
        return principal;
    }

    /**
     * Assembles a client-streamed {@code PutBinary} upload into a local temp file, then
     * finalizes it through {@link BinaryStore#storeStaged} on {@link #onCompleted}. One
     * instance per RPC call — gRPC guarantees serialized (never concurrent) invocation of a
     * single call's {@link StreamObserver} callbacks, so no synchronization is needed here.
     */
    private static final class PutBinaryUpload
        implements StreamObserver<PutBinaryChunk>
    {
        private final BinaryStore binaryStore;

        private final com.enonic.nodb.engine.TenantContext tenant;

        private final StreamObserver<PutBinaryResponse> responseObserver;

        private final Path stagedFile;

        private OutputStream stagedOut;

        private long bytesWritten;

        private boolean failed;

        PutBinaryUpload( BinaryStore binaryStore, com.enonic.nodb.engine.TenantContext tenant,
                          StreamObserver<PutBinaryResponse> responseObserver )
        {
            this.binaryStore = binaryStore;
            this.tenant = tenant;
            this.responseObserver = responseObserver;
            try
            {
                this.stagedFile = Files.createTempFile( "nodb-putbinary-", ".upload" );
                this.stagedOut = Files.newOutputStream( stagedFile );
            }
            catch ( IOException e )
            {
                throw Status.INTERNAL.withDescription( "Failed to stage upload: " + e.getMessage() ).withCause( e )
                    .asRuntimeException();
            }
        }

        @Override
        public void onNext( PutBinaryChunk chunk )
        {
            if ( failed )
            {
                return;
            }
            try
            {
                byte[] bytes = chunk.getChunk().toByteArray();
                bytesWritten += bytes.length;
                if ( bytesWritten > MAX_UPLOAD_BYTES )
                {
                    fail( Status.RESOURCE_EXHAUSTED.withDescription( "Binary exceeds max upload size of " + MAX_UPLOAD_BYTES
                                                                           + " bytes" ).asRuntimeException() );
                    return;
                }
                stagedOut.write( bytes );
            }
            catch ( IOException e )
            {
                fail( Status.INTERNAL.withDescription( "Failed writing staged upload: " + e.getMessage() ).withCause( e )
                          .asRuntimeException() );
            }
        }

        @Override
        public void onError( Throwable t )
        {
            LOG.warn( "PutBinary stream failed for tenant {}", tenant.tenantId(), t );
            cleanup();
        }

        @Override
        public void onCompleted()
        {
            if ( failed )
            {
                return;
            }
            try
            {
                stagedOut.close();
                stagedOut = null;
                String hash = binaryStore.storeStaged( tenant, stagedFile );
                responseObserver.onNext( PutBinaryResponse.newBuilder().setHash( hash ).build() );
                responseObserver.onCompleted();
            }
            catch ( IOException e )
            {
                responseObserver.onError(
                    Status.INTERNAL.withDescription( "Failed to store binary: " + e.getMessage() ).withCause( e ).asRuntimeException() );
            }
            finally
            {
                cleanup();
            }
        }

        private void fail( io.grpc.StatusRuntimeException statusException )
        {
            failed = true;
            responseObserver.onError( statusException );
            cleanup();
        }

        private void cleanup()
        {
            try
            {
                if ( stagedOut != null )
                {
                    stagedOut.close();
                }
            }
            catch ( IOException ignore )
            {
                // best-effort close on an already-failed upload
            }
            try
            {
                Files.deleteIfExists( stagedFile );
            }
            catch ( IOException ignore )
            {
                // best-effort cleanup; a leftover temp file is not a correctness issue
            }
        }
    }
}
