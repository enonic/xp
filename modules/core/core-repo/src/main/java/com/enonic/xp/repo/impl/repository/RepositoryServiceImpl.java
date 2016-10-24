package com.enonic.xp.repo.impl.repository;

import java.util.concurrent.ConcurrentMap;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeEditor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeEvents;
import com.enonic.xp.repo.impl.binary.BinaryService;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.node.UpdateNodeCommand;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repository.CreateBranchParams;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.NodeRepositoryService;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryNotFoundException;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.SystemConstants;

@Component(immediate = true)
public class RepositoryServiceImpl
    implements RepositoryService
{
    private static final Logger LOG = LoggerFactory.getLogger( RepositoryServiceImpl.class );

    private final ConcurrentMap<RepositoryId, Repository> repositorySettingsMap = Maps.newConcurrentMap();

    private IndexServiceInternal indexServiceInternal;

    private NodeRepositoryService nodeRepositoryService;

    private NodeStorageService nodeStorageService;

    private NodeSearchService nodeSearchService;

    private EventPublisher eventPublisher;

    private BinaryService binaryService;

    @SuppressWarnings("unused")
    @Activate
    public void initialize()
    {
        if ( this.indexServiceInternal.isMaster() )
        {
            new SystemRepoInitializer( this ).initialize();
        }
    }

    @Override
    public boolean isInitialized( final RepositoryId repositoryId )
    {
        return this.get( repositoryId ) != null;
    }

    @Override
    public Repository createRepository( final CreateRepositoryParams params )
    {
        return repositorySettingsMap.compute( params.getRepositoryId(), ( key, previousRepository ) -> {
            if ( previousRepository != null || repositoryNodeExists( key ) )
            {
                throw new RepositoryAlreadyExistException( key );
            }
            createRootNode( params, RepositoryConstants.MASTER_BRANCH );
            final Repository repository = createRepositoryObject( params );
            storeRepositoryEntry( repository );

            return repository;
        } );
    }

    @Override
    public Branch createBranch( final CreateBranchParams createBranchParams )
    {
        final RepositoryId repositoryId = ContextAccessor.current().
            getRepositoryId();

        repositorySettingsMap.compute( repositoryId, ( key, previousRepository ) -> {
            if ( previousRepository == null )
            {
                throw new RepositoryNotFoundException( "Cannot create branch in repository [" + repositoryId + "], not found" );
            }

            final Branch newBranch = createBranchParams.getBranch();
            if ( previousRepository.getBranches().contains( newBranch ) )
            {
                throw new BranchAlreadyExistException( newBranch );
            }

            pushRootNode( previousRepository, newBranch );
            final Repository repository = createRepositoryObject( createBranchParams, previousRepository );
            final NodeId nodeId = NodeId.from( previousRepository.getId().toString() );
            NodeEditor nodeEditor = RepositoryNodeTranslator.toCreateBranchNodeEditor( newBranch );
            updateRepositoryEntry( nodeId, nodeEditor );
            return repository;
        } );

        return createBranchParams.getBranch();
    }

    private Repository createRepositoryObject( final CreateBranchParams createBranchParams, final Repository previousRepository )
    {
        final ImmutableSet.Builder<Branch> branches = ImmutableSet.builder();
        branches.addAll( previousRepository.getBranches() );
        branches.add( createBranchParams.getBranch() );
        return Repository.create( previousRepository ).
            branches( Branches.from( branches.build() ) ).
            build();
    }

    @Override
    public Repositories list()
    {
        return Repositories.from( repositorySettingsMap.values() );
    }


    @Override
    public Repository get( final RepositoryId repositoryId )
    {
        return repositorySettingsMap.computeIfAbsent( repositoryId, key -> {
            final Node node = getRepositoryNode( repositoryId );
            return node == null ? null : RepositoryNodeTranslator.toRepository( node );
        } );
    }

    private Repository createRepositoryObject( final CreateRepositoryParams params )
    {
        return Repository.create().
            id( params.getRepositoryId() ).
            branches( Branches.from( RepositoryConstants.MASTER_BRANCH ) ).
            settings( params.getRepositorySettings() ).
            build();
    }

    private void storeRepositoryEntry( final Repository repository )
    {
        final Node node = RepositoryNodeTranslator.toNode( repository );

        nodeStorageService.store( node, InternalContext.create( ContextAccessor.current() ).
            repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
            branch( SystemConstants.BRANCH_SYSTEM ).
            build() );
    }

    private void updateRepositoryEntry( final NodeId nodeId, final NodeEditor nodeEditor )
    {
        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            id( nodeId ).
            editor( nodeEditor ).
            build();

        final Context context = ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
            branch( SystemConstants.BRANCH_SYSTEM ).
            build();

        context.callWith( () -> {
            final Node updatedNode = UpdateNodeCommand.create().
                params( updateNodeParams ).
                indexServiceInternal( this.indexServiceInternal ).
                storageService( this.nodeStorageService ).
                searchService( this.nodeSearchService ).
                binaryService( this.binaryService ).
                build().
                execute();

            if ( updatedNode != null )
            {
                this.eventPublisher.publish( NodeEvents.updated( updatedNode ) );
            }
            return null;
        } );
    }

    private void createRootNode( final CreateRepositoryParams params, final Branch branch )
    {
        if ( !this.nodeRepositoryService.isInitialized( params.getRepositoryId() ) )
        {
            this.nodeRepositoryService.create( params );
        }

        final InternalContext rootNodeContext = InternalContext.create( ContextAccessor.current() ).
            repositoryId( params.getRepositoryId() ).
            branch( branch ).
            build();

        final Node rootNode = this.nodeStorageService.store( Node.createRoot().
            permissions( params.getRootPermissions() ).
            inheritPermissions( params.isInheritPermissions() ).
            childOrder( params.getRootChildOrder() ).
            build(), rootNodeContext );

        LOG.info( "Created root node in  with id [" + rootNode.id() + "] in repository [" + params.getRepositoryId() + "]" );
    }

    private Branch pushRootNode( final Repository currentRepo, final Branch branch )
    {
        final Context context = ContextAccessor.current();
        final InternalContext internalContext = InternalContext.create( context ).branch( RepositoryConstants.MASTER_BRANCH ).build();
        final Node rootNode = this.nodeStorageService.get( Node.ROOT_UUID, internalContext );

        if ( rootNode == null )
        {
            throw new NodeNotFoundException( "Cannot find root-node in repository [" + currentRepo + "]" );
        }

        this.nodeStorageService.push( rootNode, branch, internalContext );

        return branch;
    }

    private boolean repositoryNodeExists( final RepositoryId repositoryId )
    {
        try
        {
            return getRepositoryNode( repositoryId ) != null;
        }
        catch ( Exception e )
        {
            return false;
        }
    }

    private Node getRepositoryNode( final RepositoryId repositoryId )
    {
        if ( this.nodeRepositoryService.isInitialized( SystemConstants.SYSTEM_REPO.getId() ) )
        {
            final NodeId nodeId = NodeId.from( repositoryId.toString() );
            return ContextBuilder.from( ContextAccessor.current() ).
                repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
                branch( SystemConstants.BRANCH_SYSTEM ).
                build().
                callWith( () -> this.nodeStorageService.get( nodeId, InternalContext.from( ContextAccessor.current() ) ) );
        }
        return null;
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

    @Reference
    public void setEventPublisher( final EventPublisher eventPublisher )
    {
        this.eventPublisher = eventPublisher;
    }

    @Reference
    public void setBinaryService( final BinaryService binaryService )
    {
        this.binaryService = binaryService;
    }
}
