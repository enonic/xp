package com.enonic.wem.repo.internal.entity;

import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import com.enonic.wem.repo.internal.index.query.QueryService;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static com.enonic.wem.repo.internal.entity.NodePermissionsResolver.contextUserHasPermissionOrAdmin;

final class ApplyNodePermissionsCommand
    extends AbstractNodeCommand
{
    private final static Logger LOG = LoggerFactory.getLogger( ApplyNodePermissionsCommand.class );

    private final ApplyNodePermissionsParams params;

    private final PermissionsMergingStrategy mergingStrategy;

    private ApplyNodePermissionsCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.mergingStrategy = builder.mergingStrategy;
    }

    public int execute()
    {
        final Node node = doGetById( params.getNodeId(), false );
        if ( node == null )
        {
            return 0;
        }

        final StopWatch stopWatch = new StopWatch();

        LOG.info( "Applying permissions to descendants of node [" + node.id() + "] " + node.path() );
        stopWatch.start();
        final int appliedNodeCount = applyPermissionsToChildren( node );
        stopWatch.stop();
        LOG.info( "Permissions applied to " + appliedNodeCount + " nodes. Total time: " + stopWatch.toString() );

        return appliedNodeCount;
    }

    private int applyPermissionsToChildren( final Node parent )
    {
        final AccessControlList parentPermissions = parent.getPermissions();

        final FindNodesByParentParams findByParentParams = FindNodesByParentParams.create().
            parentPath( parent.path() ).
            size( QueryService.GET_ALL_SIZE_FLAG ).
            build();
        final Nodes children = doFindNodesByParent( findByParentParams ).getNodes();

        int appliedNodeCount = 0;
        for ( Node child : children )
        {
            if ( contextUserHasPermissionOrAdmin( Permission.WRITE_PERMISSIONS, child ) )
            {
                final Node childApplied = applyNodePermissions( parentPermissions, child );
                appliedNodeCount++;
                appliedNodeCount += applyPermissionsToChildren( childApplied );
            }
            else
            {
                LOG.info( "Not enough rights for applying permissions to node [" + child.id() + "] " + child.path() );
            }
        }

        return appliedNodeCount;
    }

    private Node applyNodePermissions( final AccessControlList parentPermissions, final Node node )
    {
        LOG.info( "Applying permissions to node [" + node.id() + "] " + node.path() );
        final Node updatedNode;
        if ( params.isOverwriteChildPermissions() || node.inheritsPermissions() )
        {
            updatedNode = createUpdatedNode( node, parentPermissions, true );
        }
        else
        {
            final AccessControlList mergedPermissions = mergingStrategy.mergePermissions( node.getPermissions(), parentPermissions );
            updatedNode = createUpdatedNode( node, mergedPermissions, false );
        }

        doStoreNode( updatedNode );
        return updatedNode;
    }

    private Node createUpdatedNode( final Node persistedNode, final AccessControlList permissions, final boolean inheritsPermissions )
    {
        final Node.Builder updateNodeBuilder = Node.create( persistedNode ).
            permissions( permissions ).
            inheritPermissions( inheritsPermissions );
        return updateNodeBuilder.build();
    }

    public static Builder create()
    {
        return new Builder();
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
