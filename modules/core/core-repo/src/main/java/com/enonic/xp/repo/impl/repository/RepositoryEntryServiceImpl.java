package com.enonic.xp.repo.impl.repository;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.BinaryAttachment;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.SearchSources;
import com.enonic.xp.repo.impl.NodeBranchEntries;
import com.enonic.xp.repo.impl.NodeEvents;
import com.enonic.xp.repo.impl.RepositoryEvents;
import com.enonic.xp.repo.impl.SearchPreference;
import com.enonic.xp.storage.spi.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.binary.BinaryService;
import com.enonic.xp.repo.impl.node.DeleteNodeCommand;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.storage.spi.NodeSearchIndex;
import com.enonic.xp.storage.spi.NodeStore;
import com.enonic.xp.storage.spi.RepositoryStorageAdmin;
import com.enonic.xp.storage.spi.SearchResult;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repo.impl.storage.StoreNodeParams;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;
import com.enonic.xp.security.SystemConstants;

@Component
public class RepositoryEntryServiceImpl
    implements RepositoryEntryService
{
    private final RepositoryStorageAdmin repositoryStorageAdmin;

    private final NodeSearchIndex nodeSearchIndex;

    private final NodeStorageService nodeStorageService;

    private final NodeSearchService nodeSearchService;

    private final NodeStore nodeStore;

    private final EventPublisher eventPublisher;

    private final BinaryService binaryService;

    @Activate
    public RepositoryEntryServiceImpl( @Reference final RepositoryStorageAdmin repositoryStorageAdmin,
                                       @Reference final NodeSearchIndex nodeSearchIndex,
                                       @Reference final NodeStorageService nodeStorageService,
                                       @Reference final NodeSearchService nodeSearchService, @Reference final NodeStore nodeStore,
                                       @Reference final EventPublisher eventPublisher, @Reference final BinaryService binaryService )
    {
        this.repositoryStorageAdmin = repositoryStorageAdmin;
        this.nodeSearchIndex = nodeSearchIndex;
        this.nodeStorageService = nodeStorageService;
        this.nodeSearchService = nodeSearchService;
        this.nodeStore = nodeStore;
        this.eventPublisher = eventPublisher;
        this.binaryService = binaryService;
    }

    @Override
    public RepositoryEntry createRepositoryEntry( final RepositoryEntry repository )
    {
        return storeRepositoryEntry( repository, BinaryAttachments.empty(), true );
    }

    @Override
    public RepositoryEntry updateRepositoryEntry( final RepositoryEntry repository, final BinaryAttachments binaryAttachments )
    {
        return storeRepositoryEntry( repository, binaryAttachments, false );
    }

    private RepositoryEntry storeRepositoryEntry( final RepositoryEntry repository, final BinaryAttachments binaryAttachments,
                                                  final boolean isNew )
    {
        final AttachedBinaries.Builder resolvedBinaries = AttachedBinaries.create();
        for ( AttachedBinary existing : repository.getAttachments() )
        {
            resolvedBinaries.add( existing );
        }

        for ( BinaryAttachment binaryAttachment : binaryAttachments )
        {
            resolvedBinaries.add( binaryService.store( SystemConstants.SYSTEM_REPO_ID, binaryAttachment ) );
        }

        final RepositoryEntry entryWithBinaries = RepositoryEntry.create()
            .id( repository.getId() )
            .settings( repository.getSettings() )
            .data( repository.getData() )
            .attachments( resolvedBinaries.build() )
            .transientFlag( repository.isTransient() )
            .modelVersion( repository.getModelVersion() )
            .build();

        final Node node = RepositoryNodeTranslator.toNode( entryWithBinaries );
        final InternalContext internalContext = createInternalContext();

        final Node storedNode = nodeStorageService.store( StoreNodeParams.newVersion( node ), internalContext ).node();
        this.repositoryStorageAdmin.refresh( SystemConstants.SYSTEM_REPO_ID );
        this.nodeSearchIndex.refresh( SystemConstants.SYSTEM_REPO_ID );

        if ( isNew )
        {
            this.eventPublisher.publish( NodeEvents.created( storedNode, internalContext ) );
            this.eventPublisher.publish( RepositoryEvents.created( repository.getId() ) );
        }
        else
        {
            this.eventPublisher.publish( NodeEvents.updated( storedNode, internalContext ) );
            this.eventPublisher.publish( RepositoryEvents.updated( repository.getId() ) );
        }

        return entryWithBinaries;
    }

    @Override
    public RepositoryIds findRepositoryEntryIds()
    {
        final SearchResult searchResult = this.nodeSearchService.query( NodeQuery.create()
                                                                            .size( NodeSearchService.GET_ALL_SIZE_FLAG )
                                                                            .parent( RepositoryConstants.REPOSITORY_STORAGE_PARENT_PATH )
                                                                            .setOrderExpressions(
                                                                                ChildOrder.defaultOrder().getOrderExpressions() )
                                                                            .build(),
                                                                        SearchSources.from( createInternalContext() ) );

        return searchResult.getHits().stream().map( hit -> RepositoryId.from( hit.getId() ) ).collect( RepositoryIds.collector() );
    }

    @Override
    public RepositoryEntry getRepositoryEntry( final RepositoryId repositoryId )
    {
        final NodeId nodeId = NodeId.from( repositoryId );
        final Node node = this.nodeStorageService.get( nodeId, createInternalContext() );
        return node == null ? null : RepositoryNodeTranslator.toRepository( node );
    }

    @Override
    public void deleteRepositoryEntry( final RepositoryId repositoryId )
    {
        final NodeBranchEntries deletedNodes = createContext().callWith( () -> DeleteNodeCommand.create()
            .nodeId( NodeId.from( repositoryId ) )
            .repositoryStorageAdmin( this.repositoryStorageAdmin )
            .nodeSearchIndex( this.nodeSearchIndex )
            .storageService( this.nodeStorageService )
            .searchService( this.nodeSearchService )
            .nodeStore( this.nodeStore )
            .build()
            .execute() );

        if ( deletedNodes.isNotEmpty() )
        {
            eventPublisher.publish( NodeEvents.deleted( deletedNodes, createInternalContext() ) );
            eventPublisher.publish( RepositoryEvents.deleted( repositoryId ) );
        }
    }

    @Override
    public ByteSource getBinary( AttachedBinary attachedBinary )
    {
        return binaryService.get( SystemConstants.SYSTEM_REPO_ID, attachedBinary );
    }

    private Context createContext()
    {
        return ContextBuilder.from( ContextAccessor.current() )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .branch( SystemConstants.BRANCH_SYSTEM )
            .build();
    }

    private InternalContext createInternalContext()
    {
        return InternalContext.create( ContextAccessor.current() )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .branch( SystemConstants.BRANCH_SYSTEM )
            .searchPreference( SearchPreference.PRIMARY )
            .build();
    }
}
