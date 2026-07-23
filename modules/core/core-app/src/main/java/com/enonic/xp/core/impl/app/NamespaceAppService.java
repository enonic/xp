package com.enonic.xp.core.impl.app;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.CreateNamespaceParams;
import com.enonic.xp.app.Namespace;
import com.enonic.xp.app.NamespaceNotFoundException;
import com.enonic.xp.app.UpdateNamespaceParams;
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
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.schema.SchemaNodePropertyNames;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class NamespaceAppService
{
    private final NodeService nodeService;

    public NamespaceAppService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

    public Namespace getNamespace( final ApplicationKey applicationKey )
    {
        final NodePath appPath = new NodePath( NamespaceAppConstants.NAMESPACE_APP_ROOT_PARENT, NodeName.from( applicationKey.toString() ) );

        return NamespaceAppContext.createContext().callWith( () -> {
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
        return NamespaceAppContext.createContext().callWith( () -> {
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

        NamespaceAppContext.createContext().runWith( () -> initNamespaceNode( params ) );

        return Namespace.create().key( params.getKey() ).description( params.getDescription() ).build();
    }

    public Namespace update( final UpdateNamespaceParams params )
    {
        requireAdminRole();

        return NamespaceAppContext.createContext().callWith( () -> {
            final NodePath appPath =
                new NodePath( NamespaceAppConstants.NAMESPACE_APP_ROOT_PARENT, NodeName.from( params.getKey().toString() ) );

            if ( !nodeService.nodeExists( appPath ) )
            {
                throw new NamespaceNotFoundException( params.getKey() );
            }

            final PropertyTree data = new PropertyTree();
            if ( params.getDescription() != null )
            {
                data.setString( "description", params.getDescription() );
            }

            final Node updatedNode = nodeService.update( UpdateNodeParams.create()
                                                             .path( appPath )
                                                             .editor( toBeEdited -> toBeEdited.data = data )
                                                             .refresh( RefreshMode.ALL )
                                                             .build() );

            return Namespace.create().key( params.getKey() ).description( updatedNode.data().getString( "description" ) ).build();
        } );
    }

    public boolean delete( final ApplicationKey key )
    {
        requireAdminRole();

        return NamespaceAppContext.createContext().callWith( () -> deleteNamespaceNode( key ) );
    }

    public void persistApplicationSchema( final ApplicationKey key, final Map<String, String> resources )
    {
        NamespaceAppContext.createAdminContext().runWith( () -> {
            final NodePath appPath = new NodePath( NamespaceAppConstants.NAMESPACE_APP_ROOT_PARENT, NodeName.from( key.toString() ) );

            if ( nodeService.nodeExists( appPath ) )
            {
                nodeService.delete( DeleteNodeParams.create()
                                        .nodePath( new NodePath( appPath, NodeName.from( NamespaceAppConstants.CMS_ROOT_NAME ) ) )
                                        .refresh( RefreshMode.ALL )
                                        .build() );
                initSiteNodes( appPath );
            }
            else
            {
                initNamespaceNode( CreateNamespaceParams.create().key( key ).build() );
            }

            final NodePath cmsPath = new NodePath( appPath, NodeName.from( NamespaceAppConstants.CMS_ROOT_NAME ) );
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

    private Node initNamespaceNode( final CreateNamespaceParams params )
    {
        final PropertyTree data = new PropertyTree();
        if ( params.getDescription() != null )
        {
            data.setString( "description", params.getDescription() );
        }

        final Node namespaceNode = nodeService.create( CreateNodeParams.create()
                                                            .data( data )
                                                            .name( params.getKey().toString() )
                                                            .parent( NamespaceAppConstants.NAMESPACE_APP_ROOT_PARENT )
                                                            .permissions( NamespaceAppConstants.NAMESPACE_APP_REPO_DEFAULT_ACL )
                                                            .build() );
        initSiteNodes( namespaceNode.path() );

        nodeService.refresh( RefreshMode.ALL );

        return namespaceNode;
    }

    private boolean deleteNamespaceNode( final ApplicationKey applicationKey )
    {
        return nodeService.delete( DeleteNodeParams.create()
                                       .nodePath( new NodePath( NamespaceAppConstants.NAMESPACE_APP_ROOT_PARENT, NodeName.from( applicationKey.toString() ) ) )
                                       .refresh( RefreshMode.ALL )
                                       .build() ).getNodeIds().isNotEmpty();
    }

    private NodeIds initSiteNodes( final NodePath parent )
    {
        final Node siteRoot = nodeService.create( CreateNodeParams.create()
                                                      .data( new PropertyTree() )
                                                      .name( NamespaceAppConstants.CMS_ROOT_NAME )
                                                      .parent( parent )
                                                      .permissions( NamespaceAppConstants.NAMESPACE_APP_REPO_DEFAULT_ACL )
                                                      .build() );

        final NodeId contentTypeNodeId = initContentTypeNode( siteRoot.path() );
        final NodeId partNodeId = initPartNode( siteRoot.path() );
        final NodeId layoutNodeId = initLayoutNode( siteRoot.path() );
        final NodeId pageNodeId = initPageNode( siteRoot.path() );
        final NodeId mixinNodeId = initCmsFormFragmentNode( siteRoot.path() );
        final NodeId xDataNodeId = initMixinsNode( siteRoot.path() );
        final NodeId macroNodeId = initMacrosNode( siteRoot.path() );
        final NodeId phrasesNodeId = initPhrasesNode( siteRoot.path() );

        return NodeIds.from( siteRoot.id(), contentTypeNodeId, partNodeId, layoutNodeId, pageNodeId, mixinNodeId, xDataNodeId,
                             macroNodeId, phrasesNodeId );
    }

    private NodeId initContentTypeNode( final NodePath parent )
    {
        return nodeService.create( CreateNodeParams.create()
                                       .data( new PropertyTree() )
                                       .name( NamespaceAppConstants.CONTENT_TYPE_ROOT_NAME )
                                       .parent( parent )
                                       .permissions( NamespaceAppConstants.NAMESPACE_APP_REPO_DEFAULT_ACL )
                                       .build() ).id();
    }

    private NodeId initPartNode( final NodePath parent )
    {
        return nodeService.create( CreateNodeParams.create()
                                       .data( new PropertyTree() )
                                       .name( NamespaceAppConstants.PART_ROOT_NAME )
                                       .parent( parent )
                                       .permissions( NamespaceAppConstants.NAMESPACE_APP_REPO_DEFAULT_ACL )
                                       .build() ).id();
    }

    private NodeId initLayoutNode( final NodePath parent )
    {
        return nodeService.create( CreateNodeParams.create()
                                       .data( new PropertyTree() )
                                       .name( NamespaceAppConstants.LAYOUT_ROOT_NAME )
                                       .parent( parent )
                                       .permissions( NamespaceAppConstants.NAMESPACE_APP_REPO_DEFAULT_ACL )
                                       .build() ).id();
    }

    private NodeId initPageNode( final NodePath parent )
    {
        return nodeService.create( CreateNodeParams.create()
                                       .data( new PropertyTree() )
                                       .name( NamespaceAppConstants.PAGE_ROOT_NAME )
                                       .parent( parent )
                                       .permissions( NamespaceAppConstants.NAMESPACE_APP_REPO_DEFAULT_ACL )
                                       .build() ).id();
    }

    private NodeId initCmsFormFragmentNode( final NodePath parent )
    {
        return nodeService.create( CreateNodeParams.create()
                                       .data( new PropertyTree() )
                                       .name( NamespaceAppConstants.FORM_FRAGMENTS_ROOT_NAME )
                                       .parent( parent )
                                       .permissions( NamespaceAppConstants.NAMESPACE_APP_REPO_DEFAULT_ACL )
                                       .build() ).id();
    }

    private NodeId initMixinsNode( final NodePath parent )
    {
        return nodeService.create( CreateNodeParams.create()
                                       .data( new PropertyTree() )
                                       .name( NamespaceAppConstants.MIXINS_ROOT_NAME )
                                       .parent( parent )
                                       .permissions( NamespaceAppConstants.NAMESPACE_APP_REPO_DEFAULT_ACL )
                                       .build() ).id();
    }

    private NodeId initMacrosNode( final NodePath parent )
    {
        return nodeService.create( CreateNodeParams.create()
                                       .data( new PropertyTree() )
                                       .name( NamespaceAppConstants.MACROS_ROOT_NAME )
                                       .parent( parent )
                                       .permissions( NamespaceAppConstants.NAMESPACE_APP_REPO_DEFAULT_ACL )
                                       .build() ).id();
    }

    private NodeId initPhrasesNode( final NodePath parent )
    {
        final Node i18nNode = nodeService.create( CreateNodeParams.create()
                                                      .data( new PropertyTree() )
                                                      .name( NamespaceAppConstants.I18N_ROOT_NAME )
                                                      .parent( parent )
                                                      .permissions( NamespaceAppConstants.NAMESPACE_APP_REPO_DEFAULT_ACL )
                                                      .build() );

        return nodeService.create( CreateNodeParams.create()
                                       .data( new PropertyTree() )
                                       .name( NamespaceAppConstants.PHRASES_ROOT_NAME )
                                       .parent( i18nNode.path() )
                                       .permissions( NamespaceAppConstants.NAMESPACE_APP_REPO_DEFAULT_ACL )
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
