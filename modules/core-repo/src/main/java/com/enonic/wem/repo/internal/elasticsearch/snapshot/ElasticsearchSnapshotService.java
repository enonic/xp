package com.enonic.wem.repo.internal.elasticsearch.snapshot;

import java.time.Instant;
import java.util.Set;

import org.elasticsearch.repositories.RepositoryMissingException;
import org.elasticsearch.snapshots.SnapshotInfo;
import org.elasticsearch.snapshots.SnapshotState;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Sets;

import com.enonic.xp.snapshot.DeleteSnapshotParams;
import com.enonic.xp.snapshot.DeleteSnapshotsResult;
import com.enonic.xp.snapshot.RestoreParams;
import com.enonic.xp.snapshot.RestoreResult;
import com.enonic.xp.snapshot.SnapshotParams;
import com.enonic.xp.snapshot.SnapshotResult;
import com.enonic.xp.snapshot.SnapshotResults;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchDao;
import com.enonic.wem.repo.internal.entity.NodeHelper;
import com.enonic.wem.repo.internal.snapshot.SnapshotService;

@Component
public class ElasticsearchSnapshotService
    implements SnapshotService
{
    private ElasticsearchDao elasticsearchDao;

    @Override
    public SnapshotResult snapshot( final SnapshotParams snapshotParams )
    {
        return NodeHelper.runAsAdmin( () -> doSnapshot( snapshotParams ) );
    }

    private SnapshotResult doSnapshot( final SnapshotParams snapshotParams )
    {
        return this.elasticsearchDao.snapshot( snapshotParams );
    }

    @Override
    public RestoreResult restore( final RestoreParams restoreParams )
    {
        return NodeHelper.runAsAdmin( () -> doRestore( restoreParams ) );
    }

    private RestoreResult doRestore( final RestoreParams restoreParams )
    {

        try
        {
            final SnapshotInfo snapshot = this.elasticsearchDao.getSnapshot( restoreParams.getSnapshotName() );

            if ( snapshot.state().equals( SnapshotState.FAILED ) )
            {
                return buildRestoreFailedResult( restoreParams,
                                                 "Failed to restore snapshot: Snapshot with name '" + restoreParams.getSnapshotName() +
                                                     "' is not valid" );
            }
        }
        catch ( RepositoryMissingException e )
        {
            return buildRestoreFailedResult( restoreParams,
                                             "Failed to restore snapshot: Snapshot with name '" + restoreParams.getSnapshotName() +
                                                 "' not found" );
        }
        catch ( Exception e )
        {
            return buildRestoreFailedResult( restoreParams, "Failed to restore snapshot: " + e.toString() );
        }

        return this.elasticsearchDao.restoreSnapshot( restoreParams );
    }

    private RestoreResult buildRestoreFailedResult( final RestoreParams restoreParams, final String message )
    {
        return RestoreResult.create().
            repositoryId( restoreParams.getRepositoryId() ).
            name( restoreParams.getSnapshotName() ).
            message( message ).
            failed( true ).
            build();
    }

    @Override
    public DeleteSnapshotsResult delete( final DeleteSnapshotParams params )
    {
        return NodeHelper.runAsAdmin( () -> doDelete( params ) );
    }

    @Override
    public void deleteSnapshotRepository()
    {
        this.elasticsearchDao.deleteSnapshotRepository();
    }

    private DeleteSnapshotsResult doDelete( final DeleteSnapshotParams params )
    {
        final DeleteSnapshotsResult.Builder builder = DeleteSnapshotsResult.create();

        if ( !params.getSnapshotNames().isEmpty() )
        {
            builder.addAll( deleteByName( params.getSnapshotNames() ) );
        }

        if ( params.getBefore() != null )
        {
            builder.addAll( deleteByBefore( params.getBefore() ) );
        }

        return builder.build();
    }

    private Set<String> deleteByBefore( final Instant before )
    {
        final Set<String> deleted = Sets.newHashSet();

        final SnapshotResults snapshotResults = doListSnapshots();

        for ( final SnapshotResult snapshotResult : snapshotResults )
        {
            if ( snapshotResult.getTimestamp().isBefore( before ) )
            {
                doDeleteSnapshot( snapshotResult.getName() );
                deleted.add( snapshotResult.getName() );
            }
        }

        return deleted;
    }

    private Set<String> deleteByName( final Set<String> snapshotNames )
    {
        final Set<String> deletedNames = Sets.newHashSet();

        for ( final String name : snapshotNames )
        {
            doDeleteSnapshot( name );
            deletedNames.add( name );
        }

        return deletedNames;
    }

    private void doDeleteSnapshot( final String snapshotName )
    {
        this.elasticsearchDao.deleteSnapshot( snapshotName );
    }


    @Override
    public SnapshotResults list()
    {
        return doListSnapshots();
    }

    private SnapshotResults doListSnapshots()
    {
        return this.elasticsearchDao.listSnapshots();
    }

    @Reference
    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }
}
