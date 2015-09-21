package com.enonic.wem.repo.internal.elasticsearch.version;

import java.time.Instant;

import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchDao;
import com.enonic.wem.repo.internal.storage.result.ReturnValue;
import com.enonic.wem.repo.internal.storage.result.SearchHit;
import com.enonic.wem.repo.internal.version.VersionIndexPath;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
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

    NodeVersion createVersionEntry( final SearchHit hit )
    {
        final String timestamp = getStringValue( hit, VersionIndexPath.TIMESTAMP, true );
        final String versionId = getStringValue( hit, VersionIndexPath.VERSION_ID, true );

        return NodeVersion.create().
            nodeVersionId( NodeVersionId.from( versionId ) ).
            timestamp( Instant.parse( timestamp ) ).
            build();
    }

    private String getStringValue( final SearchHit hit, final IndexPath indexPath, final boolean required )
    {
        final ReturnValue field = hit.getField( indexPath.getPath(), required );

        if ( field == null )
        {
            return null;
        }

        return field.getSingleValue().toString();
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
