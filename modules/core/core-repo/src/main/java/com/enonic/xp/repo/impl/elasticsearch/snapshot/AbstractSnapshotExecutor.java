package com.enonic.xp.repo.impl.elasticsearch.snapshot;

import com.enonic.xp.elasticsearch.client.impl.EsClient;
import com.enonic.xp.repository.RepositoryIds;
import com.enonic.xp.repository.RepositoryService;

class AbstractSnapshotExecutor
{
    final String snapshotRepositoryName;

    final EsClient client;

    final String snapshotName;

    final RepositoryIds repositories;

    final RepositoryService repositoryService;

    AbstractSnapshotExecutor( final Builder builder )
    {
        snapshotRepositoryName = builder.snapshotRepositoryName;
        client = builder.client;
        repositories = builder.repositories;
        snapshotName = builder.snapshotName;
        repositoryService = builder.repositoryService;
    }

    public static class Builder<B extends Builder>
    {
        private String snapshotRepositoryName;

        private EsClient client;

        private RepositoryIds repositories;

        private String snapshotName;

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
        public B repositories( final RepositoryIds repositories )
        {
            this.repositories = repositories;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B snapshotName( final String snapshotName )
        {
            this.snapshotName = snapshotName;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B repositoryService( final RepositoryService repositoryService )
        {
            this.repositoryService = repositoryService;
            return (B) this;
        }

    }

}
