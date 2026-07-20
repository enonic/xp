package com.enonic.xp.storage.nodb;

import io.grpc.CallCredentials;
import io.grpc.Channel;

import com.enonic.nodb.proto.v1.BinariesGrpc;
import com.enonic.nodb.proto.v1.NodeStoreGrpc;
import com.enonic.nodb.proto.v1.RepositoryAdminGrpc;

/**
 * Test-only {@link NodbStorageClient} that wraps a pre-built {@link Channel} (an in-process
 * channel in these tests) instead of going through {@link NodbStorageClient#activate}'s real
 * TCP/config path -- {@code nodeStore()}/{@code repositoryAdmin()}/{@code binaries()}/
 * {@code binariesAsync()} are package-private instance methods, overridable from this
 * same-package test double without touching production code.
 */
final class InProcessNodbStorageClient
    extends NodbStorageClient
{
    private final NodeStoreGrpc.NodeStoreBlockingStub nodeStoreStub;

    private final RepositoryAdminGrpc.RepositoryAdminBlockingStub repositoryAdminStub;

    private final BinariesGrpc.BinariesBlockingStub binariesBlockingStub;

    private final BinariesGrpc.BinariesStub binariesAsyncStub;

    InProcessNodbStorageClient( final Channel channel )
    {
        this( channel, null );
    }

    InProcessNodbStorageClient( final Channel channel, final String token )
    {
        NodeStoreGrpc.NodeStoreBlockingStub baseNodeStore = NodeStoreGrpc.newBlockingStub( channel );
        RepositoryAdminGrpc.RepositoryAdminBlockingStub baseAdmin = RepositoryAdminGrpc.newBlockingStub( channel );
        BinariesGrpc.BinariesBlockingStub baseBinariesBlocking = BinariesGrpc.newBlockingStub( channel );
        BinariesGrpc.BinariesStub baseBinariesAsync = BinariesGrpc.newStub( channel );
        if ( token != null )
        {
            final CallCredentials credentials = new BearerTokenCallCredentials( token );
            baseNodeStore = baseNodeStore.withCallCredentials( credentials );
            baseAdmin = baseAdmin.withCallCredentials( credentials );
            baseBinariesBlocking = baseBinariesBlocking.withCallCredentials( credentials );
            baseBinariesAsync = baseBinariesAsync.withCallCredentials( credentials );
        }
        this.nodeStoreStub = baseNodeStore;
        this.repositoryAdminStub = baseAdmin;
        this.binariesBlockingStub = baseBinariesBlocking;
        this.binariesAsyncStub = baseBinariesAsync;
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

    @Override
    BinariesGrpc.BinariesBlockingStub binaries()
    {
        return binariesBlockingStub;
    }

    @Override
    BinariesGrpc.BinariesStub binariesAsync()
    {
        return binariesAsyncStub;
    }
}
