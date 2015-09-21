package com.enonic.wem.repo.internal.elasticsearch.version;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchDao;
import com.enonic.wem.repo.internal.storage.GetByIdRequest;
import com.enonic.wem.repo.internal.storage.ReturnFields;
import com.enonic.wem.repo.internal.storage.StaticStorageType;
import com.enonic.wem.repo.internal.storage.StorageDao;
import com.enonic.wem.repo.internal.storage.StorageSettings;
import com.enonic.wem.repo.internal.storage.StoreRequest;
import com.enonic.wem.repo.internal.storage.StoreStorageName;
import com.enonic.wem.repo.internal.storage.result.GetResult;
import com.enonic.wem.repo.internal.version.FindVersionsQuery;
import com.enonic.wem.repo.internal.version.NodeVersionDocument;
import com.enonic.wem.repo.internal.version.NodeVersionDocumentId;
import com.enonic.wem.repo.internal.version.VersionIndexPath;
import com.enonic.wem.repo.internal.version.VersionService;
import com.enonic.xp.node.FindNodeVersionsResult;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionDiffQuery;
import com.enonic.xp.node.NodeVersionDiffResult;

@Component
public class VersionServiceImpl
    implements VersionService
{
    public static final ReturnFields VERSION_RETURN_FIELDS =
        ReturnFields.from( VersionIndexPath.VERSION_ID, VersionIndexPath.TIMESTAMP, VersionIndexPath.NODE_PATH, VersionIndexPath.NODE_ID );

    private ElasticsearchDao elasticsearchDao;

    private StorageDao storageDao;

    @Override
    public void store( final NodeVersionDocument document, final InternalContext context )
    {
        final StoreRequest storeRequest = VersionStorageDocFactory.create( document, context.getRepositoryId() );

        this.storageDao.store( storeRequest );
    }

    @Override
    public NodeVersion getVersion( final NodeVersionDocumentId nodeVersionDocumentId, final InternalContext context )
    {
        final GetByIdRequest getByIdRequest = GetByIdRequest.create().
            id( nodeVersionDocumentId.toString() ).
            returnFields( VERSION_RETURN_FIELDS ).
            storageSettings( createStorageSettings( context ) ).
            build();

        final GetResult getResult = this.storageDao.getById( getByIdRequest );

        if ( getResult.isEmpty() )
        {
            return null;
        }

        return NodeVersionFactory.create( getResult );
    }

    @Override
    public FindNodeVersionsResult findVersions( final FindVersionsQuery query, final InternalContext context )
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

    private StorageSettings createStorageSettings( final InternalContext context )
    {
        return StorageSettings.create().
            storageName( StoreStorageName.from( context.getRepositoryId() ) ).
            storageType( StaticStorageType.VERSION ).
            build();
    }

    @Reference
    public void setElasticsearchDao( final ElasticsearchDao elasticsearchDao )
    {
        this.elasticsearchDao = elasticsearchDao;
    }

    @Reference
    public void setStorageDao( final StorageDao storageDao )
    {
        this.storageDao = storageDao;
    }
}
