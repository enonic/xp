package com.enonic.xp.repo.impl.node;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.ApplyNodePermissionsResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.SingleRepoSearchSource;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static com.enonic.xp.repo.impl.node.NodeConstants.CLOCK;

final class ApplyNodePermissionsCommand
    extends AbstractNodeCommand
{
    private static final Logger LOG = LoggerFactory.getLogger( ApplyNodePermissionsCommand.class );

    private final ApplyNodePermissionsParams params;

    private final PermissionsMergingStrategy mergingStrategy;

    private final ApplyNodePermissionsResult.Builder resultBuilder = ApplyNodePermissionsResult.create();

    private ApplyNodePermissionsCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.mergingStrategy = builder.mergingStrategy;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ApplyNodePermissionsResult execute()
    {
        final Node node = doGetById( params.getNodeId() );

        if ( node == null )
        {
            return resultBuilder.build();
        }

        refresh( RefreshMode.SEARCH );

        applyPermissions( params.getPermissions() != null ? params.getPermissions() : node.getPermissions(), node );

        refresh( RefreshMode.ALL );

        return resultBuilder.build();
    }

    private void applyPermissions( final AccessControlList permissions, final Node node )
    {
        if ( NodePermissionsResolver.userHasPermission( ContextAccessor.current().getAuthInfo(), Permission.WRITE_PERMISSIONS,
                                                        node.getPermissions() ) )
        {
            final Node childApplied = storePermissions( permissions, node );

            if ( params.getListener() != null )
            {
                params.getListener().permissionsApplied( 1 );
            }

            final AccessControlList parentPermissions = childApplied.getPermissions();

            final NodeIds childrenIds = NodeIds.from( this.nodeSearchService.query(
                NodeQuery.create().size( NodeSearchService.GET_ALL_SIZE_FLAG ).parent( childApplied.path() ).build(),
                SingleRepoSearchSource.from( ContextAccessor.current() ) ).getIds() );

            final Nodes children = this.nodeStorageService.get( childrenIds, InternalContext.from( ContextAccessor.current() ) );

            for ( Node child : children )
            {
                applyPermissions( parentPermissions, child );
            }
            resultBuilder.succeedNode( childApplied );
        }
        else
        {
            params.getListener().notEnoughRights( 1 );
            resultBuilder.skippedNode( node );

            LOG.info( "Not enough rights for applying permissions to node [" + node.id() + "] " + node.path() );
        }
    }

    private Node storePermissions( final AccessControlList permissions, final Node node )
    {
        final Node updatedNode;
        final boolean isParent = node.id().equals( params.getNodeId() );

        if ( params.isOverwriteChildPermissions() || node.inheritsPermissions() || isParent )
        {
            updatedNode = createUpdatedNode( node, permissions, !isParent || params.isInheritPermissions() );
        }
        else
        {
            final AccessControlList mergedPermissions = mergingStrategy.mergePermissions( node.getPermissions(), permissions );
            updatedNode = createUpdatedNode( node, mergedPermissions, false );
        }

        return this.nodeStorageService.store( updatedNode, InternalContext.from( ContextAccessor.current() ) );
    }

    private Node createUpdatedNode( final Node persistedNode, final AccessControlList permissions, final boolean inheritsPermissions )
    {
        final Node.Builder updateNodeBuilder = Node.create( persistedNode ).
            timestamp( Instant.now( CLOCK ) ).
            permissions( permissions ).
            inheritPermissions( inheritsPermissions );
        return updateNodeBuilder.build();
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private ApplyNodePermissionsParams params;

        private PermissionsMergingStrategy mergingStrategy = new DefaultPermissionsMergingStrategy();

        Builder()
        {
            super();
        }

        public Builder params( final ApplyNodePermissionsParams params )
        {
            this.params = params;
            return this;
        }

        public Builder mergingStrategy( final PermissionsMergingStrategy mergingStrategy )
        {
            this.mergingStrategy = mergingStrategy;
            return this;
        }

        public ApplyNodePermissionsCommand build()
        {
            validate();
            return new ApplyNodePermissionsCommand( this );
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( params );
            Preconditions.checkNotNull( mergingStrategy );
        }
    }

}
