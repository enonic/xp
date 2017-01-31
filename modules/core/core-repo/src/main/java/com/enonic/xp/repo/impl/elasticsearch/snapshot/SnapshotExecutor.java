package com.enonic.xp.repo.impl.elasticsearch.snapshot;

import java.util.Collection;
import java.util.List;

import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotRequestBuilder;
import org.elasticsearch.action.admin.cluster.snapshots.create.CreateSnapshotResponse;
import org.elasticsearch.common.settings.ImmutableSettings;

import com.google.common.collect.Lists;

import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;

public class SnapshotExecutor
    extends AbstractSnapshotExecutor
{
    private final String snapshotName;

    private final RepositoryId repositoryToSnapshot;

    private SnapshotExecutor( final Builder builder )
    {
        super( builder );
        snapshotName = builder.snapshotName;
        repositoryToSnapshot = builder.repositoryToRestore;
    }

    public SnapshotResult execute()
    {

        if ( this.repositoryToSnapshot == null )
        {
            return doSnapshotRepositories( getRepositories( true ) );
        }
        else
        {
            return doSnapshotRepositories( RepositoryIds.from( this.repositoryToSnapshot ) );
        }
    }

    private SnapshotResult doSnapshotRepositories( final RepositoryIds repoIds )
    {
        final List<String> indexNames = Lists.newArrayList();

        repoIds.forEach( ( repoId ) -> indexNames.addAll( getIndexNames( repoId ) ) );

        return executeSnapshotCommand( indexNames );
    }

    private SnapshotResult executeSnapshotCommand( final Collection<String> indices )
    {
        final CreateSnapshotRequestBuilder createRequest = new CreateSnapshotRequestBuilder( this.client.admin().cluster() ).
            setIndices( indices.toArray( new String[indices.size()] ) ).
            setIncludeGlobalState( false ).
            setWaitForCompletion( true ).
            setRepository( this.snapshotRepositoryName ).
            setSnapshot( snapshotName ).
            setSettings( ImmutableSettings.settingsBuilder().
                put( "ignore_unavailable", true ) );

        final CreateSnapshotResponse createSnapshotResponse =
            this.client.admin().cluster().createSnapshot( createRequest.request() ).actionGet();

        return SnapshotResultFactory.create( createSnapshotResponse );
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
        extends AbstractSnapshotExecutor.Builder<Builder>
    {
        private String snapshotName;

        private RepositoryId repositoryToRestore;

        private Builder()
        {
        }

        public Builder snapshotName( final String val )
        {
            snapshotName = val;
            return this;
        }

        public Builder repositoryToSnapshot( final RepositoryId val )
        {
            repositoryToRestore = val;
            return this;
        }

        public SnapshotExecutor build()
        {
            return new SnapshotExecutor( this );
        }
    }
}
