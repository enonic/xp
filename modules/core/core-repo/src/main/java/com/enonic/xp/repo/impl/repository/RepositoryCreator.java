package com.enonic.xp.repo.impl.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.Node;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repo.impl.storage.StoreNodeParams;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;

public class RepositoryCreator
{
    private static final Logger LOG = LoggerFactory.getLogger( RepositoryCreator.class );

    private final NodeRepositoryService nodeRepositoryService;

    private final NodeStorageService nodeStorageService;

    private final RepositoryEntryService repositoryEntryService;

    public RepositoryCreator( final NodeRepositoryService nodeRepositoryService, final NodeStorageService nodeStorageService,
                              final RepositoryEntryService repositoryEntryService )
    {
        this.nodeRepositoryService = nodeRepositoryService;
        this.nodeStorageService = nodeStorageService;
        this.repositoryEntryService = repositoryEntryService;
    }

    public RepositoryEntry createRepository( final CreateRepositoryParams params, RepositorySettings settings,
                                             final AttachedBinaries attachedBinaries )
    {
        final RepositoryId repositoryId = params.getRepositoryId();
        this.nodeRepositoryService.create(
            CreateRepositoryIndexParams.create().repositoryId( repositoryId ).repositorySettings( settings ).build() );

        final Node rootNode = this.nodeStorageService.store( StoreNodeParams.newVersion(
                                                                 Node.createRoot().permissions( params.getRootPermissions() ).childOrder( params.getRootChildOrder() ).build() ),
                                                             InternalContext.create( ContextAccessor.current() )
                                                                 .repositoryId( repositoryId )
                                                                 .branch( RepositoryConstants.MASTER_BRANCH )
                                                                 .build() ).node();

        this.nodeRepositoryService.refresh( repositoryId );

        LOG.info( "Created root node with id [{}] in repository [{}]", rootNode.id(), repositoryId );

        final RepositoryEntry entry = RepositoryEntry.create()
            .id( repositoryId )
            .data( params.getData() )
            .settings( settings )
            .attachments( attachedBinaries )
            .transientFlag( params.isTransient() )
            .build();
        this.repositoryEntryService.createRepositoryEntry( entry );
        return entry;
    }
}
