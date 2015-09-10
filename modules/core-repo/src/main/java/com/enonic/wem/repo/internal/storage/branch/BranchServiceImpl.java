package com.enonic.wem.repo.internal.storage.branch;

import java.util.Collection;
import java.util.Set;

import org.elasticsearch.common.Strings;
import org.elasticsearch.index.query.QueryBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.branch.BranchDocumentId;
import com.enonic.wem.repo.internal.branch.BranchService;
import com.enonic.wem.repo.internal.branch.StoreBranchDocument;
import com.enonic.wem.repo.internal.cache.BranchPath;
import com.enonic.wem.repo.internal.cache.CachePath;
import com.enonic.wem.repo.internal.cache.PathCache;
import com.enonic.wem.repo.internal.cache.PathCacheImpl;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchDao;
import com.enonic.wem.repo.internal.elasticsearch.query.ElasticsearchQuery;
import com.enonic.wem.repo.internal.elasticsearch.query.builder.QueryBuilderFactory;
import com.enonic.wem.repo.internal.repository.IndexNameResolver;
import com.enonic.wem.repo.internal.storage.GetByIdRequest;
import com.enonic.wem.repo.internal.storage.GetByValuesRequest;
import com.enonic.wem.repo.internal.storage.ReturnFields;
import com.enonic.wem.repo.internal.storage.StaticStorageType;
import com.enonic.wem.repo.internal.storage.StorageCache;
import com.enonic.wem.repo.internal.storage.StorageCacheProvider;
import com.enonic.wem.repo.internal.storage.StorageDao;
import com.enonic.wem.repo.internal.storage.StorageSettings;
import com.enonic.wem.repo.internal.storage.StoreRequest;
import com.enonic.wem.repo.internal.storage.StoreStorageName;
import com.enonic.wem.repo.internal.storage.result.GetResult;
import com.enonic.wem.repo.internal.storage.result.SearchHit;
import com.enonic.wem.repo.internal.storage.result.SearchResult;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.query.filter.ValueFilter;

@Component
public class BranchServiceImpl
    implements BranchService
{
    private ElasticsearchDao elasticsearchDao;

    private StorageDao storageDao;

    protected StorageCache cache = StorageCacheProvider.provide();

    private final PathCache pathCache = new PathCacheImpl();

    private static final Logger LOG = LoggerFactory.getLogger( BranchServiceImpl.class );

    @Override
    public String store( final StoreBranchDocument storeBranchDocument, final InternalContext context )
    {
        final StoreRequest storeRequest = BranchStorageRequestFactory.create( storeBranchDocument, context );
        final String id = this.storageDao.store( storeRequest );

        pathCache.cache( createPath( storeBranchDocument.getNode().path(), context ), id );

        return id;
    }

    @Override
    public void delete( final NodeId nodeId, final InternalContext context )
    {
        storageDao.delete( BranchDeleteRequestFactory.create( nodeId, context ) );
        pathCache.evict( new BranchDocumentId( nodeId, context.getBranch() ).toString() );
    }

    @Override
    public NodeBranchVersion get( final NodeId nodeId, final InternalContext context )
    {
        return doGetById( nodeId, context );
    }

    private NodeBranchVersion doGetById( final NodeId nodeId, final InternalContext context )
    {
        final GetByIdRequest getByIdRequest = createGetByIdRequest( nodeId, context );
        final GetResult getResult = this.storageDao.getById( getByIdRequest );

        if ( getResult.isEmpty() )
        {
            return null;
        }

        final NodeBranchVersion nodeBranchVersion = NodeBranchVersionFactory.create( getResult );

        pathCache.cache( new BranchPath( context.getBranch(), nodeBranchVersion.getNodePath() ), getResult.getId() );

        return nodeBranchVersion;
    }

    @Override
    public NodeBranchVersions get( final NodeIds nodeIds, final InternalContext context )
    {
        Set<NodeBranchVersion> nodeBranchVersions = Sets.newHashSet();

        for ( final NodeId nodeId : nodeIds )
        {
            final NodeBranchVersion branchVersion = doGetById( nodeId, context );

            if ( branchVersion != null )
            {
                nodeBranchVersions.add( branchVersion );
            }
        }

        return NodeBranchVersions.from( nodeBranchVersions );
    }

    @Override
    public NodeBranchVersion get( final NodePath nodePath, final InternalContext context )
    {
        return doGetByPath( nodePath, context );
    }

    @Override
    public NodeBranchVersions get( final NodePaths nodePaths, final InternalContext context )
    {
        Set<NodeBranchVersion> nodeBranchVersions = Sets.newHashSet();

        for ( final NodePath nodePath : nodePaths )
        {
            final NodeBranchVersion branchVersion = doGetByPath( nodePath, context );

            if ( branchVersion != null )
            {
                nodeBranchVersions.add( branchVersion );
            }
        }

        return NodeBranchVersions.from( nodeBranchVersions );
    }

    private BranchPath createPath( final NodePath nodePath, final InternalContext context )
    {
        return new BranchPath( context.getBranch(), nodePath );
    }

    private NodeBranchVersion doGetByPath( final NodePath nodePath, final InternalContext context )
    {
        final String id = this.pathCache.get( new BranchPath( context.getBranch(), nodePath ) );

        if ( id != null )
        {
            final NodeId nodeId = createNodeId( id );
            return doGetById( nodeId, context );
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

            doCacheResult( context, getResult );

            return NodeBranchVersionFactory.create( getResult );
        }

        return null;
    }

    private NodeId createNodeId( final String id )
    {
        final int branchSeparator = id.lastIndexOf( "_" );
        return NodeId.from( Strings.substring( id, 0, branchSeparator ) );
    }

    private void doCacheResult( final InternalContext context, final GetResult getResult )
    {
        final NodeBranchVersion nodeBranchVersion = NodeBranchVersionFactory.create( getResult );

        pathCache.cache( new BranchPath( context.getBranch(), nodeBranchVersion.getNodePath() ), getResult.getId() );

    }

    private GetResult createGetResult( final SearchHit searchHit )
    {
        return GetResult.create().
            id( searchHit.getId() ).
            resultFieldValues( searchHit.getReturnValues() ).
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

    // TODO: If not in cache
    @Override
    public boolean hasChildren( final NodeId nodeId, final InternalContext context )
    {
        final CachePath cachePath = this.pathCache.get( new BranchDocumentId( nodeId, context.getBranch() ).toString() );

        final Collection<String> childrenIds = this.pathCache.getChildren( cachePath );

        return !childrenIds.isEmpty();
    }

    // TODO: If not in cache
    public NodeBranchVersions getChildren( final NodeId nodeId, final InternalContext context )
    {
        final CachePath cachePath = this.pathCache.get( new BranchDocumentId( nodeId, context.getBranch() ).toString() );

        final ImmutableSet<String> children = this.pathCache.getChildren( cachePath );

        final NodeBranchVersions.Builder builder = NodeBranchVersions.create();

        for ( final String id : children )
        {
            final NodeId childId = createNodeId( id );
            builder.add( doGetById( childId, context ) );
        }

        return builder.build();
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

