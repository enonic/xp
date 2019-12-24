package com.enonic.xp.repo.impl.elasticsearch.snapshot;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.client.indices.CloseIndexRequest;
import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.elasticsearch.client.impl.EsClient;
import com.enonic.xp.repo.impl.repository.IndexNameResolver;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.SystemConstants;

class AbstractSnapshotExecutor
{
    final String snapshotRepositoryName;

    final EsClient client;

    private final RepositoryService repositoryService;

    private static final Logger LOG = LoggerFactory.getLogger( AbstractSnapshotExecutor.class );

    AbstractSnapshotExecutor( final Builder builder )
    {
        snapshotRepositoryName = builder.snapshotRepositoryName;
        client = builder.client;
        repositoryService = builder.repositoryService;
    }

    void closeIndices( final RepositoryIds repositoryIds )
    {
        if ( repositoryIds != null )
        {
            final Set<String> indexNames = repositoryIds.stream().
                flatMap( repositoryId -> getIndexNames( repositoryId ).stream() ).
                collect( Collectors.toSet() );
            closeIndices( indexNames );
        }
    }

    void closeIndices( final Set<String> indexNames )
    {
        for ( final String indexName : indexNames )
        {
            try
            {
                this.client.indicesClose( new CloseIndexRequest( indexName ) );
                LOG.info( "Closed index " + indexName );
            }
            catch ( ElasticsearchStatusException e )
            {
                if ( e.status() == RestStatus.NOT_FOUND )
                {
                    LOG.warn( "Could not close index [" + indexName + "], not found" );
                }
            }
        }
    }

    void openIndices( final RepositoryIds repositoryIds )
    {
        if ( repositoryIds != null )
        {
            final Set<String> indexNames = repositoryIds.stream().
                flatMap( repositoryId -> getIndexNames( repositoryId ).stream() ).
                collect( Collectors.toSet() );
            openIndices( indexNames );
        }
    }

    void openIndices( final Set<String> indexNames )
    {
        for ( final String indexName : indexNames )
        {
            try
            {
                client.indicesOpen( new OpenIndexRequest( indexName ) );
                LOG.info( "Opened index " + indexName );
            }
            catch ( ElasticsearchException e )
            {
                LOG.warn( "Could not open index [" + indexName + "]" );
            }
        }
    }

    RepositoryIds getRepositories( boolean includeSystemRepo )
    {
        final Repositories list = this.repositoryService.list();

        return RepositoryIds.from( list.stream().
            filter( ( repo ) -> includeSystemRepo || !repo.getId().equals( SystemConstants.SYSTEM_REPO.getId() ) ).
            map( Repository::getId ).
            collect( Collectors.toSet() ) );
    }


    Set<String> getIndexNames( final RepositoryId repositoryId )
    {
        final Set<String> indices = new HashSet<>();

        indices.add( IndexNameResolver.resolveVersionIndexName( repositoryId ) );
        indices.add( IndexNameResolver.resolveBranchIndexName( repositoryId ) );
        indices.add( IndexNameResolver.resolveCommitIndexName( repositoryId ) );

        final Repository repository = this.repositoryService.get( repositoryId );

        if ( repository != null )
        {
            indices.addAll( IndexNameResolver.resolveSearchIndexNames( repositoryId, repository.getBranches() ) );
        }
        return indices;
    }


    public static class Builder<B extends Builder>
    {
        private String snapshotRepositoryName;

        private EsClient client;

        private RepositoryService repositoryService;

        @SuppressWarnings("unchecked")
        public B snapshotRepositoryName( final String val )
        {
            snapshotRepositoryName = val;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B client( final EsClient val )
        {
            client = val;
            return (B) this;
        }


        @SuppressWarnings("unchecked")
        public B repositoryService( final RepositoryService val )
        {
            repositoryService = val;
            return (B) this;
        }

    }
}
