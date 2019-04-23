package com.enonic.xp.repo.impl.repository;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeEditor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeEvents;
import com.enonic.xp.repo.impl.RepositoryEvents;
import com.enonic.xp.repo.impl.binary.BinaryService;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.node.DeleteNodeByIdCommand;
import com.enonic.xp.repo.impl.node.FindNodesByParentCommand;
import com.enonic.xp.repo.impl.node.RefreshCommand;
import com.enonic.xp.repo.impl.node.UpdateNodeCommand;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repository.NodeRepositoryService;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;
import com.enonic.xp.security.SystemConstants;

@Component
public class RepositoryEntryServiceImpl
    implements RepositoryEntryService
{

    private IndexServiceInternal indexServiceInternal;

    private NodeRepositoryService nodeRepositoryService;

    private NodeStorageService nodeStorageService;

    private NodeSearchService nodeSearchService;

    private EventPublisher eventPublisher;

    private BinaryService binaryService;

    @Override
    public void createRepositoryEntry( final Repository repository )
    {
        final Node node = RepositoryNodeTranslator.toNode( repository );
        final Node createdNode = nodeStorageService.store( node, createInternalContext() );
        if ( createdNode != null )
        {
            eventPublisher.publish( NodeEvents.created( createdNode ) );
            refresh();
            eventPublisher.publish( RepositoryEvents.created( repository.getId() ) );
        }
    }

    @Override
    public RepositoryIds findRepositoryEntryIds()
    {
        final ImmutableList.Builder<RepositoryId> repositoryIds = ImmutableList.builder();

        if ( this.nodeRepositoryService.isInitialized( SystemConstants.SYSTEM_REPO.getId() ) )
        {
            final FindNodesByParentParams findNodesByParentParams = FindNodesByParentParams.create().
                parentPath( RepositoryConstants.REPOSITORY_STORAGE_PARENT_PATH ).
                size( -1 ).
                build();

            final FindNodesByParentResult findNodesByParentResult = createContext().
                callWith( () -> FindNodesByParentCommand.create().
                    params( findNodesByParentParams ).
                    indexServiceInternal( this.indexServiceInternal ).
                    storageService( this.nodeStorageService ).
                    searchService( this.nodeSearchService ).
                    build().
                    execute() );

            findNodesByParentResult.getNodeIds().
                stream().
                map( nodeId -> RepositoryId.from( nodeId.toString() ) ).
                forEach( repositoryIds::add );
        }
        return RepositoryIds.from( repositoryIds.build() );
    }

    @Override
    public Repository getRepositoryEntry( final RepositoryId repositoryId )
    {
        if ( this.nodeRepositoryService.isInitialized( SystemConstants.SYSTEM_REPO.getId() ) )
        {
            final NodeId nodeId = NodeId.from( repositoryId.toString() );
            final Node node = this.nodeStorageService.get( nodeId, createInternalContext() );
            return node == null ? null : RepositoryNodeTranslator.toRepository( node );
        }
        return null;
    }

    @Override
    public Repository addBranchToRepositoryEntry( final RepositoryId repositoryId, final Branch branch )
    {
        NodeEditor nodeEditor = RepositoryNodeTranslator.toCreateBranchNodeEditor( branch );
        return updateRepositoryEntry( repositoryId, nodeEditor );
    }

    @Override
    public Repository removeBranchFromRepositoryEntry( final RepositoryId repositoryId, final Branch branch )
    {
        NodeEditor nodeEditor = RepositoryNodeTranslator.toDeleteBranchNodeEditor( branch );
        return updateRepositoryEntry( repositoryId, nodeEditor );
    }

    @Override
    public void deleteRepositoryEntry( final RepositoryId repositoryId )
    {
        final NodeBranchEntries deletedNodes = createContext().callWith( () -> DeleteNodeByIdCommand.create().
            nodeId( NodeId.from( repositoryId.toString() ) ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute() );

        if ( deletedNodes.isNotEmpty() )
        {
            eventPublisher.publish( NodeEvents.deleted( deletedNodes ) );
            refresh();
            eventPublisher.publish( RepositoryEvents.deleted( repositoryId ) );
        }
    }

    private void refresh()
    {
        createContext().callWith( () -> {
            RefreshCommand.create().
                indexServiceInternal( this.indexServiceInternal ).
                refreshMode( RefreshMode.ALL ).
                build().
                execute();
            return null;
        } );
    }

    private Repository updateRepositoryEntry( final RepositoryId repositoryId, final NodeEditor nodeEditor )
    {
        final NodeId nodeId = NodeId.from( repositoryId.toString() );
        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            id( nodeId ).
            editor( nodeEditor ).
            build();

        final Node updatedNode = createContext().callWith( () -> UpdateNodeCommand.create().
            params( updateNodeParams ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            binaryService( this.binaryService ).
            build().
            execute() );

        if ( updatedNode != null )
        {
            eventPublisher.publish( NodeEvents.updated( updatedNode ) );
            refresh();
            eventPublisher.publish( RepositoryEvents.updated( repositoryId ) );
        }

        return RepositoryNodeTranslator.toRepository( updatedNode );
    }

    private Context createContext()
    {
        return ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
            branch( SystemConstants.BRANCH_SYSTEM ).
            build();
    }

    private InternalContext createInternalContext()
    {
        return InternalContext.create( ContextAccessor.current() ).
            repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
            branch( SystemConstants.BRANCH_SYSTEM ).
            build();
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
