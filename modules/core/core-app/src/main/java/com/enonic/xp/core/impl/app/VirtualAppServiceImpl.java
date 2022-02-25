package com.enonic.xp.core.impl.app;

import java.util.List;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.VirtualAppService;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.issue.VirtualAppConstants;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.query.expr.DslExpr;
import com.enonic.xp.query.expr.QueryExpr;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;

@Component(immediate = true)
public class VirtualAppServiceImpl
    implements VirtualAppService
{
    private final IndexService indexService;

    private final RepositoryService repositoryService;

    private final NodeService nodeService;

    @Activate
    public VirtualAppServiceImpl( @Reference final IndexService indexService, @Reference final RepositoryService repositoryService,
                                  @Reference final NodeService nodeService )
    {
        this.indexService = indexService;
        this.repositoryService = repositoryService;
        this.nodeService = nodeService;
    }

    @Activate
    public void initialize()
    {
        VirtualAppRepoInitializer.create().setIndexService( indexService ).setRepositoryService( repositoryService ).build().initialize();
    }

//    @Override
//    public void create( final CreateVirtualAppParams params )
//    {
//        VirtualAppContext.createContext().runWith( () -> initVirtualAppNode( params.getRepositoryId() ) );
//    }

    private void initVirtualAppNode( final RepositoryId repositoryId )
    {
        final Node virtualAppNode = nodeService.create( CreateNodeParams.create()
                                                            .data( new PropertyTree() )
                                                            .name( repositoryId.toString() )
                                                            .parent( VirtualAppConstants.VIRTUAL_APP_ROOT_PARENT )
                                                            .permissions( VirtualAppConstants.VIRTUAL_APP_REPO_DEFAULT_ACL )
                                                            .build() );
        initSiteNodes( virtualAppNode.path() );
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

        return NodeIds.from( siteRoot.id(), contentTypeNodeId, partNodeId, layoutNodeId, pageNodeId );
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

    @Override
    public List<Application> list()
    {
        PropertyTree request = new PropertyTree();
        final PropertySet likeExpression = request.addSet( "like" );
        likeExpression.addString( "field", "_path" );
        likeExpression.addString( "value", "/*/" + VirtualAppConstants.SITE_ROOT_NAME + "/*/*/*" );

        return VirtualAppContext.createContext().callWith( () -> {
            final FindNodesByQueryResult nodes = this.nodeService.findByQuery(
                NodeQuery.create().query( QueryExpr.from( DslExpr.from( request ) ) ).withPath( true ).build() );
            return nodes.getNodeHits()
                .stream()
                .map( nodeHit -> nodeHit.getNodePath()
                    .getElementAsString(0)
                     )
                .map( ApplicationKey::from )
                .distinct()
                .map( key -> VirtualAppFactory.create( ApplicationKey.from( "com.enonic.app.test" ), nodeService ) )
                .collect( Collectors.toList() );
        } );
    }

    @Override
    public Application get( final ApplicationKey applicationKey )
    {
        PropertyTree request = new PropertyTree();
        final PropertySet likeExpression = request.addSet( "like" );
        likeExpression.addString( "field", "_path" );
        likeExpression.addString( "value", "/" + applicationKey + "/" + VirtualAppConstants.SITE_ROOT_NAME + "/*/*/*" );

        return VirtualAppContext.createContext().callWith( () -> {
            final FindNodesByQueryResult nodes = this.nodeService.findByQuery(
                NodeQuery.create().query( QueryExpr.from( DslExpr.from( request ) ) ).withPath( true ).build() );
            if ( nodes.getTotalHits() != 0 )
            {
                return VirtualAppFactory.create( applicationKey, nodeService ) ;
            }
            else
            {
                return null;
            }
        } );
    }
}
