package com.enonic.wem.repo.internal.elasticsearch.version;

import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchDao;
import com.enonic.xp.repository.RepositoryId;

class AbstractVersionsCommand
{
    final ElasticsearchDao elasticsearchDao;

    final RepositoryId repositoryId;

    AbstractVersionsCommand( Builder builder )
    {
        this.elasticsearchDao = builder.elasticsearchDao;
        this.repositoryId = builder.repositoryId;
    }

    static class Builder<B extends Builder>
    {
        private ElasticsearchDao elasticsearchDao;

        private RepositoryId repositoryId;

        Builder()
        {
        }

        @SuppressWarnings("unchecked")
        B elasticsearchDao( ElasticsearchDao elasticsearchDao )
        {
            this.elasticsearchDao = elasticsearchDao;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        B repositoryId( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return (B) this;
        }
    }
}
