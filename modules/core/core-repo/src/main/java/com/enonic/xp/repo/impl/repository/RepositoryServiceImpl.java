package com.enonic.xp.repo.impl.repository;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.BranchInfo;
import com.enonic.xp.branch.BranchInfos;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.IdFilter;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.node.DeleteNodeByIdCommand;
import com.enonic.xp.repo.impl.node.PushNodesCommand;
import com.enonic.xp.repo.impl.node.RefreshCommand;
import com.enonic.xp.repo.impl.repository.event.RepositoryEventListener;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repository.BranchNotFoundException;
import com.enonic.xp.repository.ChildBranchFoundException;
import com.enonic.xp.repository.CreateBranchParams;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.DeleteBranchParams;
import com.enonic.xp.repository.DeleteRepositoryParams;
import com.enonic.xp.repository.NodeRepositoryService;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryNotFoundException;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Component(immediate = true)
public class RepositoryServiceImpl
    implements RepositoryService, EventListener
{
    private static final Logger LOG = LoggerFactory.getLogger( RepositoryServiceImpl.class );

    private static final int BATCH_SIZE = 5_000;

    private final ConcurrentMap<RepositoryId, Repository> repositoryMap = Maps.newConcurrentMap();

    private RepositoryEntryService repositoryEntryService;

    private IndexServiceInternal indexServiceInternal;

    private NodeRepositoryService nodeRepositoryService;

    private NodeStorageService nodeStorageService;

    private NodeSearchService nodeSearchService;

    private RepositoryEventListener repositoryEventListener;

    @SuppressWarnings("unused")
    @Activate
    public void initialize()
    {
        this.repositoryEventListener = RepositoryEventListener.create().
            repositoryService( this ).
            storageService( nodeStorageService ).
            build();

        SystemRepoInitializer.create().
            setIndexServiceInternal( indexServiceInternal ).
            setRepositoryService( this ).
            setNodeStorageService( nodeStorageService ).
            build().
            initialize();

    }

    @Override
    public Repository createRepository( final CreateRepositoryParams params )
    {
        requireAdminRole();

        return repositoryMap.compute( params.getRepositoryId(),
                                      ( repositoryId, previousRepository ) -> doCreateRepo( params, previousRepository ) );
    }

    private Repository doCreateRepo( final CreateRepositoryParams params, final Repository previousRepository )
    {
        //If the repository entry already exists, throws an exception
        final RepositoryId repositoryId = params.getRepositoryId();
        final boolean repoAlreadyInitialized = this.nodeRepositoryService.isInitialized( repositoryId );

        if ( previousRepository != null && repoAlreadyInitialized )
        {
            throw new RepositoryAlreadyExistException( repositoryId );
        }

        //If the repository does not exist, creates it
        if ( !this.nodeRepositoryService.isInitialized( repositoryId ) )
        {
            this.nodeRepositoryService.create( params );
        }

        //If the root node does not exist, creates it
        if ( getRootNode( params.getRepositoryId(), RepositoryConstants.MASTER_BRANCH ) == null )
        {
            createRootNode( params );
        }

        //Creates the repository entry
        final Repository repository = createRepositoryObject( params );
        repositoryEntryService.createRepositoryEntry( repository );
        return repository;
    }

    @Override
    public Branch createBranch( final CreateBranchParams createBranchParams )
    {
        requireAdminRole();
        final RepositoryId repositoryId = ContextAccessor.current().
            getRepositoryId();

        repositoryMap.compute( repositoryId,
                               ( key, previousRepository ) -> doCreateBranch( createBranchParams, repositoryId, previousRepository ) );

        return createBranchParams.getBranchInfo().getBranch();
    }

    private Repository doCreateBranch( final CreateBranchParams createBranchParams, final RepositoryId repositoryId,
                                       Repository previousRepository )
    {
        //If the repository entry does not exist, throws an exception
        previousRepository = previousRepository == null ? repositoryEntryService.getRepositoryEntry( repositoryId ) : previousRepository;
        if ( previousRepository == null )
        {
            throw new RepositoryNotFoundException( repositoryId );
        }

        //If the branch already exists, throws an exception
        final Branch newBranch = createBranchParams.getBranch();
        if ( previousRepository.getBranches().contains( newBranch ) )
        {
            throw new BranchAlreadyExistException( newBranch );
        }

        //If the root node does not exist, creates it
        if ( getRootNode( previousRepository.getId(), newBranch ) == null )
        {
            pushRootNode( previousRepository, newBranch );
        }

        //If the branch is a child branch, push all nodes
        final Branch parentBranch = createBranchParams.getBranchInfo().getParentBranch();
        if ( parentBranch != null )
        {
            pushAllNodes( previousRepository, parentBranch, newBranch );
        }

        //Updates the repository entry
        final BranchInfo newBranchInfo = BranchInfo.from( newBranch, parentBranch );
        return repositoryEntryService.addBranchToRepositoryEntry( repositoryId, newBranchInfo );
    }

    private void pushAllNodes( final Repository repository, final Branch parentBranch, final Branch newBranch )
    {
        final Context context = ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( repository.getId() ).
            branch( parentBranch ).
            build();
        final InternalContext internalContext = InternalContext.from( context );
        final BooleanFilter filter = BooleanFilter.create().
            mustNot( IdFilter.create().value( Node.ROOT_UUID.toString() ).build() ).
            build();

        int from = 0;
        SearchResult searchResult;
        do
        {
            final NodeQuery nodeQuery = NodeQuery.create().
                addQueryFilter( filter ).
                from( from ).
                size( BATCH_SIZE ).
                build();
            searchResult = nodeSearchService.query( nodeQuery, SingleRepoSearchSource.from( internalContext ) );

            if ( !searchResult.isEmpty() )
            {
                final NodeIds nodeIds = NodeIds.from( searchResult.getIds() );
                context.callWith( () -> PushNodesCommand.create().
                    indexServiceInternal( indexServiceInternal ).
                    searchService( nodeSearchService ).
                    storageService( nodeStorageService ).
                    ids( nodeIds ).
                    target( newBranch ).
                    build().
                    execute() );
                //TODO Handle failures
            }
            from += BATCH_SIZE;
        }
        while ( !searchResult.isEmpty() );
    }

    @Override
    public Repositories list()
    {
        requireAdminRole();
        final ImmutableList.Builder<Repository> repositories = ImmutableList.builder();
        repositoryEntryService.findRepositoryEntryIds().stream().
            map( repositoryId -> repositoryEntryService.getRepositoryEntry( repositoryId ) ).
            filter( Objects::nonNull ).
            forEach( repositories::add );
        return Repositories.from( repositories.build() );
    }

    @Override
    public boolean isInitialized( final RepositoryId repositoryId )
    {
        requireAdminRole();
        final Repository repository = this.get( repositoryId );
        return repository != null && this.nodeRepositoryService.isInitialized( repositoryId );
    }

    @Override
    public Repository get( final RepositoryId repositoryId )
    {
        requireAdminRole();
        return repositoryMap.computeIfAbsent( repositoryId, key -> repositoryEntryService.getRepositoryEntry( repositoryId ) );
    }

    @Override
    public RepositoryId deleteRepository( final DeleteRepositoryParams params )
    {
        requireAdminRole();
        final RepositoryId repositoryId = params.getRepositoryId();
        repositoryMap.compute( repositoryId, ( key, previousRepository ) -> {
            repositoryEntryService.deleteRepositoryEntry( repositoryId );
            nodeRepositoryService.delete( repositoryId );
            return null;
        } );

        invalidatePathCache();

        return repositoryId;
    }

    private void invalidatePathCache()
    {
        this.nodeStorageService.invalidate();
    }

    @Override
    public Branch deleteBranch( final DeleteBranchParams params )
    {
        requireAdminRole();
        final RepositoryId repositoryId = ContextAccessor.current().
            getRepositoryId();

        repositoryMap.compute( repositoryId, ( key, previousRepository ) -> doDeleteBranch( params, repositoryId, previousRepository ) );

        invalidatePathCache();

        return params.getBranch();
    }

    private Repository doDeleteBranch( final DeleteBranchParams params, final RepositoryId repositoryId, Repository previousRepository )
    {
        //If the repository entry does not exist, throws an exception
        previousRepository = previousRepository == null ? repositoryEntryService.getRepositoryEntry( repositoryId ) : previousRepository;
        if ( previousRepository == null )
        {
            throw new RepositoryNotFoundException( repositoryId );
        }

        //If the branch does not exist, throws an exception
        final Branch branch = params.getBranch();
        if ( !previousRepository.getBranches().contains( branch ) )
        {
            throw new BranchNotFoundException( branch );
        }

        //If the branch has a child branch, throws an exception
        previousRepository.getBranchInfos().
            stream().
            filter( branchInfo -> branch.equals( branchInfo.getParentBranch() ) ).
            findFirst().
            ifPresent( childBranch -> {
                throw new ChildBranchFoundException( childBranch.getBranch(), branch );
            } );

        //If the root node exists, deletes it
        if ( getRootNode( previousRepository.getId(), branch ) != null )
        {
            deleteRootNode( branch );
        }

        //Updates the repository entry
        return repositoryEntryService.removeBranchFromRepositoryEntry( repositoryId, branch );
    }

    @Override
    public void invalidateAll()
    {
        repositoryMap.clear();
    }

    @Override
    public void invalidate( final RepositoryId repositoryId )
    {
        repositoryMap.remove( repositoryId );
    }

    private void requireAdminRole()
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        final boolean hasAdminRole = authInfo.hasRole( RoleKeys.ADMIN );
        if ( !hasAdminRole )
        {
            throw new ForbiddenAccessException( authInfo.getUser() );
        }
    }

    private Repository createRepositoryObject( final CreateRepositoryParams params )
    {
        return Repository.create().
            id( params.getRepositoryId() ).
            branchInfos( BranchInfos.from( RepositoryConstants.MASTER_BRANCH_INFO ) ).
            settings( params.getRepositorySettings() ).
            build();
    }

    private Node getRootNode( final RepositoryId repositoryId, final Branch branch )
    {
        final Context rootNodeContext = ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( repositoryId ).
            branch( branch ).
            build();

        final InternalContext rootNodeInternalContext = InternalContext.create( rootNodeContext ).build();

        return this.nodeStorageService.get( Node.ROOT_UUID, rootNodeInternalContext );
    }

    private void createRootNode( final CreateRepositoryParams params )
    {
        final Context rootNodeContext = ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( params.getRepositoryId() ).
            branch( RepositoryConstants.MASTER_BRANCH ).
            build();

        final InternalContext rootNodeInternalContext = InternalContext.create( rootNodeContext ).build();

        final Node rootNode = this.nodeStorageService.store( Node.createRoot().
            permissions( params.getRootPermissions() ).
            inheritPermissions( false ).
            childOrder( params.getRootChildOrder() ).
            build(), rootNodeInternalContext );

        rootNodeContext.callWith( () -> {
            RefreshCommand.create().
                indexServiceInternal( this.indexServiceInternal ).
                refreshMode( RefreshMode.SEARCH ).
                build().
                execute();
            return null;
        } );

        LOG.info( "Created root node in with id [" + rootNode.id() + "] in repository [" + params.getRepositoryId() + "]" );
    }

    private void pushRootNode( final Repository currentRepo, final Branch branch )
    {
        final Context context = ContextAccessor.current();
        final InternalContext internalContext = InternalContext.create( context ).branch( RepositoryConstants.MASTER_BRANCH ).build();
        final Node rootNode = this.nodeStorageService.get( Node.ROOT_UUID, internalContext );

        if ( rootNode == null )
        {
            throw new NodeNotFoundException( "Cannot find root-node in repository [" + currentRepo + "]" );
        }

        this.nodeStorageService.push( rootNode, branch, internalContext );
    }

    private void deleteRootNode( final Branch branch )
    {
        ContextBuilder.from( ContextAccessor.current() ).
            branch( branch ).
            build().
            runWith( () -> DeleteNodeByIdCommand.create().
                nodeId( Node.ROOT_UUID ).
                storageService( this.nodeStorageService ).
                searchService( this.nodeSearchService ).
                indexServiceInternal( this.indexServiceInternal ).
                allowDeleteRoot( true ).
                build().
                execute() );

        doRefresh();
    }

    private void doRefresh()
    {
        RefreshCommand.create().
            refreshMode( RefreshMode.ALL ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();
    }

    @Reference
    public void setRepositoryEntryService( final RepositoryEntryService repositoryEntryService )
    {
        this.repositoryEntryService = repositoryEntryService;
    }

    @Reference
    public void setIndexServiceInternal( final IndexServiceInternal indexServiceInternal )
    {
        this.indexServiceInternal = indexServiceInternal;
    }

    @Reference
    public void setNodeRepositoryService( final NodeRepositoryService nodeRepositoryService )
    {
        this.nodeRepositoryService = nodeRepositoryService;
    }

    @Reference
    public void setNodeStorageService( final NodeStorageService nodeStorageService )
    {
        this.nodeStorageService = nodeStorageService;
    }

    @Reference
    public void setNodeSearchService( final NodeSearchService nodeSearchService )
    {
        this.nodeSearchService = nodeSearchService;
    }

    @Override
    public int getOrder()
    {
        return repositoryEventListener.getOrder();
    }

    @Override
    public void onEvent( final Event event )
    {
        repositoryEventListener.onEvent( event );
    }
}
