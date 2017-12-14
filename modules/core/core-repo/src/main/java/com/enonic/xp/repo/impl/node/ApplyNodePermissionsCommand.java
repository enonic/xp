package com.enonic.xp.repo.impl.node;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static com.enonic.xp.repo.impl.node.NodePermissionsResolver.contextUserHasPermissionOrAdmin;

final class ApplyNodePermissionsCommand
    extends AbstractNodeCommand
{
    private final static Logger LOG = LoggerFactory.getLogger( ApplyNodePermissionsCommand.class );

    private final ApplyNodePermissionsParams params;

    private final PermissionsMergingStrategy mergingStrategy;

    private final Nodes.Builder updatedNodes = Nodes.create();

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

    public Nodes execute()
    {
        final Node node = doGetById( params.getNodeId() );

        if ( node == null )
        {
            return Nodes.empty();
        }

        applyPermissionsToChildren( node );

        return updatedNodes.build();
    }

    private void applyPermissionsToChildren( final Node parent )
    {
        final AccessControlList parentPermissions = parent.getPermissions();

        final FindNodesByParentParams findByParentParams = FindNodesByParentParams.create().
            parentPath( parent.path() ).
            size( NodeSearchService.GET_ALL_SIZE_FLAG ).
            build();

        final FindNodesByParentResult result = doFindNodesByParent( findByParentParams );

        final Nodes children = GetNodesByIdsCommand.create( this ).
            ids( result.getNodeIds() ).
            build().
            execute();

        for ( Node child : children )
        {
            if ( contextUserHasPermissionOrAdmin( Permission.WRITE_PERMISSIONS, child ) )
            {
                final Node childApplied = applyNodePermissions( parentPermissions, child );
                applyPermissionsToChildren( childApplied );
                updatedNodes.add( childApplied );
            }
            else
            {
                LOG.info( "Not enough rights for applying permissions to node [" + child.id() + "] " + child.path() );
            }
        }
    }

    private Node applyNodePermissions( final AccessControlList parentPermissions, final Node node )
    {
        final Node updatedNode;
        if ( params.isOverwriteChildPermissions() || node.inheritsPermissions() )
        {
            updatedNode = createUpdatedNode( node, parentPermissions, true );

            final Node result = StoreNodeCommand.create( this ).
                node( updatedNode ).
                updateMetadataOnly( false ).
                build().
                execute();

            return result;
        }
       /* else
        {
            final AccessControlList mergedPermissions = mergingStrategy.mergePermissions( node.getPermissions(), parentPermissions );
            updatedNode = createUpdatedNode( node, mergedPermissions, false );
        }*/
        return node;

    }

    private Node createUpdatedNode( final Node persistedNode, final AccessControlList permissions, final boolean inheritsPermissions )
    {
        final Node.Builder updateNodeBuilder = Node.create( persistedNode ).
            timestamp( Instant.now() ).
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
