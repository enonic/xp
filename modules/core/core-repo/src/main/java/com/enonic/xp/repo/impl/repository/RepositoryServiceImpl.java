package com.enonic.xp.repo.impl.repository;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.google.common.io.ByteSource;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntries;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.branch.BranchService;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQuery;
import com.enonic.xp.repo.impl.branch.search.NodeBranchQueryResultFactory;
import com.enonic.xp.repo.impl.branch.storage.BranchIndexPath;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repository.BranchAlreadyExistsException;
import com.enonic.xp.repository.BranchNotFoundException;
import com.enonic.xp.repository.CreateBranchParams;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.DeleteBranchParams;
import com.enonic.xp.repository.DeleteRepositoryParams;
import com.enonic.xp.repository.EditableRepository;
import com.enonic.xp.repository.IndexException;
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
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.util.BinaryReference;

public class RepositoryServiceImpl
    implements RepositoryService, InternalRepositoryService
{
    private final ConcurrentMap<RepositoryId, Repository> repositoryMap = new ConcurrentHashMap<>();

    private final RepositoryEntryService repositoryEntryService;

    private final NodeRepositoryService nodeRepositoryService;

    private final NodeStorageService nodeStorageService;

    private final NodeSearchService nodeSearchService;

    private final BranchService branchService;

    private final RepositoryCreator repositoryCreator;

    private final RepositoryAuditLogSupport repositoryAuditLogSupport;

    public RepositoryServiceImpl( final RepositoryEntryService repositoryEntryService, final NodeRepositoryService nodeRepositoryService,
                                  final NodeStorageService nodeStorageService, final NodeSearchService nodeSearchService,
                                  final BranchService branchService, final RepositoryAuditLogSupport repositoryAuditLogSupport )
    {
        this.repositoryEntryService = repositoryEntryService;
        this.nodeRepositoryService = nodeRepositoryService;
        this.nodeStorageService = nodeStorageService;
        this.nodeSearchService = nodeSearchService;
        this.branchService = branchService;
        this.repositoryCreator = new RepositoryCreator( nodeRepositoryService, nodeStorageService, repositoryEntryService );
        this.repositoryAuditLogSupport = repositoryAuditLogSupport;
    }

    @Override
    public boolean isInitialized( final RepositoryId repositoryId )
    {
        requireAdminRole();

        return this.repositoryCreator.isInitialized( repositoryId );
    }

    @Override
    public void initializeRepository( final CreateRepositoryParams params )
    {
        requireAdminRole();

        repositoryCreator.createRepository( params, RepositorySettings.create().build(), AttachedBinaries.empty(), true );
    }

    @Override
    public Repository createRepository( final CreateRepositoryParams params )
    {
        requireAdminRole();

        final Repository repository = copyRepository( repositoryMap.compute( params.getRepositoryId(), ( _, _ ) -> doCreateRepo( params ) ) );
        repositoryAuditLogSupport.createRepository( params );
        return repository;
    }

    private Repository doCreateRepo( final CreateRepositoryParams params )
    {
        final RepositoryEntry entry =
            repositoryCreator.createRepository( params, RepositorySettings.create().build(), AttachedBinaries.empty(), false );

        return asRepository( entry, Branches.from( RepositoryConstants.MASTER_BRANCH ) );
    }

    @Override
    public Repository updateRepository( final UpdateRepositoryParams params )
    {
        requireAdminRole();

        return copyRepository( repositoryMap.compute( params.getRepositoryId(), ( _, _ ) -> doUpdateRepository( params ) ) );
    }

    private Repository doUpdateRepository( final UpdateRepositoryParams updateRepositoryParams )
    {
        final RepositoryId repositoryId = updateRepositoryParams.getRepositoryId();
        final RepositoryEntry entry = getRequitededRepositoryEntry( repositoryId );
        final Branches branches = getRequiredBranches( repositoryId );

        final EditableRepository editableRepository = new EditableRepository( asRepository( entry, branches ) );

        updateRepositoryParams.getEditor().accept( editableRepository );

        final RepositoryEntry entryToUpdate = RepositoryEntry.create( entry )
            .id( repositoryId )
            .data( editableRepository.data )
            .transientFlag( editableRepository.transientFlag )
            .build();

        final BinaryAttachments.Builder binaryAttachments = BinaryAttachments.create();
        editableRepository.binaryAttachments.forEach( binaryAttachments::add );

        final RepositoryEntry updatedEntry = repositoryEntryService.updateRepositoryEntry( entryToUpdate, binaryAttachments.build() );
        return asRepository( updatedEntry, branches );
    }

    @Override
    public Branch createBranch( final CreateBranchParams createBranchParams )
    {
        requireAdminRole();

        repositoryMap.compute( ContextAccessor.current().getRepositoryId(),
                               ( repositoryId, _ ) -> doCreateBranch( createBranchParams, repositoryId ) );

        repositoryAuditLogSupport.createBranch( createBranchParams );
        return createBranchParams.getBranch();
    }

    private Repository doCreateBranch( final CreateBranchParams createBranchParams, final RepositoryId repositoryId )
    {
        final RepositoryEntry entry = getRequitededRepositoryEntry( repositoryId );

        final Branch newBranch = createBranchParams.getBranch();
        final InternalContext newBranchContext =
            InternalContext.create( ContextAccessor.current() ).repositoryId( repositoryId ).branch( newBranch ).build();

        final Branches branches = getRequiredBranches( repositoryId );
        if ( branches.contains( newBranch ) )
        {
            throw new BranchAlreadyExistsException( newBranch );
        }

        final NodeBranchEntry masterRootNode = this.nodeStorageService.getNodeBranchEntry( NodeId.ROOT,
                                                                                           InternalContext.create( newBranchContext )
                                                                                               .branch( RepositoryConstants.MASTER_BRANCH )
                                                                                               .build() );

        if ( masterRootNode == null )
        {
            throw new NodeNotFoundException( "Cannot find root-node in repository [" + repositoryId + "]" );
        }

        this.nodeStorageService.push( masterRootNode, newBranchContext );

        this.nodeRepositoryService.refresh( repositoryId );

        return asRepository( entry, Stream.concat( branches.stream(), Stream.of( newBranch ) ).collect( Branches.collector() ) );
    }

    @Override
    public Repositories list()
    {
        requireAdminRole();
        return repositoryEntryService.findRepositoryEntryIds()
            .stream()
            .map( this::doGet )
            .filter( Objects::nonNull )
            .collect( Repositories.collector() );
    }

    @Override
    public Repository get( final RepositoryId repositoryId )
    {
        requireAdminRole();
        return doGet( repositoryId );
    }

    private @Nullable Repository doGet( final RepositoryId repositoryId )
    {
        final Repository cached = repositoryMap.computeIfAbsent( repositoryId, this::loadRepository );
        return cached == null ? null : copyRepository( cached );
    }

    private Repository loadRepository( final RepositoryId repositoryId )
    {
        final RepositoryEntry entry = repositoryEntryService.getRepositoryEntry( repositoryId );
        if ( entry == null )
        {
            return null;
        }
        final Branches branches = getBranches( repositoryId );
        return branches.isEmpty() ? null : asRepository( entry, branches );
    }

    @Override
    public RepositoryId deleteRepository( final DeleteRepositoryParams params )
    {
        requireAdminRole();
        final RepositoryId repositoryId = params.getRepositoryId();
        repositoryMap.compute( repositoryId, ( _, _ ) -> {
            repositoryEntryService.deleteRepositoryEntry( repositoryId );
            nodeRepositoryService.delete( repositoryId );
            return null;
        } );

        nodeStorageService.invalidate();

        repositoryAuditLogSupport.deleteRepository( params );
        return repositoryId;
    }

    @Override
    public Branch deleteBranch( final DeleteBranchParams params )
    {
        requireAdminRole();

        final RepositoryId repositoryId = ContextAccessor.current().getRepositoryId();
        final Branch branch = params.getBranch();
        checkProtectedBranch( repositoryId, branch );

        repositoryMap.compute( repositoryId, ( _, _ ) -> doDeleteBranch( params, repositoryId ) );

        repositoryAuditLogSupport.deleteBranch( params );
        return params.getBranch();
    }

    private static void checkProtectedBranch( final RepositoryId repositoryId, final Branch branchId )
    {
        if ( RepositoryConstants.MASTER_BRANCH.equals( branchId ) ||
            ( repositoryId.toString().startsWith( ProjectConstants.PROJECT_REPO_ID_PREFIX ) &&
                ( ContentConstants.BRANCH_DRAFT.equals( branchId ) ) ) )
        {
            throw new RepositoryExeption( "No allowed to delete branch [" + branchId + "] in repository [" + repositoryId + "]" );
        }
    }

    private Repository doDeleteBranch( final DeleteBranchParams params, final RepositoryId repositoryId )
    {
        final RepositoryEntry entry = getRequitededRepositoryEntry( repositoryId );
        final Branch branch = params.getBranch();
        final InternalContext branchContext =
            InternalContext.create( ContextAccessor.current() ).repositoryId( repositoryId ).branch( branch ).build();

        final Branches branches = getRequiredBranches( repositoryId );
        if ( !branches.contains( branch ) )
        {
            throw new BranchNotFoundException( branch );
        }

        //Deletes all nodes in the branch
        this.nodeRepositoryService.refresh( repositoryId );
        final NodeBranchQuery queryAll = NodeBranchQuery.create()
            .size( NodeSearchService.GET_ALL_SIZE_FLAG )
            .addQueryFilter( ValueFilter.create()
                                 .fieldName( BranchIndexPath.BRANCH_NAME.getPath() )
                                 .addValue( ValueFactory.newString( branch.getValue() ) )
                                 .build() )
            .build();

        final NodeBranchEntries nodeBranchEntries =
            NodeBranchQueryResultFactory.create( this.nodeSearchService.query( queryAll, repositoryId ) );

        this.nodeStorageService.delete( nodeBranchEntries.getSet(), branchContext );
        this.nodeRepositoryService.refresh( repositoryId );

        return asRepository( entry, Branches.from( branches.stream().filter( b -> !b.equals( branch ) ).toList() ) );
    }

    @Override
    public void invalidateAll()
    {
        repositoryMap.clear();
        nodeStorageService.invalidate();
    }

    @Override
    public void invalidate( final RepositoryId repositoryId )
    {
        repositoryMap.remove( repositoryId );
        nodeStorageService.invalidate();
    }

    @Override
    public ByteSource getBinary( final RepositoryId repositoryId, final BinaryReference binaryReference )
    {
        requireAdminRole();

        RepositoryEntry repository = getRequitededRepositoryEntry( repositoryId );

        final AttachedBinary attachedBinary = repository.getAttachments().getByBinaryReference( binaryReference );
        return attachedBinary == null ? null : repositoryEntryService.getBinary( attachedBinary );
    }

    private @NonNull RepositoryEntry getRequitededRepositoryEntry( final RepositoryId repositoryId )
    {
        final RepositoryEntry repository = repositoryEntryService.getRepositoryEntry( repositoryId );
        if ( repository == null )
        {
            throw new RepositoryNotFoundException( repositoryId );
        }
        return repository;
    }

    private static Repository copyRepository( final Repository repository )
    {
        return Repository.create()
            .id( repository.getId() )
            .branches( repository.getBranches() )
            .data( repository.getData().copy() )
            .attachments( repository.getAttachments() )
            .transientFlag( repository.isTransient() )
            .build();
    }

    private Repository asRepository( final RepositoryEntry entry, final Branches branches )
    {
        return Repository.create()
            .id( entry.getId() )
            .branches( branches )
            .data( entry.getData().copy() )
            .attachments( entry.getAttachments() )
            .transientFlag( entry.isTransient() )
            .build();
    }

    private @NonNull Branches getBranches( final RepositoryId repositoryId )
    {
        try
        {
            return branchService.getBranches( NodeId.ROOT, repositoryId );
        }
        catch ( IndexException e )
        {
            return Branches.empty();
        }
    }

    private @NonNull Branches getRequiredBranches( final RepositoryId repositoryId )
    {
        final Branches branches = getBranches( repositoryId );
        if ( branches.isEmpty() )
        {
            throw new RepositoryNotFoundException( repositoryId );
        }
        return branches;
    }

    private static void requireAdminRole()
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        final boolean hasAdminRole = authInfo.hasRole( RoleKeys.ADMIN );
        if ( !hasAdminRole )
        {
            throw new ForbiddenAccessException( authInfo.getUser() );
        }
    }
}
