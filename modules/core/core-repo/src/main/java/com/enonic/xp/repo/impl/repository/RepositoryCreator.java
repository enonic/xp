package com.enonic.xp.repo.impl.repository;

import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.Model;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repo.impl.storage.StoreNodeParams;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.IndexException;
import com.enonic.xp.repository.RepositoryAlreadyExistsException;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.SystemConstants;

import static java.util.Objects.requireNonNull;

public class RepositoryCreator
{
    private static final Logger LOG = LoggerFactory.getLogger( RepositoryCreator.class );

    private final NodeRepositoryService nodeRepositoryService;

    private final NodeStorageService nodeStorageService;

    private final RepositoryEntryService repositoryEntryService;

    public RepositoryCreator( final NodeRepositoryService nodeRepositoryService, final NodeStorageService nodeStorageService,
                              final RepositoryEntryService repositoryEntryService )
    {
        this.nodeRepositoryService = requireNonNull( nodeRepositoryService );
        this.nodeStorageService = requireNonNull( nodeStorageService );
        this.repositoryEntryService = requireNonNull( repositoryEntryService );
    }

    public boolean isInitialized( RepositoryId repositoryId )
    {
        return this.nodeRepositoryService.isInitialized( repositoryId ) && this.nodeStorageService.exists( NodeId.ROOT,
                                                                                                           InternalContext.create(
                                                                                                                   ContextAccessor.current() )
                                                                                                               .repositoryId( repositoryId )
                                                                                                               .branch(
                                                                                                                   RepositoryConstants.MASTER_BRANCH )
                                                                                                               .build() ) &&
            this.repositoryEntryService.getRepositoryEntry( repositoryId ) != null;
    }

    public void createSystemRepository( final RepositorySettings settings )
    {
        try
        {
            this.nodeRepositoryService.create( SystemConstants.SYSTEM_REPO_ID, settings );
        }
        catch ( IndexException e )
        {
            if ( e.getCause() instanceof IndexAlreadyExistsException )
            {
                throw new RepositoryAlreadyExistsException( SystemConstants.SYSTEM_REPO_ID );
            }
            else
            {
                throw e;
            }
        }

        final InternalContext internalContext = InternalContext.create( ContextAccessor.current() )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .branch( SystemConstants.BRANCH_SYSTEM )
            .build();

        this.nodeStorageService.store( StoreNodeParams.newVersion( Node.createRoot()
                                                                       .permissions( SystemConstants.SYSTEM_REPO_DEFAULT_ACL )
                                                                       .childOrder( RepositoryConstants.DEFAULT_CHILD_ORDER )
                                                                       .build() ), internalContext );
        LOG.info( "Created root node in system repository" );
        this.nodeRepositoryService.refresh( SystemConstants.SYSTEM_REPO_ID );

        final RepositoryEntry newEntry =
            RepositoryEntry.create().id( SystemConstants.SYSTEM_REPO_ID ).settings( settings ).modelVersion( Model.MODEL_VERSION ).build();
        this.repositoryEntryService.createRepositoryEntry( newEntry );
    }

    public RepositoryEntry createRepository( final CreateRepositoryParams params, RepositorySettings settings,
                                             final AttachedBinaries attachedBinaries, final boolean graceful )
    {
        final RepositoryId repositoryId = params.getRepositoryId();

        final RepositoryEntry existingEntry = this.repositoryEntryService.getRepositoryEntry( repositoryId );

        if ( !graceful && existingEntry != null )
        {
            throw new RepositoryAlreadyExistsException( repositoryId );
        }

        try
        {
            this.nodeRepositoryService.create( repositoryId, settings );
        }
        catch ( IndexException e )
        {
            if ( e.getCause() instanceof IndexAlreadyExistsException )
            {
                if ( graceful )
                {
                    LOG.debug( "Repository index already exists, skipping creation", e );
                }
                else
                {
                    throw new RepositoryAlreadyExistsException( repositoryId );
                }
            }
            else
            {
                throw e;
            }
        }

        final InternalContext internalContext = InternalContext.create( ContextAccessor.current() )
            .repositoryId( repositoryId )
            .branch( RepositoryConstants.MASTER_BRANCH )
            .build();

        if ( !this.nodeStorageService.exists( NodeId.ROOT, internalContext ) )
        {

            this.nodeStorageService.store( StoreNodeParams.newVersion(
                                               Node.createRoot().permissions( params.getRootPermissions() ).childOrder( params.getRootChildOrder() ).build() ),
                                           internalContext );
            this.nodeRepositoryService.refresh( repositoryId );
            LOG.info( "Created root node in repository [{}]", repositoryId );
        }
        else
        {
            if ( graceful )
            {
                LOG.debug( "Root node already exists in repository [{}], skipping root node creation", repositoryId );
            }
            else
            {
                throw new RepositoryAlreadyExistsException( repositoryId );
            }
        }

        if ( existingEntry != null )
        {
            return existingEntry;
        }
        else
        {
            final RepositoryEntry newEntry = RepositoryEntry.create()
                .id( repositoryId )
                .data( params.getData() )
                .settings( settings )
                .attachments( attachedBinaries )
                .transientFlag( params.isTransient() )
                .modelVersion( Model.MODEL_VERSION )
                .build();
            return this.repositoryEntryService.createRepositoryEntry( newEntry );
        }
    }
}
