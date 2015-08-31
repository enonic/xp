package com.enonic.wem.repo.internal.elasticsearch.version;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchDao;
import com.enonic.wem.repo.internal.storage.StorageService;
import com.enonic.wem.repo.internal.version.GetVersionsQuery;
import com.enonic.wem.repo.internal.version.NodeVersionDocument;
import com.enonic.wem.repo.internal.version.VersionService;
import com.enonic.xp.node.FindNodeVersionsResult;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionDiffQuery;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.node.NodeVersionId;

@Component
public class VersionServiceImpl
    implements VersionService
{
    private ElasticsearchDao elasticsearchDao;

    private StorageService storageService;

    @Override
    public void store( final NodeVersionDocument nodeVersionDocument, final InternalContext context )
    {
        storageService.store( VersionStorageDocFactory.create( nodeVersionDocument, context.getRepositoryId() ), context );
    }

    @Override
    public NodeVersion getVersion( final NodeVersionId nodeVersionId, final InternalContext context )
    {
        return GetVersionCommand.create().
            elasticsearchDao( this.elasticsearchDao ).
            repositoryId( context.getRepositoryId() ).
            nodeVersionId( nodeVersionId ).
            build().
            execute();
    }

    @Override
    public FindNodeVersionsResult findVersions( final GetVersionsQuery query, final InternalContext context )
    {
        return FindVersionsCommand.create().
            elasticsearchDao( this.elasticsearchDao ).
            repositoryId( context.getRepositoryId() ).
            query( query ).
            build().
            execute();
    }

    @Override
    public NodeVersionDiffResult diff( final NodeVersionDiffQuery query, final InternalContext context )
    {
        return NodeVersionDiffCommand.create().
            elasticsearchDao( this.elasticsearchDao ).
            repositoryId( context.getRepositoryId() ).
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
    public void setStorageService( final StorageService storageService )
    {
        this.storageService = storageService;
    }
}
