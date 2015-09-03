package com.enonic.wem.repo.internal.storage.branch;

import java.util.Collection;

import org.elasticsearch.index.query.QueryBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.branch.BranchDocumentId;
import com.enonic.wem.repo.internal.branch.BranchService;
import com.enonic.wem.repo.internal.branch.StoreBranchDocument;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchDao;
import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.elasticsearch.query.builder.QueryBuilderFactory;
import com.enonic.wem.repo.internal.repository.IndexNameResolver;
import com.enonic.wem.repo.internal.storage.AnotherCache;
import com.enonic.wem.repo.internal.storage.BranchPathCacheKey;
import com.enonic.wem.repo.internal.storage.CacheResult;
import com.enonic.wem.repo.internal.storage.CacheStoreRequest;
import com.enonic.wem.repo.internal.storage.GetByIdRequest;
import com.enonic.wem.repo.internal.storage.GetByValuesRequest;
import com.enonic.wem.repo.internal.storage.ReturnField;
import com.enonic.wem.repo.internal.storage.ReturnFields;
import com.enonic.wem.repo.internal.storage.StaticStorageType;
import com.enonic.wem.repo.internal.storage.StorageDao;
import com.enonic.wem.repo.internal.storage.StorageData;
import com.enonic.wem.repo.internal.storage.StorageSettings;
import com.enonic.wem.repo.internal.storage.StoreRequest;
import com.enonic.wem.repo.internal.storage.StoreStorageName;
import com.enonic.wem.repo.internal.storage.result.GetResult;
import com.enonic.wem.repo.internal.storage.result.ReturnValues;
import com.enonic.wem.repo.internal.storage.result.SearchHit;
import com.enonic.wem.repo.internal.storage.result.SearchResult;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.query.filter.ValueFilter;

@Component
public class BranchServiceImpl
    implements BranchService
{
    private ElasticsearchDao elasticsearchDao;

    protected StorageDao storageDao;

    protected AnotherCache cache = new AnotherCache();

    @Override
    public String store( final StoreBranchDocument storeBranchDocument, final InternalContext context )
    {
        final StoreRequest storeRequest = BranchStorageRequestFactory.create( storeBranchDocument, context );
        final String id = this.storageDao.store( storeRequest );

        cache.store( CacheStoreRequest.create().
            id( id ).
            addCacheKey( new BranchPathCacheKey( context.getBranch(), storeBranchDocument.getNode().path() ) ).
            storageData( storeRequest.getData() ).
            build() );

        return id;
    }

    @Override
    public void delete( final NodeId nodeId, final InternalContext context )
    {
        storageDao.delete( BranchDeleteRequestFactory.create( nodeId, context ) );

        cache.delete( nodeId.toString() );
    }

    @Override
    public NodeBranchVersion get( final NodeId nodeId, final InternalContext context )
    {
        final CacheResult cacheResult = this.cache.get( nodeId.toString() );
        // final CacheResult cacheResult = CacheResult.empty();

        if ( cacheResult.exists() )
        {
            return NodeBranchVersionFactory.create( createGetResult( cacheResult ) );
        }

        final GetByIdRequest getByIdRequest = createGetByIdRequest( nodeId, context );
        final GetResult getResult = this.storageDao.getById( getByIdRequest );

        if ( getResult.isEmpty() )
        {
            return null;
        }

        final NodeBranchVersion nodeBranchVersion = NodeBranchVersionFactory.create( getResult );

        cache.store( CacheStoreRequest.create().
            id( getResult.getId() ).
            addCacheKey( new BranchPathCacheKey( context.getBranch(), nodeBranchVersion.getNodePath() ) ).
            storageData( createStorageData( nodeBranchVersion, nodeId, context ) ).
            build() );

        return nodeBranchVersion;
    }

    @Override
    public NodeBranchVersion get( final NodePath nodePath, final InternalContext context )
    {
        final CacheResult cacheResult = this.cache.get( new BranchPathCacheKey( context.getBranch(), nodePath ) );

        if ( cacheResult.exists() )
        {
            return NodeBranchVersionFactory.create( createGetResult( cacheResult ) );
        }

        final SearchResult result = this.storageDao.getByValues( GetByValuesRequest.create().
            storageSettings( createStorageSettings( context ) ).
            addValue( BranchIndexPath.BRANCH_NAME.getPath(), context.getBranch().getName() ).
            addValue( BranchIndexPath.PATH.getPath(), nodePath.toString() ).
            returnFields(
                ReturnFields.from( BranchIndexPath.NODE_ID, BranchIndexPath.VERSION_ID, BranchIndexPath.STATE, BranchIndexPath.PATH,
                                   BranchIndexPath.TIMESTAMP ) ).
            expectSingleValue( true ).
            build() );

        if ( !result.isEmpty() )
        {
            final SearchHit firstHit = result.getResults().getFirstHit();

            final GetResult getResult = createGetResult( firstHit );

            cacheResult( context, getResult );

            return NodeBranchVersionFactory.create( getResult );
        }

        return null;
    }

    private StorageData createStorageData( final NodeBranchVersion nodeBranchVersion, final NodeId nodeId, final InternalContext context )
    {
        return StorageData.create().
            add( BranchIndexPath.NODE_ID.getPath(), nodeId.toString() ).
            add( BranchIndexPath.PATH.getPath(), nodeBranchVersion.getNodePath().toString() ).
            add( BranchIndexPath.VERSION_ID.getPath(), nodeBranchVersion.getVersionId() ).
            add( BranchIndexPath.STATE.getPath(), nodeBranchVersion.getNodeState().value() ).
            add( BranchIndexPath.TIMESTAMP.getPath(), nodeBranchVersion.getTimestamp() ).
            add( BranchIndexPath.BRANCH_NAME.getPath(), context.getBranch().getName() ).
            build();
    }

    private void cacheResult( final InternalContext context, final GetResult getResult )
    {
        final NodeBranchVersion nodeBranchVersion = NodeBranchVersionFactory.create( getResult );

        cache.store( CacheStoreRequest.create().
            id( getResult.getId() ).
            addCacheKey( new BranchPathCacheKey( context.getBranch(), nodeBranchVersion.getNodePath() ) ).
            storageData( createStorageData( nodeBranchVersion, NodeId.from( getResult.getId() ), context ) ).
            build() );
    }

    private GetResult createGetResult( final SearchHit searchHit )
    {
        return GetResult.create().
            id( searchHit.getId() ).
            resultFieldValues( searchHit.getReturnValues() ).
            build();
    }

    private GetResult createGetResult( final CacheResult cacheResult )
    {
        if ( !cacheResult.exists() )
        {
            return GetResult.empty();
        }

        final ReturnFields returnFields =
            ReturnFields.from( BranchIndexPath.NODE_ID, BranchIndexPath.VERSION_ID, BranchIndexPath.STATE, BranchIndexPath.PATH,
                               BranchIndexPath.TIMESTAMP );

        final ReturnValues.Builder builder = ReturnValues.create();

        for ( final ReturnField field : returnFields )
        {
            final StorageData data = cacheResult.getStorageData();

            final Collection<Object> values = data.get( field.getPath() );

            if ( values == null || values.isEmpty() )
            {
                throw new RuntimeException( "Expected data with path '" + field.getPath() + " in storage" );
            }

            builder.add( field.getPath(), values ).build();
        }

        return GetResult.create().
            id( cacheResult.getId() ).
            resultFieldValues( builder.build() ).
            build();

    }

    private GetByIdRequest createGetByIdRequest( final NodeId nodeId, final InternalContext context )
    {
        return GetByIdRequest.create().
            id( new BranchDocumentId( nodeId, context.getBranch() ).toString() ).
            storageSettings( createStorageSettings( context ) ).
            returnFields(
                ReturnFields.from( BranchIndexPath.VERSION_ID, BranchIndexPath.STATE, BranchIndexPath.PATH, BranchIndexPath.TIMESTAMP ) ).
            routing( nodeId.toString() ).
            build();
    }

    private StorageSettings createStorageSettings( final InternalContext context )
    {
        return StorageSettings.create().
            storageName( StoreStorageName.from( context.getRepositoryId() ) ).
            storageType( StaticStorageType.BRANCH ).
            build();
    }


    // TODO: Move to search service
    @Override
    public NodeBranchQueryResult findAll( final NodeBranchQuery nodeBranchQuery, final InternalContext context )
    {
        final QueryBuilder queryBuilder = QueryBuilderFactory.create().
            addQueryFilter( ValueFilter.create().
                fieldName( BranchIndexPath.BRANCH_NAME.getPath() ).
                addValue( ValueFactory.newString( context.getBranch().getName() ) ).
                build() ).
            build();

        final ElasticsearchQuery query = ElasticsearchQuery.create().
            index( IndexNameResolver.resolveStorageIndexName( context.getRepositoryId() ) ).
            indexType( IndexType.BRANCH.getName() ).
            query( queryBuilder ).
            size( nodeBranchQuery.getSize() ).
            from( nodeBranchQuery.getFrom() ).
            setReturnFields( ReturnFields.from( BranchIndexPath.NODE_ID, BranchIndexPath.VERSION_ID ) ).
            build();

        final SearchResult searchResult = this.elasticsearchDao.find( query );

        if ( searchResult.isEmpty() )
        {
            return NodeBranchQueryResult.empty();
        }

        return NodeBranchQueryResultFactory.create( searchResult );
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

