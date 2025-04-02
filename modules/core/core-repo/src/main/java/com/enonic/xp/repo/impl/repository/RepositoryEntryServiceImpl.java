package com.enonic.xp.repo.impl.repository;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeEvents;
import com.enonic.xp.repo.impl.RepositoryEvents;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.binary.BinaryService;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.node.DeleteNodeCommand;
import com.enonic.xp.repo.impl.node.PatchNodeCommand;
import com.enonic.xp.repo.impl.node.RefreshCommand;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;
import com.enonic.xp.security.SystemConstants;

@Component
public class RepositoryEntryServiceImpl
    implements RepositoryEntryService
{

    private final IndexServiceInternal indexServiceInternal;

    private final NodeStorageService nodeStorageService;

    private final NodeSearchService nodeSearchService;

    private final EventPublisher eventPublisher;

    private final BinaryService binaryService;

    @Activate
    public RepositoryEntryServiceImpl( @Reference final IndexServiceInternal indexServiceInternal, @Reference final NodeStorageService nodeStorageService,
                                       @Reference final NodeSearchService nodeSearchService, @Reference final EventPublisher eventPublisher,
                                       @Reference final BinaryService binaryService )
    {
        this.indexServiceInternal = indexServiceInternal;
        this.nodeStorageService = nodeStorageService;
        this.nodeSearchService = nodeSearchService;
        this.eventPublisher = eventPublisher;
        this.binaryService = binaryService;
    }

    @Override
    public void createRepositoryEntry( final Repository repository )
    {
        final Node node = RepositoryNodeTranslator.toNode( repository );
        final Node createdNode = nodeStorageService.store( node, createInternalContext() ).node();

        refresh();
        eventPublisher.publish( NodeEvents.created( createdNode ) );
        eventPublisher.publish( RepositoryEvents.created( repository.getId() ) );
    }

    @Override
    public RepositoryIds findRepositoryEntryIds()
    {
        final RepositoryIds.Builder repositoryIds = RepositoryIds.create();

        final SearchResult searchResult = this.nodeSearchService.query( NodeQuery.create()
                                                                            .size( NodeSearchService.GET_ALL_SIZE_FLAG )
                                                                            .parent( RepositoryConstants.REPOSITORY_STORAGE_PARENT_PATH )
                                                                            .setOrderExpressions(
                                                                                ChildOrder.defaultOrder().getOrderExpressions() )
                                                                            .build(),
                                                                        SingleRepoSearchSource.from( createInternalContext() ) );

        searchResult.getHits().stream().map( hit -> RepositoryId.from( hit.getId() ) ).forEach( repositoryIds::add );

        return repositoryIds.build();
    }

    @Override
    public Repository getRepositoryEntry( final RepositoryId repositoryId )
    {
        final NodeId nodeId = NodeId.from( repositoryId );
        final Node node = this.nodeStorageService.get( nodeId, createInternalContext() );
        return node == null ? null : RepositoryNodeTranslator.toRepository( node );
    }

    @Override
    public Repository addBranchToRepositoryEntry( final RepositoryId repositoryId, final Branch branch )
    {
        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            id( NodeId.from( repositoryId ) ).
            editor( RepositoryNodeTranslator.toCreateBranchNodeEditor( branch ) ).
            refresh( RefreshMode.ALL ).
            build();

        return updateRepositoryNode( updateNodeParams );
    }

    @Override
    public Repository removeBranchFromRepositoryEntry( final RepositoryId repositoryId, final Branch branch )
    {
        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            id( NodeId.from( repositoryId ) ).
            editor( RepositoryNodeTranslator.toDeleteBranchNodeEditor( branch ) ).
            refresh( RefreshMode.ALL ).
            build();

        return updateRepositoryNode( updateNodeParams );
    }

    @Override
    public Repository updateRepositoryEntry( UpdateRepositoryEntryParams params )
    {
        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            id( NodeId.from( params.getRepositoryId() ) ).
            editor( RepositoryNodeTranslator.toUpdateRepositoryNodeEditor( params ) ).
            setBinaryAttachments( params.getAttachments() ).
            refresh( RefreshMode.ALL ).
            build();
        return updateRepositoryNode( updateNodeParams );
    }

    @Override
    public void deleteRepositoryEntry( final RepositoryId repositoryId )
    {
        final NodeBranchEntries deletedNodes = createContext().callWith( () -> DeleteNodeCommand.create().
            nodeId( NodeId.from( repositoryId ) ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            build().
            execute() );

        if ( deletedNodes.isNotEmpty() )
        {
            eventPublisher.publish( NodeEvents.deleted( deletedNodes ) );
            eventPublisher.publish( RepositoryEvents.deleted( repositoryId ) );
        }
    }

    @Override
    public ByteSource getBinary( AttachedBinary attachedBinary )
    {
        return binaryService.get( SystemConstants.SYSTEM_REPO_ID, attachedBinary );
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

    private Repository updateRepositoryNode( final UpdateNodeParams updateNodeParams )
    {
        final Node updatedNode =
            createContext().callWith( () -> PatchNodeCommand.create().params( PatchNodeParams.from( updateNodeParams ) ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.nodeStorageService ).
            searchService( this.nodeSearchService ).
            binaryService( this.binaryService ).
            build().execute().getResult( ContextAccessor.current().getBranch() ) );

        eventPublisher.publish( NodeEvents.updated( updatedNode ) );

        Repository repository = RepositoryNodeTranslator.toRepository( updatedNode );

        eventPublisher.publish( RepositoryEvents.updated( repository.getId() ) );

        return repository;
    }

    private Context createContext()
    {
        return ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( SystemConstants.SYSTEM_REPO_ID ).
            branch( SystemConstants.BRANCH_SYSTEM ).
            build();
    }

    private InternalContext createInternalContext()
    {
        return InternalContext.create( ContextAccessor.current() ).
            repositoryId( SystemConstants.SYSTEM_REPO_ID ).
            branch( SystemConstants.BRANCH_SYSTEM ).
            build();
    }
}
