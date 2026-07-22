package com.enonic.xp.core.impl.app;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.CreateNamespaceParams;
import com.enonic.xp.app.Namespace;
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
import com.enonic.xp.schema.SchemaNodePropertyNames;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class VirtualAppService
{
    private final NodeService nodeService;

    public VirtualAppService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    public Namespace getNamespace( final ApplicationKey applicationKey )
    {
        final NodePath appPath = new NodePath( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT, NodeName.from( applicationKey.toString() ) );

        return VirtualAppContext.createContext().callWith( () -> {
            final Node node = nodeService.getByPath( appPath );
            if ( node == null )
            {
                return null;
            }
            return Namespace.create().key( applicationKey ).description( node.data().getString( "description" ) ).build();
        } );
    }

    public List<Namespace> listNamespaces()
    {
        return VirtualAppContext.createContext().callWith( () -> {
            final FindNodesByParentResult result =
                this.nodeService.findByParent( FindNodesByParentParams.create().parentPath( NodePath.ROOT ).build() );

            final Nodes nodes = nodeService.getByIds( result.getNodeIds() );

            return nodes.stream()
                .map( node -> Namespace.create()
                    .key( DynamicResourceManager.appKeyFromNodePath( node.path() ) )
                    .description( node.data().getString( "description" ) )
                    .build() )
                .collect( Collectors.toList() );
        } );
    }

    public Namespace create( final CreateNamespaceParams params )
    {
        requireAdminRole();

        VirtualAppContext.createContext().runWith( () -> initVirtualAppNode( params ) );

        return Namespace.create().key( params.getKey() ).description( params.getDescription() ).build();
    }

    public boolean delete( final ApplicationKey key )
    {
        requireAdminRole();

        return VirtualAppContext.createContext().callWith( () -> deleteVirtualAppNode( key ) );
    }

    public void persistApplicationSchema( final ApplicationKey key, final Map<String, String> resources )
    {
        VirtualAppContext.createAdminContext().runWith( () -> {
            final NodePath appPath = new NodePath( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT, NodeName.from( key.toString() ) );

            if ( nodeService.nodeExists( appPath ) )
            {
                nodeService.delete( DeleteNodeParams.create()
                                        .nodePath( new NodePath( appPath, NodeName.from( VirtualAppConstants.CMS_ROOT_NAME ) ) )
                                        .refresh( RefreshMode.ALL )
                                        .build() );
                initSiteNodes( appPath );
            }
            else
            {
                initVirtualAppNode( CreateNamespaceParams.create().key( key ).build() );
            }

            final NodePath cmsPath = new NodePath( appPath, NodeName.from( VirtualAppConstants.CMS_ROOT_NAME ) );
            resources.forEach( ( path, content ) -> createResourceNode( cmsPath, path, content ) );

            nodeService.refresh( RefreshMode.ALL );
        } );
    }

    private void createResourceNode( final NodePath cmsPath, final String resourcePath, final String content )
    {
        final String[] elements = resourcePath.split( "/" );

        NodePath parent = cmsPath;
        for ( int i = 0; i < elements.length - 1; i++ )
        {
            final NodePath folderPath = new NodePath( parent, NodeName.from( elements[i] ) );
            if ( !nodeService.nodeExists( folderPath ) )
            {
                nodeService.create( CreateNodeParams.create()
                                        .name( elements[i] )
                                        .parent( parent )
                                        .inheritPermissions( true )
                                        .refresh( RefreshMode.ALL )
                                        .build() );
            }
            parent = folderPath;
        }

        final PropertyTree data = new PropertyTree();
        data.setString( SchemaNodePropertyNames.RESOURCE, content );

        nodeService.create( CreateNodeParams.create()
                                .name( elements[elements.length - 1] )
                                .parent( parent )
                                .data( data )
                                .inheritPermissions( true )
                                .refresh( RefreshMode.ALL )
                                .build() );
    }

    private Node initVirtualAppNode( final CreateNamespaceParams params )
    {
        final PropertyTree data = new PropertyTree();
        if ( params.getDescription() != null )
        {
            data.setString( "description", params.getDescription() );
        }

        final Node virtualAppNode = nodeService.create( CreateNodeParams.create()
                                                            .data( data )
                                                            .name( params.getKey().toString() )
                                                            .parent( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT )
                                                            .permissions( VirtualAppConstants.VIRTUAL_APP_REPO_DEFAULT_ACL )
                                                            .build() );
        initSiteNodes( virtualAppNode.path() );

        nodeService.refresh( RefreshMode.ALL );

        return virtualAppNode;
    }

    private boolean deleteVirtualAppNode( final ApplicationKey applicationKey )
    {
        return nodeService.delete( DeleteNodeParams.create()
                                       .nodePath( new NodePath( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT, NodeName.from( applicationKey.toString() ) ) )
                                       .refresh( RefreshMode.ALL )
                                       .build() ).getNodeIds().isNotEmpty();
    }

    private NodeIds initSiteNodes( final NodePath parent )
    {
        final Node siteRoot = nodeService.create( CreateNodeParams.create()
                                                      .data( new PropertyTree() )
                                                      .name( VirtualAppConstants.CMS_ROOT_NAME )
                                                      .parent( parent )
                                                      .permissions( VirtualAppConstants.VIRTUAL_APP_REPO_DEFAULT_ACL )
                                                      .build() );

        final NodeId contentTypeNodeId = initContentTypeNode( siteRoot.path() );
        final NodeId partNodeId = initPartNode( siteRoot.path() );
        final NodeId layoutNodeId = initLayoutNode( siteRoot.path() );
        final NodeId pageNodeId = initPageNode( siteRoot.path() );
        final NodeId mixinNodeId = initCmsFormFragmentNode( siteRoot.path() );
        final NodeId xDataNodeId = initMixinsNode( siteRoot.path() );
        final NodeId macroNodeId = initMacrosNode( siteRoot.path() );

        return NodeIds.from( siteRoot.id(), contentTypeNodeId, partNodeId, layoutNodeId, pageNodeId, mixinNodeId, xDataNodeId,
                             macroNodeId );
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

    private NodeId initCmsFormFragmentNode( final NodePath parent )
    {
        return nodeService.create( CreateNodeParams.create()
                                       .data( new PropertyTree() )
                                       .name( VirtualAppConstants.FORM_FRAGMENTS_ROOT_NAME )
                                       .parent( parent )
                                       .permissions( VirtualAppConstants.VIRTUAL_APP_REPO_DEFAULT_ACL )
                                       .build() ).id();
    }

    private NodeId initMixinsNode( final NodePath parent )
    {
        return nodeService.create( CreateNodeParams.create()
                                       .data( new PropertyTree() )
                                       .name( VirtualAppConstants.MIXINS_ROOT_NAME )
                                       .parent( parent )
                                       .permissions( VirtualAppConstants.VIRTUAL_APP_REPO_DEFAULT_ACL )
                                       .build() ).id();
    }

    private NodeId initMacrosNode( final NodePath parent )
    {
        return nodeService.create( CreateNodeParams.create()
                                       .data( new PropertyTree() )
                                       .name( VirtualAppConstants.MACROS_ROOT_NAME )
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
