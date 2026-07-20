package com.enonic.xp.storage.nodb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import com.google.protobuf.ByteString;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

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

/**
 * Test-only {@code Binaries} service backed by an in-memory map (see {@link FakeNodbState}'s
 * javadoc for why a stub, not the real nodb/engine {@code BinaryStore} + MinIO, is used here
 * -- Gate A's own testcontainers-minio suite already proves the real S3/dedup semantics;
 * this only needs to exercise {@link NodbBinaryBlobStore}'s wire-level routing/streaming/
 * blocking behavior). Content-addresses with the same {@code sha256:<hex>} scheme as the
 * real engine so hash-based assertions in tests read naturally.
 */
final class StubBinariesService
    extends BinariesGrpc.BinariesImplBase
{
    private final Map<String, byte[]> binaries;

    /** Test hook: run just before a PutBinary upload is marked durable and responded to -- used by the blocking/invariant test. */
    volatile Runnable beforeCompletingPut;

    /** Chunk size used when serving GetBinary -- deliberately small to force multi-chunk streaming even for tiny test payloads. */
    private static final int CHUNK_SIZE = 8;

    StubBinariesService( final Map<String, byte[]> binaries )
    {
        this.binaries = binaries;
    }

    @Override
    public StreamObserver<PutBinaryChunk> putBinary( final StreamObserver<PutBinaryResponse> responseObserver )
    {
        return new StreamObserver<>()
        {
            private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            @Override
            public void onNext( final PutBinaryChunk chunk )
            {
                try
                {
                    chunk.getChunk().writeTo( buffer );
                }
                catch ( IOException e )
                {
                    throw new IllegalStateException( e );
                }
            }

            @Override
            public void onError( final Throwable t )
            {
                // best-effort: nothing staged is kept on a failed upload
            }

            @Override
            public void onCompleted()
            {
                final byte[] bytes = buffer.toByteArray();
                final String hash = "sha256:" + sha256Hex( bytes );
                final Runnable hook = beforeCompletingPut;
                if ( hook != null )
                {
                    hook.run();
                }
                binaries.put( hash, bytes );
                responseObserver.onNext( PutBinaryResponse.newBuilder().setHash( hash ).build() );
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void getBinary( final GetBinaryRequest request, final StreamObserver<GetBinaryChunk> responseObserver )
    {
        final byte[] bytes = binaries.get( request.getHash() );
        if ( bytes == null )
        {
            responseObserver.onError( Status.NOT_FOUND.withDescription( "No such binary: " + request.getHash() ).asRuntimeException() );
            return;
        }
        for ( int i = 0; i < bytes.length; i += CHUNK_SIZE )
        {
            final int len = Math.min( CHUNK_SIZE, bytes.length - i );
            responseObserver.onNext( GetBinaryChunk.newBuilder().setChunk( ByteString.copyFrom( bytes, i, len ) ).build() );
        }
        responseObserver.onCompleted();
    }

    @Override
    public void binaryExists( final BinaryExistsRequest request, final StreamObserver<ExistsResponse> responseObserver )
    {
        responseObserver.onNext( ExistsResponse.newBuilder().setExists( binaries.containsKey( request.getHash() ) ).build() );
        responseObserver.onCompleted();
    }

    @Override
    public void deleteBinary( final DeleteBinaryRequest request, final StreamObserver<Ack> responseObserver )
    {
        binaries.remove( request.getHash() );
        responseObserver.onNext( Ack.newBuilder().build() );
        responseObserver.onCompleted();
    }

    @Override
    public void presignGet( final PresignGetRequest request, final StreamObserver<PresignGetResponse> responseObserver )
    {
        // Not called by the Gate B client (stream-through-NoDB is the phase-2 default for
        // all reads, see nodb.proto's Binaries service comment) -- not exercised here.
        responseObserver.onError( Status.UNIMPLEMENTED.withDescription( "presignGet not exercised by Gate B tests" ).asRuntimeException() );
    }

    private static String sha256Hex( final byte[] bytes )
    {
        try
        {
            final MessageDigest digest = MessageDigest.getInstance( "SHA-256" );
            final byte[] hash = digest.digest( bytes );
            final StringBuilder sb = new StringBuilder( hash.length * 2 );
            for ( final byte b : hash )
            {
                sb.append( String.format( "%02x", b ) );
            }
            return sb.toString();
        }
        catch ( NoSuchAlgorithmException e )
        {
            throw new IllegalStateException( e );
        }
    }
}
