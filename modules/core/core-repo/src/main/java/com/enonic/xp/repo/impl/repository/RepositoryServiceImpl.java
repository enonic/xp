package com.enonic.xp.repo.impl.repository;

import java.util.concurrent.ConcurrentMap;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.NodeRepositoryService;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.SystemConstants;

@Component(immediate = true)
public class RepositoryServiceImpl
    implements RepositoryService
{
    private final ConcurrentMap<RepositoryId, Repository> repositorySettingsMap = Maps.newConcurrentMap();

    private IndexServiceInternal indexServiceInternal;

    private NodeRepositoryService nodeRepositoryService;

    private NodeStorageService nodeStorageService;

    private static final Logger LOG = LoggerFactory.getLogger( RepositoryServiceImpl.class );

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
    public Repository create( final CreateRepositoryParams params )
    {
        return repositorySettingsMap.compute( params.getRepositoryId(), ( key, previousValue ) -> {

            if ( previousValue != null || repositoryNodeExists( key ) )
            {
                throw new RepositoryAlreadyExistException( key );
            }

            final Repository repository;
            if ( !this.nodeRepositoryService.isInitialized( params.getRepositoryId() ) )
            {
                repository = this.nodeRepositoryService.create( params );
            }
            else
            {
                repository = Repository.create().
                    id( params.getRepositoryId() ).
                    settings( params.getRepositorySettings() ).
                    build();
            }

            final Node node = RepositoryNodeTranslator.toNode( repository );
            ContextBuilder.from( ContextAccessor.current() ).
                repositoryId( SystemConstants.SYSTEM_REPO.getId() ).
                branch( SystemConstants.BRANCH_SYSTEM ).
                build().
                callWith( () -> nodeStorageService.store( node, InternalContext.from( ContextAccessor.current() ) ) );

            createRootNode( params );

            return repository;
        } );
    }

    private void createRootNode( final CreateRepositoryParams params )
    {
        final Node rootNode = this.nodeStorageService.store( Node.createRoot().
            permissions( params.getRootPermissions() ).
            inheritPermissions( params.isInheritPermissions() ).
            childOrder( params.getRootChildOrder() ).
            build(), InternalContext.create( ContextAccessor.current() ).
            repositoryId( params.getRepositoryId() ).
            build() );

        LOG.info( "Created root node in  with id [" + rootNode.id() + "] in repository [" + params.getRepositoryId() + "]" );
    }

    @Override
    public Repository get( final RepositoryId repositoryId )
    {
        return repositorySettingsMap.computeIfAbsent( repositoryId, key -> {
            final Node node = getRepositoryNode( repositoryId );
            return node == null ? null : RepositoryNodeTranslator.toRepository( node );
        } );
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
}
