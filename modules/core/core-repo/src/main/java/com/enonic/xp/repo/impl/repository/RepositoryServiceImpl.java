package com.enonic.xp.repo.impl.repository;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repository.NodeRepositoryService;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.repository.RepositorySettings;
import com.enonic.xp.security.SystemConstants;

@Component(immediate = true)
public class RepositoryServiceImpl
    implements RepositoryService
{
    private IndexServiceInternal indexServiceInternal;

    private NodeRepositoryService nodeRepositoryService;

    private NodeStorageService nodeStorageService;

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
        return this.nodeRepositoryService.isInitialized( repositoryId );
    }

    @Override
    public RepositoryId create( final RepositorySettings repositorySettings )
    {
        this.nodeRepositoryService.create( repositorySettings );

        final Node node = RepositoryNodeTranslator.toNode( repositorySettings );
        ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
            branch( SystemConstants.BRANCH_SYSTEM ).
            build().
            callWith( () -> nodeStorageService.store( node, InternalContext.from( ContextAccessor.current() ) ) );
        return repositorySettings.getRepositoryId();
    }


    @Override
    public Repository get( final RepositoryId repositoryId )
    {
        final NodeId nodeId = NodeId.from( repositoryId.toString() );
        final Node node = ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
            branch( SystemConstants.BRANCH_SYSTEM ).
            build().
            callWith( () -> this.nodeStorageService.get( nodeId, InternalContext.from( ContextAccessor.current() ) ) );
        return RepositoryNodeTranslator.fromNode( node );
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
}
