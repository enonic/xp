package com.enonic.xp.core.impl.app;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.CreateVirtualApplicationParams;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class VirtualAppService
{
    private final NodeService nodeService;

    public VirtualAppService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    public List<Application> list()
    {
        return VirtualAppContext.createContext().callWith( () -> {
            final FindNodesByParentResult result =
                this.nodeService.findByParent( FindNodesByParentParams.create().parentPath( NodePath.ROOT ).build() );

            final Nodes nodes = nodeService.getByIds( result.getNodeIds() );

            return nodes.stream()
                .map( node -> DynamicResourceManager.appKeyFromNodePath( node.path() ) )
                .map( key -> VirtualAppFactory.create( key, nodeService ) )
                .collect( Collectors.toList() );
        } );
    }

    public Application get( final ApplicationKey applicationKey )
    {
        final NodePath appPath = new NodePath( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT, NodeName.from( applicationKey.toString() ) );
        boolean appExists = VirtualAppContext.createContext().callWith( () -> this.nodeService.nodeExists( appPath ) );

        if ( appExists )
        {
            return VirtualAppFactory.create( applicationKey, nodeService );
        }
        else
        {
            return null;
        }
    }

    public Application create( final CreateVirtualApplicationParams params )
    {
        requireAdminRole();

        VirtualAppContext.createContext().runWith( () -> initVirtualAppNode( params.getKey() ) );

        return VirtualAppFactory.create( params.getKey(), nodeService );
    }

    public boolean delete( final ApplicationKey key )
    {
        requireAdminRole();

        return VirtualAppContext.createContext().callWith( () -> deleteVirtualAppNode( key ) );
    }

    private Node initVirtualAppNode( final ApplicationKey applicationKey )
    {
        final Node virtualAppNode = nodeService.create( CreateNodeParams.create()
                                                            .data( new PropertyTree() )
                                                            .name( applicationKey.toString() )
                                                            .parent( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT )
                                                            .permissions( VirtualAppConstants.VIRTUAL_APP_REPO_DEFAULT_ACL )
                                                            .build() );

        initMixinNode( virtualAppNode.path() );
        initSiteNodes( virtualAppNode.path() );

        nodeService.refresh( RefreshMode.ALL );

        return virtualAppNode;
    }

    private boolean deleteVirtualAppNode( final ApplicationKey applicationKey )
    {
        return nodeService.delete( DeleteNodeParams.create()
                                       .nodePath( new NodePath( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT,
                                                                NodeName.from( applicationKey.toString() ) ) )
                                       .refresh( RefreshMode.ALL )
                                       .build() ).getNodeBranchEntries().isNotEmpty();
    }

    private NodeIds initSiteNodes( final NodePath parent )
    {
        final Node siteRoot = nodeService.create( CreateNodeParams.create()
                                                      .data( new PropertyTree() )
                                                      .name( VirtualAppConstants.SITE_ROOT_NAME )
                                                      .parent( parent )
                                                      .permissions( VirtualAppConstants.VIRTUAL_APP_REPO_DEFAULT_ACL )
                                                      .build() );

        final NodeId contentTypeNodeId = initContentTypeNode( siteRoot.path() );
        final NodeId partNodeId = initPartNode( siteRoot.path() );
        final NodeId layoutNodeId = initLayoutNode( siteRoot.path() );
        final NodeId pageNodeId = initPageNode( siteRoot.path() );
        final NodeId xDataNodeId = initXDataNode( siteRoot.path() );
        final NodeId stylesNodeId = initStylesNode( siteRoot.path() );

        return NodeIds.from( siteRoot.id(), contentTypeNodeId, partNodeId, layoutNodeId, pageNodeId, xDataNodeId, stylesNodeId );
    }

    private NodeId initContentTypeNode( final NodePath parent )
    {
        return nodeService.create( CreateNodeParams.create()
                                       .data( new PropertyTree() )
                                       .name( VirtualAppConstants.CONTENT_TYPE_ROOT_NAME )
                                       .parent( parent )
                                       .permissions( VirtualAppConstants.VIRTUAL_APP_REPO_DEFAULT_ACL )
                                       .build() ).id();
    }

    private NodeId initPartNode( final NodePath parent )
    {
        return nodeService.create( CreateNodeParams.create()
                                       .data( new PropertyTree() )
                                       .name( VirtualAppConstants.PART_ROOT_NAME )
                                       .parent( parent )
                                       .permissions( VirtualAppConstants.VIRTUAL_APP_REPO_DEFAULT_ACL )
                                       .build() ).id();
    }

    private NodeId initLayoutNode( final NodePath parent )
    {
        return nodeService.create( CreateNodeParams.create()
                                       .data( new PropertyTree() )
                                       .name( VirtualAppConstants.LAYOUT_ROOT_NAME )
                                       .parent( parent )
                                       .permissions( VirtualAppConstants.VIRTUAL_APP_REPO_DEFAULT_ACL )
                                       .build() ).id();
    }

    private NodeId initPageNode( final NodePath parent )
    {
        return nodeService.create( CreateNodeParams.create()
                                       .data( new PropertyTree() )
                                       .name( VirtualAppConstants.PAGE_ROOT_NAME )
                                       .parent( parent )
                                       .permissions( VirtualAppConstants.VIRTUAL_APP_REPO_DEFAULT_ACL )
                                       .build() ).id();
    }

    private NodeId initMixinNode( final NodePath parent )
    {
        return nodeService.create( CreateNodeParams.create()
                                       .data( new PropertyTree() )
                                       .name( VirtualAppConstants.MIXIN_ROOT_NAME )
                                       .parent( parent )
                                       .permissions( VirtualAppConstants.VIRTUAL_APP_REPO_DEFAULT_ACL )
                                       .build() ).id();
    }

    private NodeId initXDataNode( final NodePath parent )
    {
        return nodeService.create( CreateNodeParams.create()
                                       .data( new PropertyTree() )
                                       .name( VirtualAppConstants.X_DATA_ROOT_NAME )
                                       .parent( parent )
                                       .permissions( VirtualAppConstants.VIRTUAL_APP_REPO_DEFAULT_ACL )
                                       .build() ).id();
    }

    private NodeId initStylesNode( final NodePath parent )
    {
        return nodeService.create( CreateNodeParams.create()
                                       .data( new PropertyTree() )
                                       .name( VirtualAppConstants.STYLES_ROOT_NAME )
                                       .parent( parent )
                                       .permissions( VirtualAppConstants.VIRTUAL_APP_REPO_DEFAULT_ACL )
                                       .build() ).id();
    }

    private void requireAdminRole()
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        final boolean hasAdminRole = authInfo.hasRole( RoleKeys.ADMIN ) || authInfo.hasRole( RoleKeys.SCHEMA_ADMIN );
        if ( !hasAdminRole )
        {
            throw new ForbiddenAccessException( authInfo.getUser() );
        }
    }
}
