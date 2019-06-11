package com.enonic.xp.repo.impl.branch.storage;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.util.concurrent.Striped;

import com.enonic.xp.context.InternalContext;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.query.filter.IdFilter;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.SingleRepoStorageSource;
import com.enonic.xp.repo.impl.StorageSource;
import com.enonic.xp.repo.impl.branch.BranchService;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQueryResult;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQueryResultFactory;
import com.enonic.xp.repo.impl.cache.BranchCachePath;
import com.enonic.xp.repo.impl.cache.BranchPath;
import com.enonic.xp.repo.impl.search.SearchDao;
import com.enonic.xp.repo.impl.search.SearchRequest;
import com.enonic.xp.repo.impl.search.result.SearchHit;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.storage.DeleteRequests;
import com.enonic.xp.repo.impl.storage.GetByIdRequest;
import com.enonic.xp.repo.impl.storage.GetResult;
import com.enonic.xp.repo.impl.storage.StaticStorageType;
import com.enonic.xp.repo.impl.storage.StorageDao;
import com.enonic.xp.repo.impl.storage.StoreRequest;
import com.enonic.xp.repo.impl.storage.StoreStorageName;

@Component
public class BranchServiceImpl
    implements BranchService
{
    private static final ReturnFields BRANCH_RETURN_FIELDS =
        ReturnFields.from( BranchIndexPath.NODE_ID, BranchIndexPath.VERSION_ID, BranchIndexPath.NODE_BLOB_KEY,
                           BranchIndexPath.INDEX_CONFIG_BLOB_KEY, BranchIndexPath.ACCESS_CONTROL_BLOB_KEY, BranchIndexPath.STATE,
                           BranchIndexPath.PATH, BranchIndexPath.TIMESTAMP, BranchIndexPath.REFERENCES );

    private static final int BATCHED_EXECUTOR_LIMIT = 1000;

    private final static Striped<Lock> PARENT_PATH_LOCKS = Striped.lazyWeakLock( 100 );

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
            return doStore( nodeBranchEntry, context, false );
        }
        else
        {
            final NodePath parentPath = nodeBranchEntry.getNodePath().getParentPath();
            return synchronizeByPath( parentPath, () -> doStore( nodeBranchEntry, context, true ) );
        }
    }

    private String doStore( final NodeBranchEntry nodeBranchEntry, final InternalContext context, final boolean validate )
    {
        if ( validate )
        {
            verifyNotExistingNodeWithOtherId( nodeBranchEntry, context );
        }

        final StoreRequest storeRequest = BranchStorageRequestFactory.create( nodeBranchEntry, context );

        final String id = this.storageDao.store( storeRequest );

        doCache( context, nodeBranchEntry.getNodePath(), BranchDocumentId.from( id ) );

        return id;
    }

    private <T> T synchronizeByPath( final NodePath path, final Supplier<T> callback )
    {
        final Lock lock = PARENT_PATH_LOCKS.get( path );
        try
        {
            lock.lock();
            return callback.get();
        }
        finally
        {
            lock.unlock();
        }
    }

    private void verifyNotExistingNodeWithOtherId( final NodeBranchEntry nodeBranchEntry, final InternalContext context )
    {
        final BranchDocumentId branchDocumentId = this.pathCache.get( createPath( nodeBranchEntry.getNodePath(), context ) );

        if ( branchDocumentId != null &&
            !branchDocumentId.equals( BranchDocumentId.from( nodeBranchEntry.getNodeId(), context.getBranch() ) ) )
        {
            throw new NodeAlreadyExistAtPathException( nodeBranchEntry.getNodePath() );
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

        storageDao.delete( DeleteRequests.create().
            forceRefresh( false ).
            ids( nodeIds.stream().
                map( nodeId -> new BranchDocumentId( nodeId, context.getBranch() ).toString() ).
                collect( Collectors.toList() ) ).
            settings( createStorageSettings( context ) ).
            build() );
    }

    @Override
    public NodeBranchEntry get( final NodeId nodeId, final InternalContext context )
    {
        return doGetById( nodeId, context );
    }

    private NodeBranchEntry doGetById( final NodeId nodeId, final InternalContext context )
    {
        final GetByIdRequest getByIdRequest = createGetByIdRequest( nodeId, context );
        final GetResult getResult = this.storageDao.getById( getByIdRequest );

        if ( getResult.isEmpty() )
        {
            return null;
        }

        return NodeBranchVersionFactory.create( getResult.getReturnValues() );
    }

    @Override
    public NodeBranchEntries get( final NodeIds nodeIds, final boolean keepOrder, final InternalContext context )
    {
        if ( keepOrder )
        {
            return getKeepOrder( nodeIds, context );
        }

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
        final List<NodeBranchEntry> nodeBranchEntries = nodePaths.stream().
            map( nodePath -> doGetByPath( nodePath, context ) ).
            filter( branchVersion -> branchVersion != null ).collect( Collectors.toList() );

        return NodeBranchEntries.from( nodeBranchEntries );
    }

    @Override
    public void cachePath( final NodeId nodeId, final NodePath nodePath, final InternalContext context )
    {
        doCache( context, nodePath, nodeId );
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
            return getFromCache( nodePath, context, branchDocumentId );
        }

        final NodeBranchQuery query = NodeBranchQuery.create().
            addQueryFilter( ValueFilter.create().
                fieldName( NodeIndexPath.PATH.getPath() ).
                addValue( ValueFactory.newString( nodePath.toString() ) ).build() ).
            size( 1 ).
            build();

        final SearchResult result = this.searchDao.search( SearchRequest.create().
            searchSource( SingleRepoSearchSource.from( context ) ).
            query( query ).
            build() );

        if ( !result.isEmpty() )
        {
            final SearchHit firstHit = result.getHits().getFirst();

            final NodeId nodeId = NodeId.from( firstHit.getId() );
            final NodeBranchEntry nodeBranchEntry = doGetById( nodeId, context );

            if ( nodeBranchEntry == null )
            {
                return null;
            }

            doCache( context, nodeBranchEntry.getNodePath(), nodeId );

            return nodeBranchEntry;
        }

        return null;
    }

    private NodeBranchEntry getFromCache( final NodePath nodePath, final InternalContext context, final BranchDocumentId branchDocumentId )
    {
        final NodeBranchEntry nodeBranchEntry = doGetById( branchDocumentId.getNodeId(), context );

        if ( nodeBranchEntry == null )
        {
            throw new NodeNotFoundException( "Node with path [" + nodePath + "] found in path-cache but not in storage" );
        }

        return nodeBranchEntry;
    }

    private void doCache( final InternalContext context, final NodePath nodePath, final NodeId nodeId )
    {
        doCache( context, nodePath, new BranchDocumentId( nodeId, context.getBranch() ) );
    }

    private void doCache( final InternalContext context, final NodePath nodePath, final BranchDocumentId branchDocumentId )
    {
        pathCache.cache( new BranchPath( context.getRepositoryId(), context.getBranch(), nodePath ), branchDocumentId );
    }

    private NodeBranchEntries getKeepOrder( final NodeIds nodeIds, final InternalContext context )
    {
        final NodeBranchEntries.Builder builder = NodeBranchEntries.create();

        final GetBranchEntriesMethod getBranchEntriesMethod = GetBranchEntriesMethod.create().
            context( context ).
            pathCache( this.pathCache ).
            returnFields( BRANCH_RETURN_FIELDS ).
            storageDao( this.storageDao ).
            build();

        if ( nodeIds.getSize() > BATCHED_EXECUTOR_LIMIT )
        {
            builder.addAll( BatchedBranchEntryExecutor.create().
                nodeIds( nodeIds ).
                method( getBranchEntriesMethod ).
                build().
                execute() );
        }
        else
        {
            getBranchEntriesMethod.execute( nodeIds.getSet(), builder );
        }

        return builder.build();
    }

    private NodeBranchEntries getIgnoreOrder( final NodeIds nodeIds, final InternalContext context )
    {
        if ( nodeIds.isEmpty() )
        {
            return NodeBranchEntries.empty();
        }

        final SearchResult results = this.searchDao.search( SearchRequest.create().
            query( NodeBranchQuery.create().
                addQueryFilter( ValueFilter.create().
                    fieldName( BranchIndexPath.BRANCH_NAME.getPath() ).
                    addValue( ValueFactory.newString( context.getBranch().getValue() ) ).
                    build() ).
                addQueryFilter( IdFilter.create().
                    fieldName( BranchIndexPath.NODE_ID.getPath() ).
                    values( nodeIds ).
                    build() ).
                size( nodeIds.getSize() ).
                build() ).
            returnFields( BRANCH_RETURN_FIELDS ).
            searchSource( new SingleRepoStorageSource( context.getRepositoryId(), SingleRepoStorageSource.Type.BRANCH ) ).
            build() );

        final NodeBranchQueryResult nodeBranchEntries = NodeBranchQueryResultFactory.create( results );
        return NodeBranchEntries.from( nodeBranchEntries.getList() );
    }

    private GetByIdRequest createGetByIdRequest( final NodeId nodeId, final InternalContext context )
    {
        return GetByIdRequest.create().
            id( new BranchDocumentId( nodeId, context.getBranch() ).toString() ).
            storageSettings( createStorageSettings( context ) ).
            returnFields( BRANCH_RETURN_FIELDS ).
            routing( nodeId.toString() ).
            build();
    }

    private StorageSource createStorageSettings( final InternalContext context )
    {
        return StorageSource.create().
            storageName( StoreStorageName.from( context.getRepositoryId() ) ).
            storageType( StaticStorageType.BRANCH ).
            build();
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

