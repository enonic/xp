package com.enonic.xp.repo.impl.branch.storage;

import java.util.List;
import java.util.Set;

import org.elasticsearch.common.Strings;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.StorageSettings;
import com.enonic.xp.repo.impl.branch.BranchDocumentId;
import com.enonic.xp.repo.impl.branch.BranchService;
import com.enonic.xp.repo.impl.branch.StoreBranchDocument;
import com.enonic.xp.repo.impl.cache.BranchPath;
import com.enonic.xp.repo.impl.cache.PathCache;
import com.enonic.xp.repo.impl.cache.PathCacheImpl;
import com.enonic.xp.repo.impl.search.result.SearchHit;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.storage.GetByIdRequest;
import com.enonic.xp.repo.impl.storage.GetByValuesRequest;
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
        ReturnFields.from( BranchIndexPath.NODE_ID, BranchIndexPath.VERSION_ID, BranchIndexPath.STATE, BranchIndexPath.PATH,
                           BranchIndexPath.TIMESTAMP );

    private StorageDao storageDao;

    private final PathCache pathCache = new PathCacheImpl();

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
    public BranchNodeVersion get( final NodeId nodeId, final InternalContext context )
    {
        return doGetById( nodeId, context );
    }

    private BranchNodeVersion doGetById( final NodeId nodeId, final InternalContext context )
    {
        final GetByIdRequest getByIdRequest = createGetByIdRequest( nodeId, context );
        final GetResult getResult = this.storageDao.getById( getByIdRequest );

        if ( getResult.isEmpty() )
        {
            return null;
        }

        final BranchNodeVersion branchNodeVersion = NodeBranchVersionFactory.create( getResult );

        pathCache.cache( new BranchPath( context.getBranch(), branchNodeVersion.getNodePath() ), getResult.getId() );

        return branchNodeVersion;
    }

    @Override
    public BranchNodeVersions get( final NodeIds nodeIds, final InternalContext context )
    {
        List<BranchNodeVersion> branchNodeVersions = Lists.newLinkedList();

        for ( final NodeId nodeId : nodeIds )
        {
            final BranchNodeVersion branchVersion = doGetById( nodeId, context );

            if ( branchVersion != null )
            {
                branchNodeVersions.add( branchVersion );
            }
        }

        return BranchNodeVersions.from( branchNodeVersions );
    }

    @Override
    public BranchNodeVersion get( final NodePath nodePath, final InternalContext context )
    {
        return doGetByPath( nodePath, context );
    }

    @Override
    public BranchNodeVersions get( final NodePaths nodePaths, final InternalContext context )
    {
        Set<BranchNodeVersion> branchNodeVersions = Sets.newHashSet();

        for ( final NodePath nodePath : nodePaths )
        {
            final BranchNodeVersion branchVersion = doGetByPath( nodePath, context );

            if ( branchVersion != null )
            {
                branchNodeVersions.add( branchVersion );
            }
        }

        return BranchNodeVersions.from( branchNodeVersions );
    }

    private BranchPath createPath( final NodePath nodePath, final InternalContext context )
    {
        return new BranchPath( context.getBranch(), nodePath );
    }

    private BranchNodeVersion doGetByPath( final NodePath nodePath, final InternalContext context )
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
            returnFields( BRANCH_RETURN_FIELDS ).
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
        final BranchNodeVersion branchNodeVersion = NodeBranchVersionFactory.create( getResult );

        pathCache.cache( new BranchPath( context.getBranch(), branchNodeVersion.getNodePath() ), getResult.getId() );
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
            returnFields( BRANCH_RETURN_FIELDS ).
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

    @Reference
    public void setStorageDao( final StorageDao storageDao )
    {
        this.storageDao = storageDao;
    }
}

