package com.enonic.nodb.client;

import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import io.grpc.CallCredentials;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;

import com.enonic.nodb.proto.v1.Ack;
import com.enonic.nodb.proto.v1.BranchEntry;
import com.enonic.nodb.proto.v1.CreateRepositoryRequest;
import com.enonic.nodb.proto.v1.GetBranchEntryRequest;
import com.enonic.nodb.proto.v1.GetChildrenRequest;
import com.enonic.nodb.proto.v1.GetPayloadRequest;
import com.enonic.nodb.proto.v1.GetVersionRequest;
import com.enonic.nodb.proto.v1.NodeStoreGrpc;
import com.enonic.nodb.proto.v1.Payload;
import com.enonic.nodb.proto.v1.PutPayloadRequest;
import com.enonic.nodb.proto.v1.PutPayloadResponse;
import com.enonic.nodb.proto.v1.RepositoryAdminGrpc;
import com.enonic.nodb.proto.v1.Version;
import com.enonic.nodb.proto.v1.WriteBatchRequest;
import com.enonic.nodb.proto.v1.WriteBatchResponse;

/**
 * Thin blocking gRPC client for NoDB (later becomes the core of XP's {@code nodb-client}).
 * Wraps the generated {@code NodeStoreGrpc}/{@code RepositoryAdminGrpc} blocking stubs over
 * a real TCP channel (plaintext — this slice has no mTLS/TLS story yet, matching the dev
 * issuer's own posture) and attaches the caller's bearer token as {@link CallCredentials} on
 * every call, the same "authorization: Bearer &lt;jwt&gt;" shape
 * {@code com.enonic.nodb.server.auth.TenantAuthInterceptor} verifies server-side.
 *
 * <h2>Where the generated stubs come from</h2>
 * This module does not run its own protobuf/grpc codegen against {@code nodb.proto} — that
 * would be a second codegen output from the same .proto file (server already runs one),
 * easy to let drift. Simplest path for slice 1 (see {@code client-java/build.gradle.kts}):
 * depend on {@code :server} (declared {@code api}, so it also reaches this class's
 * consumers, e.g. {@code :bench}) and reuse its generated classes directly. A cleaner
 * long-term shape is a dedicated proto-codegen module both sides depend on; not built here
 * since nothing in slice 1 runs client-java against anything other than a real server.
 */
public final class NodbClient
    implements AutoCloseable
{
    private static final Metadata.Key<String> AUTHORIZATION =
        Metadata.Key.of( "authorization", Metadata.ASCII_STRING_MARSHALLER );

    private final ManagedChannel channel;

    private final NodeStoreGrpc.NodeStoreBlockingStub nodeStore;

    private final RepositoryAdminGrpc.RepositoryAdminBlockingStub repositoryAdmin;

    private NodbClient( ManagedChannel channel, String token )
    {
        this.channel = channel;
        NodeStoreGrpc.NodeStoreBlockingStub baseNodeStore = NodeStoreGrpc.newBlockingStub( channel );
        RepositoryAdminGrpc.RepositoryAdminBlockingStub baseAdmin = RepositoryAdminGrpc.newBlockingStub( channel );
        if ( token == null )
        {
            this.nodeStore = baseNodeStore;
            this.repositoryAdmin = baseAdmin;
        }
        else
        {
            CallCredentials credentials = bearerToken( token );
            this.nodeStore = baseNodeStore.withCallCredentials( credentials );
            this.repositoryAdmin = baseAdmin.withCallCredentials( credentials );
        }
    }

    /** Opens a plaintext gRPC channel to {@code host:port}; every RPC carries {@code token} as a bearer credential. */
    public static NodbClient connect( String host, int port, String token )
    {
        ManagedChannel channel = ManagedChannelBuilder.forAddress( host, port ).usePlaintext().build();
        return new NodbClient( channel, token );
    }

    public WriteBatchResponse writeBatch( WriteBatchRequest request )
    {
        return nodeStore.writeBatch( request );
    }

    public BranchEntry getBranchEntry( GetBranchEntryRequest request )
    {
        return nodeStore.getBranchEntry( request );
    }

    public Iterator<BranchEntry> getChildren( GetChildrenRequest request )
    {
        return nodeStore.getChildren( request );
    }

    public Version getVersion( GetVersionRequest request )
    {
        return nodeStore.getVersion( request );
    }

    public PutPayloadResponse putPayload( PutPayloadRequest request )
    {
        return nodeStore.putPayload( request );
    }

    public Payload getPayload( GetPayloadRequest request )
    {
        return nodeStore.getPayload( request );
    }

    /**
     * Not in the slice-6 core method list, but every non-trivial caller (the bench harness
     * included) needs a repo to write into before it can call {@link #writeBatch}, and
     * {@code RepositoryAdmin.CreateRepository} is a one-RPC affair — adding it here beats
     * forcing callers to reach past this "thin client" just for setup.
     */
    public Ack createRepository( CreateRepositoryRequest request )
    {
        return repositoryAdmin.createRepository( request );
    }

    @Override
    public void close()
    {
        channel.shutdown();
        try
        {
            if ( !channel.awaitTermination( 5, TimeUnit.SECONDS ) )
            {
                channel.shutdownNow();
            }
        }
        catch ( InterruptedException e )
        {
            channel.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private static CallCredentials bearerToken( String token )
    {
        String headerValue = "Bearer " + token;
        return new CallCredentials()
        {
            @Override
            public void applyRequestMetadata( RequestInfo requestInfo, Executor appExecutor, MetadataApplier applier )
            {
                Metadata headers = new Metadata();
                headers.put( AUTHORIZATION, headerValue );
                applier.apply( headers );
            }
        };
    }
}
