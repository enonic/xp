package com.enonic.xp.storage.nodb;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import com.enonic.nodb.proto.v1.Ack;
import com.enonic.nodb.proto.v1.CreateRepositoryRequest;
import com.enonic.nodb.proto.v1.DeleteRepositoryRequest;
import com.enonic.nodb.proto.v1.ExistsResponse;
import com.enonic.nodb.proto.v1.RepositoryAdminGrpc;
import com.enonic.nodb.proto.v1.RepositoryExistsRequest;

/** Test-only {@code RepositoryAdmin} service backed by {@link FakeNodbState}. */
final class StubRepositoryAdminService
    extends RepositoryAdminGrpc.RepositoryAdminImplBase
{
    private final FakeNodbState state;

    StubRepositoryAdminService( final FakeNodbState state )
    {
        this.state = state;
    }

    @Override
    public void createRepository( final CreateRepositoryRequest request, final StreamObserver<Ack> responseObserver )
    {
        if ( !state.repos.add( request.getRepoId() ) )
        {
            responseObserver.onError(
                Status.ALREADY_EXISTS.withDescription( "Repository already exists: " + request.getRepoId() ).asRuntimeException() );
            return;
        }
        responseObserver.onNext( Ack.newBuilder().build() );
        responseObserver.onCompleted();
    }

    @Override
    public void deleteRepository( final DeleteRepositoryRequest request, final StreamObserver<Ack> responseObserver )
    {
        if ( !state.repos.remove( request.getRepoId() ) )
        {
            responseObserver.onError(
                Status.NOT_FOUND.withDescription( "Unknown repo id: " + request.getRepoId() ).asRuntimeException() );
            return;
        }
        responseObserver.onNext( Ack.newBuilder().build() );
        responseObserver.onCompleted();
    }

    @Override
    public void repositoryExists( final RepositoryExistsRequest request, final StreamObserver<ExistsResponse> responseObserver )
    {
        responseObserver.onNext( ExistsResponse.newBuilder().setExists( state.repos.contains( request.getRepoId() ) ).build() );
        responseObserver.onCompleted();
    }
}
