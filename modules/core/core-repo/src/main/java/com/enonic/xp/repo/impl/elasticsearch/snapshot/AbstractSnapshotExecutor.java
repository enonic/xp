package com.enonic.xp.repo.impl.elasticsearch.snapshot;

import java.util.Set;
import java.util.stream.Collectors;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.close.CloseIndexAction;
import org.elasticsearch.action.admin.indices.close.CloseIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.open.OpenIndexAction;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.IndexNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

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

    final Client client;

    private final RepositoryService repositoryService;

    private final Logger LOG = LoggerFactory.getLogger( AbstractSnapshotExecutor.class );

    AbstractSnapshotExecutor( final Builder builder )
    {
        snapshotRepositoryName = builder.snapshotRepositoryName;
        client = builder.client;
        repositoryService = builder.repositoryService;
    }

    void closeIndices( final Set<String> indexNames )
    {
        for ( final String indexName : indexNames )
        {
            CloseIndexRequestBuilder closeIndexRequestBuilder =
                new CloseIndexRequestBuilder( this.client.admin().indices(), CloseIndexAction.INSTANCE ).
                    setIndices( indexName );

            try
            {
                this.client.admin().indices().close( closeIndexRequestBuilder.request() ).actionGet();
                LOG.info( "Closed index " + indexName );
            }
            catch ( IndexNotFoundException e )
            {
                LOG.warn( "Could not close index [" + indexName + "], not found" );
            }
        }
    }

    void openIndices( final Set<String> indexNames )
    {
        for ( final String indexName : indexNames )
        {
            OpenIndexRequestBuilder openIndexRequestBuilder =
                new OpenIndexRequestBuilder( this.client.admin().indices(), OpenIndexAction.INSTANCE ).
                    setIndices( indexName );

            try
            {
                this.client.admin().indices().open( openIndexRequestBuilder.request() ).actionGet();
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
        final Set<String> indices = Sets.newHashSet();

        indices.add( IndexNameResolver.resolveStorageIndexName( repositoryId ) );
        indices.add( IndexNameResolver.resolveSearchIndexName( repositoryId ) );

        return indices;
    }


    public static class Builder<B extends Builder>
    {
        private String snapshotRepositoryName;

        private Client client;

        private RepositoryService repositoryService;

        @SuppressWarnings("unchecked")
        public B snapshotRepositoryName( final String val )
        {
            snapshotRepositoryName = val;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B client( final Client val )
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
