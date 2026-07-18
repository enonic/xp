package com.enonic.xp.storage.nodb;

import io.grpc.CallCredentials;
import io.grpc.Channel;

import com.enonic.nodb.proto.v1.NodeStoreGrpc;
import com.enonic.nodb.proto.v1.RepositoryAdminGrpc;

/**
 * Test-only {@link NodbStorageClient} that wraps a pre-built {@link Channel} (an in-process
 * channel in these tests) instead of going through {@link NodbStorageClient#activate}'s real
 * TCP/config path -- {@code nodeStore()}/{@code repositoryAdmin()} are package-private
 * instance methods, overridable from this same-package test double without touching
 * production code.
 */
final class InProcessNodbStorageClient
    extends NodbStorageClient
{
    private final NodeStoreGrpc.NodeStoreBlockingStub nodeStoreStub;

    private final RepositoryAdminGrpc.RepositoryAdminBlockingStub repositoryAdminStub;

    InProcessNodbStorageClient( final Channel channel )
    {
        this( channel, null );
    }

    InProcessNodbStorageClient( final Channel channel, final String token )
    {
        NodeStoreGrpc.NodeStoreBlockingStub baseNodeStore = NodeStoreGrpc.newBlockingStub( channel );
        RepositoryAdminGrpc.RepositoryAdminBlockingStub baseAdmin = RepositoryAdminGrpc.newBlockingStub( channel );
        if ( token != null )
        {
            final CallCredentials credentials = new BearerTokenCallCredentials( token );
            baseNodeStore = baseNodeStore.withCallCredentials( credentials );
            baseAdmin = baseAdmin.withCallCredentials( credentials );
        }
        this.nodeStoreStub = baseNodeStore;
        this.repositoryAdminStub = baseAdmin;
    }

    @Override
    NodeStoreGrpc.NodeStoreBlockingStub nodeStore()
    {
        return nodeStoreStub;
    }

    @Override
    RepositoryAdminGrpc.RepositoryAdminBlockingStub repositoryAdmin()
    {
        return repositoryAdminStub;
    }
}
