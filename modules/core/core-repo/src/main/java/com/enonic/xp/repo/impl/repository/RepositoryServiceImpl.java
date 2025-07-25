package com.enonic.xp.repo.impl.repository;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.PushNodeEntry;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQueryResultFactory;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.node.RefreshCommand;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repository.BranchNotFoundException;
import com.enonic.xp.repository.CreateBranchParams;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.DeleteBranchParams;
import com.enonic.xp.repository.DeleteRepositoryParams;
import com.enonic.xp.repository.EditableRepository;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryExeption;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryNotFoundException;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.repository.UpdateRepositoryParams;
import com.enonic.xp.repository.internal.InternalRepositoryService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.util.BinaryReference;

public class RepositoryServiceImpl
    implements RepositoryService, InternalRepositoryService
{
    private static final Logger LOG = LoggerFactory.getLogger( RepositoryServiceImpl.class );

    private final ConcurrentMap<RepositoryId, RepositoryEntry> repositoryMap = new ConcurrentHashMap<>();

    private final RepositoryEntryService repositoryEntryService;

    private final IndexServiceInternal indexServiceInternal;

    private final NodeRepositoryService nodeRepositoryService;

    private final NodeStorageService nodeStorageService;

    private final NodeSearchService nodeSearchService;

    public RepositoryServiceImpl( final RepositoryEntryService repositoryEntryService, final IndexServiceInternal indexServiceInternal,
                                  final NodeRepositoryService nodeRepositoryService, final NodeStorageService nodeStorageService,
                                  final NodeSearchService nodeSearchService )
    {
        this.repositoryEntryService = repositoryEntryService;
        this.indexServiceInternal = indexServiceInternal;
        this.nodeRepositoryService = nodeRepositoryService;
        this.nodeStorageService = nodeStorageService;
        this.nodeSearchService = nodeSearchService;
    }

    @Override
    public Repository createRepository( final CreateRepositoryParams params )
    {
        requireAdminRole();

        return repositoryMap.compute( params.getRepositoryId(), ( repositoryId, previousRepository ) -> doCreateRepo( params ) )
            .asRepository();
    }

    private RepositoryEntry doCreateRepo( final CreateRepositoryParams params )
    {
        final RepositoryId repositoryId = params.getRepositoryId();
        final boolean repoAlreadyInitialized = this.nodeRepositoryService.isInitialized( repositoryId );

        if ( repoAlreadyInitialized )
        {
            throw new RepositoryAlreadyExistException( repositoryId );
        }

        this.nodeRepositoryService.create( CreateRepositoryIndexParams.create()
                                               .repositoryId( params.getRepositoryId() )
                                               .build() );

        createRootNode( params );

        final RepositoryEntry repository = RepositoryEntry.create()
            .id( params.getRepositoryId() )
            .branches( Branches.from( RepositoryConstants.MASTER_BRANCH ) )
            .data( params.getData() )
            .transientFlag( params.isTransient() )
            .build();
        repositoryEntryService.createRepositoryEntry( repository );
        return repository;
    }

    @Override
    public Repository updateRepository( final UpdateRepositoryParams params )
    {
        requireAdminRole();

        RepositoryEntry repository = repositoryMap.compute( params.getRepositoryId(),
                                                       ( key, previousRepository ) -> doUpdateRepository( params, previousRepository ) );

        invalidatePathCache();

        return repository.asRepository();
    }

    private RepositoryEntry doUpdateRepository( final UpdateRepositoryParams updateRepositoryParams, RepositoryEntry previousRepository )
    {
        RepositoryId repositoryId = updateRepositoryParams.getRepositoryId();

        previousRepository = previousRepository == null ? repositoryEntryService.getRepositoryEntry( repositoryId ) : previousRepository;

        if ( previousRepository == null )
        {
            throw new RepositoryNotFoundException( repositoryId );
        }

        final EditableRepository editableRepository = new EditableRepository( previousRepository.asRepository() );

        updateRepositoryParams.getEditor().accept( editableRepository );

        UpdateRepositoryEntryParams params = UpdateRepositoryEntryParams.create().
            repositoryId( repositoryId ).
            repositoryData( editableRepository.data ).
            attachments( editableRepository.binaryAttachments ).
            transientFlag( editableRepository.transientFlag ).
            build();

        return repositoryEntryService.updateRepositoryEntry( params );
    }

    @Override
    public Branch createBranch( final CreateBranchParams createBranchParams )
    {
        requireAdminRole();

        repositoryMap.compute( ContextAccessor.current().getRepositoryId(),
                               ( repositoryId, previousRepository ) -> doCreateBranch( createBranchParams, repositoryId, previousRepository ) );

        return createBranchParams.getBranch();
    }

    private RepositoryEntry doCreateBranch( final CreateBranchParams createBranchParams, final RepositoryId repositoryId,
                                            RepositoryEntry previousRepository )
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
            final Context context = ContextAccessor.current();
            final InternalContext internalContext = InternalContext.create( context ).branch( RepositoryConstants.MASTER_BRANCH ).build();
            final NodeBranchEntry rootNode = this.nodeStorageService.getBranchNodeVersion( Node.ROOT_UUID, internalContext );

            if ( rootNode == null )
            {
                throw new NodeNotFoundException( "Cannot find root-node in repository [" + previousRepository.getId() + "]" );
            }

            this.nodeStorageService.push( Collections.singleton( PushNodeEntry.create().nodeBranchEntry( rootNode ).build()), newBranch,
                                          c -> {
            }, internalContext );

            doRefresh();
        }

        //Updates the repository entry
        return repositoryEntryService.addBranchToRepositoryEntry( repositoryId, newBranch );
    }

    @Override
    public Repositories list()
    {
        requireAdminRole();
        return repositoryEntryService.findRepositoryEntryIds().stream().
            map( this::doGet ).
            collect( Repositories.collector() );
    }

    @Override
    public boolean isInitialized( final RepositoryId repositoryId )
    {
        requireAdminRole();
        return this.nodeRepositoryService.isInitialized( repositoryId ) &&
            this.repositoryEntryService.getRepositoryEntry( repositoryId ) != null;
    }

    @Override
    public Repository get( final RepositoryId repositoryId )
    {
        requireAdminRole();
        return doGet( repositoryId );
    }

    private Repository doGet( final RepositoryId repositoryId )
    {
        final RepositoryEntry repositoryEntry =
            repositoryMap.computeIfAbsent( repositoryId, key -> repositoryEntryService.getRepositoryEntry( repositoryId ) );
        return repositoryEntry == null ? null : repositoryEntry.asRepository();
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

        final RepositoryId repositoryId = ContextAccessor.current().getRepositoryId();
        final Branch branch = params.getBranch();
        checkProtectedBranch( repositoryId, branch );

        repositoryMap.compute( repositoryId,
                               ( repo, previousRepository ) -> doDeleteBranch( params, repo, previousRepository ) );

        invalidatePathCache();

        return params.getBranch();
    }

    private static void checkProtectedBranch( final RepositoryId repositoryId, final Branch branchId )
    {
        if ( ( SystemConstants.SYSTEM_REPO_ID.equals( repositoryId ) && SystemConstants.BRANCH_SYSTEM.equals( branchId ) ) ||
            ( repositoryId.toString().startsWith( ProjectConstants.PROJECT_REPO_ID_PREFIX ) &&
                ( ContentConstants.BRANCH_DRAFT.equals( branchId ) || ContentConstants.BRANCH_MASTER.equals( branchId ) ) ) )
        {
            throw new RepositoryExeption( "No allowed to delete branch [" + branchId + "] in repository [" + repositoryId + "]" );
        }
    }

    private RepositoryEntry doDeleteBranch( final DeleteBranchParams params, final RepositoryId repositoryId, RepositoryEntry previousRepository )
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

        //If the root node exists, deletes it
        if ( getRootNode( previousRepository.getId(), branch ) != null )
        {
            ContextBuilder.from( ContextAccessor.current() ).
                branch( branch ).
                build().
                runWith( this::deleteAllNodes );
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

    @Override
    public void recreateMissing()
    {
        for ( RepositoryId repositoryId : repositoryEntryService.findRepositoryEntryIds() )
        {
            if ( !this.nodeRepositoryService.isInitialized( repositoryId ) )
            {
                final CreateRepositoryIndexParams createRepositoryParams = CreateRepositoryIndexParams.create()
                    .repositorySettings( repositoryEntryService.getRepositoryEntry( repositoryId ).getSettings() )
                    .repositoryId( repositoryId )
                    .build();
                this.nodeRepositoryService.create( createRepositoryParams );
            }
        }
    }

    @Override
    public ByteSource getBinary( final RepositoryId repositoryId, final BinaryReference binaryReference )
    {
        requireAdminRole();

        RepositoryEntry repository = repositoryEntryService.getRepositoryEntry( repositoryId );
        if ( repository == null )
        {
            throw new RepositoryNotFoundException( repositoryId );
        }

        final AttachedBinary attachedBinary = repository.getAttachments().getByBinaryReference( binaryReference );
        return attachedBinary == null ? null : repositoryEntryService.getBinary( attachedBinary );
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
            branch( RepositoryConstants.MASTER_BRANCH ).build();

        final InternalContext rootNodeInternalContext = InternalContext.create( rootNodeContext ).build();

        final Node rootNode = this.nodeStorageService.store( Node.createRoot()
                                                                 .permissions( params.getRootPermissions() )
                                                                 .childOrder( params.getRootChildOrder() )
                                                                 .build(), rootNodeInternalContext ).node();

        doRefresh();

        LOG.info( "Created root node with id [{}] in repository [{}]", rootNode.id(), params.getRepositoryId() );
    }

    private void deleteAllNodes()
    {
        doRefresh();

        final InternalContext context = InternalContext.from( ContextAccessor.current() );
        final NodeBranchQuery queryAll = NodeBranchQuery.create()
            .size( NodeSearchService.GET_ALL_SIZE_FLAG )
            .addQueryFilter( ValueFilter.create()
                                 .fieldName( BranchIndexPath.BRANCH_NAME.getPath() )
                                 .addValue( ValueFactory.newString( context.getBranch().getValue() ) )
                                 .build() )
            .build();

        final NodeBranchEntries nodeBranchEntries =
            NodeBranchQueryResultFactory.create( this.nodeSearchService.query( queryAll, context.getRepositoryId() ) );

        this.nodeStorageService.delete( nodeBranchEntries.getSet(), context );

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
}
