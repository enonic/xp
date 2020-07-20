package com.enonic.xp.repo.impl.elasticsearch.snapshot;

import org.elasticsearch.client.Client;

import com.enonic.xp.repository.RepositoryIds;

class AbstractSnapshotExecutor
{
    final String snapshotRepositoryName;

    final String snapshotName;

    final Client client;

    final RepositoryIds repositories;

    AbstractSnapshotExecutor( final Builder builder )
    {
        snapshotRepositoryName = builder.snapshotRepositoryName;
        client = builder.client;
        repositories = builder.repositories;
        snapshotName = builder.snapshotName;
    }

    public static class Builder<B extends Builder>
    {
        private String snapshotRepositoryName;

        private Client client;

        private RepositoryIds repositories;

        private String snapshotName;

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
    }
}
