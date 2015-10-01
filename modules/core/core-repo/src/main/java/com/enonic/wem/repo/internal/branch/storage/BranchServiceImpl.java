package com.enonic.wem.repo.internal.branch.storage;

import java.util.List;
import java.util.Set;

import org.elasticsearch.common.Strings;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.ReturnFields;
import com.enonic.wem.repo.internal.StorageSettings;
import com.enonic.wem.repo.internal.branch.BranchDocumentId;
import com.enonic.wem.repo.internal.branch.BranchService;
import com.enonic.wem.repo.internal.branch.MoveBranchDocument;
import com.enonic.wem.repo.internal.branch.StoreBranchDocument;
import com.enonic.wem.repo.internal.cache.BranchPath;
import com.enonic.wem.repo.internal.cache.PathCache;
import com.enonic.wem.repo.internal.cache.PathCacheImpl;
import com.enonic.wem.repo.internal.search.result.SearchHit;
import com.enonic.wem.repo.internal.search.result.SearchResult;
import com.enonic.wem.repo.internal.storage.GetByIdRequest;
import com.enonic.wem.repo.internal.storage.GetByValuesRequest;
import com.enonic.wem.repo.internal.storage.GetResult;
import com.enonic.wem.repo.internal.storage.StaticStorageType;
import com.enonic.wem.repo.internal.storage.StorageDao;
import com.enonic.wem.repo.internal.storage.StoreRequest;
import com.enonic.wem.repo.internal.storage.StoreStorageName;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;

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
        return doStore( storeBranchDocument, context );
    }

    private String doStore( final StoreBranchDocument storeBranchDocument, final InternalContext context )
    {
        final StoreRequest storeRequest = BranchStorageRequestFactory.create( storeBranchDocument, context );
        final String id = this.storageDao.store( storeRequest );

        pathCache.cache( createPath( storeBranchDocument.getNode().path(), context ), id );

        return id;
    }

    @Override
    public String move( final MoveBranchDocument moveBranchDocument, final InternalContext context )
    {
        this.pathCache.evict( createPath( moveBranchDocument.getPreviousPath(), context ) );

        return doStore( StoreBranchDocument.create().
            node( moveBranchDocument.getNode() ).
            nodeVersionId( moveBranchDocument.getNodeVersionId() ).
            build(), context );
    }

    @Override
    public void delete( final NodeId nodeId, final InternalContext context )
    {
        final BranchNodeVersion branchNodeVersion = doGetById( nodeId, context );

        if ( branchNodeVersion == null )
        {
            return;
        }

        storageDao.delete( BranchDeleteRequestFactory.create( nodeId, context ) );

        pathCache.evict( createPath( branchNodeVersion.getNodePath(), context ) );
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

