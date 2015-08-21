package com.enonic.wem.repo.internal.elasticsearch.version;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchDao;
import com.enonic.wem.repo.internal.elasticsearch.storage.ElasticsearchStorageDao;
import com.enonic.wem.repo.internal.storage.StorageDao;
import com.enonic.wem.repo.internal.version.GetVersionsQuery;
import com.enonic.wem.repo.internal.version.NodeVersionDocument;
import com.enonic.wem.repo.internal.version.VersionService;
import com.enonic.xp.node.FindNodeVersionsResult;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionDiffQuery;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repository.RepositoryId;

@Component
public class VersionServiceImpl
    implements VersionService
{
    private static final boolean DEFAULT_REFRESH = true;

    private ElasticsearchDao elasticsearchDao;

    private StorageDao storageDao;

    @Override
    public void store( final NodeVersionDocument nodeVersionDocument, final RepositoryId repositoryId )
    {
        storageDao.store( VersionStorageDocFactory.create( nodeVersionDocument, repositoryId ) );
    }

    @Override
    public NodeVersion getVersion( final NodeVersionId nodeVersionId, final RepositoryId repositoryId )
    {
        return GetVersionCommand.create().
            elasticsearchDao( this.elasticsearchDao ).
            repositoryId( repositoryId ).
            nodeVersionId( nodeVersionId ).
            build().
            execute();
    }

    @Override
    public FindNodeVersionsResult findVersions( final GetVersionsQuery query, final RepositoryId repositoryId )
    {
        return FindVersionsCommand.create().
            elasticsearchDao( this.elasticsearchDao ).
            repositoryId( repositoryId ).
            query( query ).
            build().
            execute();
    }

    @Override
    public NodeVersionDiffResult diff( final NodeVersionDiffQuery query, final RepositoryId repositoryId )
    {
        return NodeVersionDiffCommand.create().
            elasticsearchDao( this.elasticsearchDao ).
            repositoryId( repositoryId ).
            query( query ).
            build().
            execute();
    }

    @Reference
    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }

    @Reference
    public void setStorageDao( final ElasticsearchStorageDao storageDao )
    {
        this.storageDao = storageDao;
    }
}
