package com.enonic.xp.repo.impl.branch.storage;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.util.concurrent.Striped;

import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.query.filter.IdFilter;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.SingleRepoStorageSource;
import com.enonic.xp.repo.impl.StorageSource;
import com.enonic.xp.repo.impl.branch.BranchService;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQueryResultFactory;
import com.enonic.xp.repo.impl.cache.BranchCachePath;
import com.enonic.xp.repo.impl.cache.BranchPath;
import com.enonic.xp.repo.impl.search.SearchDao;
import com.enonic.xp.repo.impl.search.SearchRequest;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.storage.DeleteRequests;
import com.enonic.xp.repo.impl.storage.GetByIdRequest;
import com.enonic.xp.repo.impl.storage.GetResult;
import com.enonic.xp.repo.impl.storage.StaticStorageType;
import com.enonic.xp.repo.impl.storage.StorageDao;
import com.enonic.xp.repo.impl.storage.StoreStorageName;
import com.enonic.xp.repository.RepositoryId;

@Component
public class BranchServiceImpl
    implements BranchService
{
    private static final ReturnFields BRANCH_RETURN_FIELDS =
        ReturnFields.from( BranchIndexPath.NODE_ID, BranchIndexPath.VERSION_ID, BranchIndexPath.NODE_BLOB_KEY,
                           BranchIndexPath.INDEX_CONFIG_BLOB_KEY, BranchIndexPath.ACCESS_CONTROL_BLOB_KEY, BranchIndexPath.STATE,
                           BranchIndexPath.PATH, BranchIndexPath.TIMESTAMP, BranchIndexPath.REFERENCES );

    private static final int BATCHED_EXECUTOR_LIMIT = 1000;

    private static final Striped<Lock> PARENT_PATH_LOCKS = Striped.lazyWeakLock( 100 );

    private final BranchCachePath pathCache = new BranchCachePath();

    private StorageDao storageDao;

    private SearchDao searchDao;

    @Override
    public String store( final NodeBranchEntry nodeBranchEntry, final InternalContext context )
    {
        return store( nodeBranchEntry, null, context );
    }

    @Override
    public String store( final NodeBranchEntry nodeBranchEntry, final NodePath previousPath, final InternalContext context )
    {
        if ( previousPath != null && !previousPath.equals( nodeBranchEntry.getNodePath() ) )
        {
            this.pathCache.evict( createPath( previousPath, context ) );
        }

        if ( context.isSkipConstraints() )
        {
            return doStore( nodeBranchEntry, context );
        }
        else
        {
            final Lock lock = PARENT_PATH_LOCKS.get( nodeBranchEntry.getNodePath().getParentPath() );
            lock.lock();
            try
            {
                verifyNotExistingNodeWithOtherId( nodeBranchEntry, context );

                return doStore( nodeBranchEntry, context );
            }
            finally
            {
                lock.unlock();
            }
        }
    }

    private String doStore( final NodeBranchEntry nodeBranchEntry, final InternalContext context )
    {
        final String id = this.storageDao.store( BranchStorageRequestFactory.create( nodeBranchEntry, context ) );

        doCache( context.getRepositoryId(), nodeBranchEntry.getNodePath(), BranchDocumentId.from( id ) );

        return id;
    }

    private void verifyNotExistingNodeWithOtherId( final NodeBranchEntry nodeBranchEntry, final InternalContext context )
    {
        final BranchDocumentId branchDocumentId = this.pathCache.get( createPath( nodeBranchEntry.getNodePath(), context ) );

        if ( branchDocumentId != null &&
            !branchDocumentId.equals( BranchDocumentId.from( nodeBranchEntry.getNodeId(), context.getBranch() ) ) )
        {
            throw new NodeAlreadyExistAtPathException( nodeBranchEntry.getNodePath(), context.getRepositoryId(), context.getBranch() );
        }
    }

    @Override
    public void delete( final NodeId nodeId, final InternalContext context )
    {
        final NodeBranchEntry nodeBranchEntry = doGetById( nodeId, context );

        if ( nodeBranchEntry == null )
        {
            return;
        }

        storageDao.delete( BranchDeleteRequestFactory.create( nodeId, context ) );

        pathCache.evict( createPath( nodeBranchEntry.getNodePath(), context ) );
    }

    @Override
    public void delete( final NodeIds nodeIds, final InternalContext context )
    {
        final NodeBranchEntries nodeBranchEntries = getIgnoreOrder( nodeIds, context );

        nodeBranchEntries.forEach( entry -> pathCache.evict( createPath( entry.getNodePath(), context ) ) );

        storageDao.delete( DeleteRequests.create()
                               .forceRefresh( false )
                               .ids( nodeIds.stream()
                                         .map( nodeId -> BranchDocumentId.from( nodeId, context.getBranch() ).toString() )
                                         .collect( Collectors.toList() ) )
                               .settings( createStorageSettings( context.getRepositoryId() ) )
                               .build() );
    }

    @Override
    public NodeBranchEntry get( final NodeId nodeId, final InternalContext context )
    {
        return doGetById( nodeId, context );
    }

    private NodeBranchEntry doGetById( final NodeId nodeId, final InternalContext context )
    {
        final GetByIdRequest getByIdRequest = GetByIdRequest.create()
            .id( BranchDocumentId.from( nodeId, context.getBranch() ).toString() )
            .storageSettings( createStorageSettings( context.getRepositoryId() ) )
            .returnFields( BRANCH_RETURN_FIELDS )
            .routing( nodeId.toString() )
            .build();
        final GetResult getResult = this.storageDao.getById( getByIdRequest );

        if ( getResult.isEmpty() )
        {
            return null;
        }

        return NodeBranchVersionFactory.create( getResult.getReturnValues() );
    }

    @Override
    public NodeBranchEntries get( final NodeIds nodeIds, final InternalContext context )
    {
        return getIgnoreOrder( nodeIds, context );
    }

    @Override
    public NodeBranchEntry get( final NodePath nodePath, final InternalContext context )
    {
        return doGetByPath( nodePath, context );
    }

    @Override
    public NodeBranchEntries get( final NodePaths nodePaths, final InternalContext context )
    {
        final List<NodeBranchEntry> nodeBranchEntries = nodePaths.stream()
            .map( nodePath -> doGetByPath( nodePath, context ) )
            .filter( Objects::nonNull )
            .collect( Collectors.toList() );

        return NodeBranchEntries.from( nodeBranchEntries );
    }

    @Override
    public void cachePath( final NodeId nodeId, final NodePath nodePath, final InternalContext context )
    {
        doCache( context.getRepositoryId(), nodePath, BranchDocumentId.from( nodeId, context.getBranch() ) );
    }

    @Override
    public void evictPath( final NodePath nodePath, final InternalContext context )
    {
        pathCache.evict( new BranchPath( context.getRepositoryId(), context.getBranch(), nodePath ) );
    }

    @Override
    public void evictAllPaths()
    {
        pathCache.evictAll();
    }

    private BranchPath createPath( final NodePath nodePath, final InternalContext context )
    {
        return new BranchPath( context.getRepositoryId(), context.getBranch(), nodePath );
    }


    private NodeBranchEntry doGetByPath( final NodePath nodePath, final InternalContext context )
    {
        final BranchDocumentId branchDocumentId =
            this.pathCache.get( new BranchPath( context.getRepositoryId(), context.getBranch(), nodePath ) );

        if ( branchDocumentId != null )
        {
            final NodeBranchEntry nodeBranchEntry = doGetById( branchDocumentId.getNodeId(), context );

            if ( nodeBranchEntry == null )
            {
                throw new NodeNotFoundException( "Node with path [" + nodePath + "] found in path-cache but not in storage" );
            }

            return nodeBranchEntry;
        }

        final NodeBranchQuery query = NodeBranchQuery.create()
            .addQueryFilter( ValueFilter.create()
                                 .fieldName( BranchIndexPath.PATH.getPath() )
                                 .addValue( ValueFactory.newString( nodePath.toString() ) )
                                 .build() )
            .addQueryFilter( ValueFilter.create()
                                 .fieldName( BranchIndexPath.BRANCH_NAME.getPath() )
                                 .addValue( ValueFactory.newString( context.getBranch().getValue() ) )
                                 .build() )
            .size( 1 )
            .build();

        final SearchResult result = this.searchDao.search( SearchRequest.create()
                                                               .searchSource( SingleRepoStorageSource.create( context.getRepositoryId(),
                                                                                                              SingleRepoStorageSource.Type.BRANCH ) )
                                                               .returnFields( BRANCH_RETURN_FIELDS )
                                                               .query( query )
                                                               .searchPreference( context.getSearchPreference() )
                                                               .build() );

        if ( !result.isEmpty() )
        {
            final NodeBranchEntry nodeBranchEntry = NodeBranchVersionFactory.create( result.getHits().getFirst().getReturnValues() );
            doCache( context.getRepositoryId(), nodeBranchEntry.getNodePath(),
                     BranchDocumentId.from( nodeBranchEntry.getNodeId(), context.getBranch() ) );

            return nodeBranchEntry;
        }

        return null;
    }

    private void doCache( final RepositoryId repositoryId, final NodePath nodePath, final BranchDocumentId branchDocumentId )
    {
        pathCache.cache( new BranchPath( repositoryId, branchDocumentId.getBranch(), nodePath ), branchDocumentId );
    }

    private NodeBranchEntries getIgnoreOrder( final NodeIds nodeIds, final InternalContext context )
    {
        if ( nodeIds.isEmpty() )
        {
            return NodeBranchEntries.empty();
        }

        final NodeBranchQuery query = NodeBranchQuery.create()
            .addQueryFilter( ValueFilter.create()
                                 .fieldName( BranchIndexPath.BRANCH_NAME.getPath() )
                                 .addValue( ValueFactory.newString( context.getBranch().getValue() ) )
                                 .build() )
            .addQueryFilter( IdFilter.create().fieldName( BranchIndexPath.NODE_ID.getPath() ).values( nodeIds ).build() )
            .size( nodeIds.getSize() )
            .build();
        final SearchResult results = this.searchDao.search( SearchRequest.create()
                                                                .query( query )
                                                                .returnFields( BRANCH_RETURN_FIELDS )
                                                                .searchSource( SingleRepoStorageSource.create( context.getRepositoryId(),
                                                                                                            SingleRepoStorageSource.Type.BRANCH ) )
                                                                .searchPreference( context.getSearchPreference() )
                                                                .build() );

        return NodeBranchQueryResultFactory.create( results );
    }

    private StorageSource createStorageSettings( final RepositoryId repositoryId )
    {
        return StorageSource.create()
            .storageName( StoreStorageName.from( repositoryId ) )
            .storageType( StaticStorageType.BRANCH )
            .build();
    }

    @Reference
    public void setStorageDao( final StorageDao storageDao )
    {
        this.storageDao = storageDao;
    }

    @Reference
    public void setSearchDao( final SearchDao searchDao )
    {
        this.searchDao = searchDao;
    }
}

