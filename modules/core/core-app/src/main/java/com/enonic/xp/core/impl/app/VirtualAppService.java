package com.enonic.xp.core.impl.app;

import java.util.List;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.CreateVirtualApplicationParams;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.query.expr.DslExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Component(immediate = true, service = VirtualAppService.class)
public class VirtualAppService
{
    private final IndexService indexService;

    private final RepositoryService repositoryService;

    private final NodeService nodeService;

    private final SecurityService securityService;

    @Activate
    public VirtualAppService( @Reference final IndexService indexService, @Reference final RepositoryService repositoryService,
                              @Reference final NodeService nodeService, @Reference SecurityService securityService )
    {
        this.indexService = indexService;
        this.repositoryService = repositoryService;
        this.nodeService = nodeService;
        this.securityService = securityService;
    }

    @Activate
    public void initialize()
    {
        VirtualAppInitializer.create().setIndexService( indexService ).setRepositoryService( repositoryService ).setSecurityService( securityService ).build().initialize();
    }

    public List<Application> list()
    {
        return VirtualAppContext.createContext().callWith( () -> {
            final FindNodesByParentResult result =
                this.nodeService.findByParent( FindNodesByParentParams.create().parentPath( NodePath.ROOT ).build() );

            final Nodes nodes = nodeService.getByIds( result.getNodeIds() );

            return nodes.stream()
                .map( node -> node.path().getElementAsString( 0 ) )
                .map( ApplicationKey::from )
                .map( key -> VirtualAppFactory.create( key, nodeService ) )
                .collect( Collectors.toList() );
        } );
    }

    public Application get( final ApplicationKey applicationKey )
    {
        return VirtualAppContext.createContext().callWith( () -> doGet( applicationKey ) );
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

    private Node initVirtualAppNode( final ApplicationKey key )
    {
        final Node virtualAppNode = nodeService.create( CreateNodeParams.create()
                                                            .data( new PropertyTree() )
                                                            .name( key.toString() )
                                                            .parent( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT )
                                                            .permissions( VirtualAppConstants.VIRTUAL_APP_REPO_DEFAULT_ACL )
                                                            .build() );
        initSiteNodes( virtualAppNode.path() );

        return virtualAppNode;
    }

    private boolean deleteVirtualAppNode( final ApplicationKey key )
    {
        return nodeService.deleteByPath( NodePath.create( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT, key.toString() ).build() )
            .isNotEmpty();
    }

    private Application doGet( final ApplicationKey applicationKey )
    {
        PropertyTree request = new PropertyTree();
        final PropertySet likeExpression = request.addSet( "like" );
        likeExpression.addString( "field", "_path" );
        likeExpression.addString( "value", "/" + applicationKey );

        final FindNodesByQueryResult nodes =
            this.nodeService.findByQuery( NodeQuery.create().query( QueryExpr.from( DslExpr.from( request ) ) ).withPath( true ).build() );
        if ( nodes.getTotalHits() != 0 )
        {
            return VirtualAppFactory.create( applicationKey, nodeService );
        }
        else
        {
            return null;
        }

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
        final NodeId mixinNodeId = initMixinNode( siteRoot.path() );
        final NodeId xDataNodeId = initXDataNode( siteRoot.path() );

        return NodeIds.from( siteRoot.id(), contentTypeNodeId, partNodeId, layoutNodeId, pageNodeId, mixinNodeId, xDataNodeId );
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
