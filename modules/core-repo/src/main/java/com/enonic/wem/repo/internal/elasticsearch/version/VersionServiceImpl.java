package com.enonic.wem.repo.internal.elasticsearch.version;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchDao;
import com.enonic.wem.repo.internal.storage.CacheHelper;
import com.enonic.wem.repo.internal.storage.CacheResult;
import com.enonic.wem.repo.internal.storage.CacheStoreRequest;
import com.enonic.wem.repo.internal.storage.GetByIdRequest;
import com.enonic.wem.repo.internal.storage.ReturnFields;
import com.enonic.wem.repo.internal.storage.StaticStorageType;
import com.enonic.wem.repo.internal.storage.StorageCache;
import com.enonic.wem.repo.internal.storage.StorageCacheProvider;
import com.enonic.wem.repo.internal.storage.StorageDao;
import com.enonic.wem.repo.internal.storage.StorageData;
import com.enonic.wem.repo.internal.storage.StorageSettings;
import com.enonic.wem.repo.internal.storage.StoreRequest;
import com.enonic.wem.repo.internal.storage.StoreStorageName;
import com.enonic.wem.repo.internal.storage.VersionPathCacheKey;
import com.enonic.wem.repo.internal.storage.result.GetResult;
import com.enonic.wem.repo.internal.version.GetVersionsQuery;
import com.enonic.wem.repo.internal.version.NodeVersionDocument;
import com.enonic.wem.repo.internal.version.VersionIndexPath;
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
    public static final ReturnFields VERSION_RETURN_FIELDS =
        ReturnFields.from( VersionIndexPath.VERSION_ID, VersionIndexPath.TIMESTAMP, VersionIndexPath.NODE_PATH, VersionIndexPath.NODE_ID );

    private ElasticsearchDao elasticsearchDao;

    private StorageDao storageDao;

    private StorageCache cache = StorageCacheProvider.provide();

    @Override
    public void store( final NodeVersionDocument document, final InternalContext context )
    {
        final StoreRequest storeRequest = VersionStorageDocFactory.create( document, context.getRepositoryId() );

        this.storageDao.store( storeRequest );

        cache.put( CacheStoreRequest.create().
            id( document.getNodeVersionId().toString() ).
            addCacheKey( new VersionPathCacheKey( document.getNodePath() ) ).
            storageData( storeRequest.getData() ).
            build() );
    }

    @Override
    public NodeVersion getVersion( final NodeVersionId nodeVersionId, final InternalContext context )
    {
        final CacheResult cacheResult = this.cache.get( nodeVersionId.toString() );

        if ( cacheResult.exists() )
        {
            final GetResult getResult = CacheHelper.createGetResult( cacheResult, VERSION_RETURN_FIELDS );

            return NodeVersionFactory.create( getResult );
        }

        final GetByIdRequest getByIdRequest = GetByIdRequest.create().
            id( nodeVersionId.toString() ).
            returnFields( VERSION_RETURN_FIELDS ).
            storageSettings( createStorageSettings( context ) ).
            build();

        final GetResult getResult = this.storageDao.getById( getByIdRequest );

        if ( getResult.isEmpty() )
        {
            return null;
        }

        final NodeVersion nodeVersion = NodeVersionFactory.create( getResult );

        cache.put( CacheStoreRequest.create().
            addCacheKey( new VersionPathCacheKey( nodeVersion.getNodePath() ) ).
            storageData( StorageData.create().
                add( VersionIndexPath.NODE_PATH.getPath(), nodeVersion.getNodePath().toString() ).
                add( VersionIndexPath.NODE_ID.getPath(), nodeVersion.getNodeId().toString() ).
                add( VersionIndexPath.VERSION_ID.getPath(), nodeVersion.getNodeVersionId().toString() ).
                add( VersionIndexPath.TIMESTAMP.getPath(), nodeVersion.getTimestamp().toString() ).
                build() ).
            build() );

        return nodeVersion;
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
