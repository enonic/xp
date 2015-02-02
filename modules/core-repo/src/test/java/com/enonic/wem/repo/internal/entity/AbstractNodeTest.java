package com.enonic.wem.repo.internal.entity;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.context.ContextBuilder;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.FindNodesByParentParams;
import com.enonic.wem.api.node.FindNodesByParentResult;
import com.enonic.wem.api.node.FindNodesByQueryResult;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeIds;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeQuery;
import com.enonic.wem.api.node.PushNodesResult;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.repo.internal.blob.BlobStore;
import com.enonic.wem.repo.internal.blob.file.FileBlobStore;
import com.enonic.wem.repo.internal.elasticsearch.AbstractElasticsearchIntegrationTest;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchIndexService;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchQueryService;
import com.enonic.wem.repo.internal.elasticsearch.version.ElasticsearchVersionService;
import com.enonic.wem.repo.internal.elasticsearch.workspace.ElasticsearchWorkspaceService;
import com.enonic.wem.repo.internal.entity.dao.NodeDaoImpl;
import com.enonic.wem.repo.internal.index.IndexType;
import com.enonic.wem.repo.internal.repository.IndexNameResolver;
import com.enonic.wem.repo.internal.repository.RepositoryInitializer;
import com.enonic.wem.repo.internal.repository.StorageNameResolver;

public abstract class AbstractNodeTest
    extends AbstractElasticsearchIntegrationTest
{
    protected NodeDaoImpl nodeDao;

    protected ElasticsearchVersionService versionService;

    protected ElasticsearchWorkspaceService workspaceService;

    protected ElasticsearchIndexService indexService;

    protected ElasticsearchQueryService queryService;

    protected static final Workspace WS_DEFAULT = Workspace.create().
        name( "stage" ).
        build();

    protected static final Workspace WS_OTHER = Workspace.create().
        name( "prod" ).
        build();

    protected static final Context CTX_DEFAULT = ContextBuilder.create().
        workspace( WS_DEFAULT ).
        repositoryId( TEST_REPO.getId() ).
        build();

    protected static final Context CTX_OTHER = ContextBuilder.create().
        workspace( WS_OTHER ).
        repositoryId( TEST_REPO.getId() ).
        build();

    @Rule
    public TemporaryFolder WEM_HOME = new TemporaryFolder();

    public BlobStore binaryBlobStore;

    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();

        System.setProperty( "wem.home", WEM_HOME.getRoot().getPath() );

        ContextAccessor.INSTANCE.set( CTX_DEFAULT );

        this.binaryBlobStore = new FileBlobStore( "test" );

        this.queryService = new ElasticsearchQueryService();
        this.queryService.setElasticsearchDao( elasticsearchDao );

        this.workspaceService = new ElasticsearchWorkspaceService();
        this.workspaceService.setElasticsearchDao( elasticsearchDao );

        this.versionService = new ElasticsearchVersionService();
        this.versionService.setElasticsearchDao( elasticsearchDao );

        this.indexService = new ElasticsearchIndexService();
        this.indexService.setClient( client );
        this.indexService.setElasticsearchDao( elasticsearchDao );

        this.nodeDao = new NodeDaoImpl();
        this.nodeDao.setWorkspaceService( this.workspaceService );

        createContentRepository();
        waitForClusterHealth();
    }

    void createRepository( final Repository repository )
    {
        NodeServiceImpl nodeService = new NodeServiceImpl();
        nodeService.setIndexService( indexService );
        nodeService.setQueryService( queryService );
        nodeService.setNodeDao( nodeDao );
        nodeService.setVersionService( versionService );
        nodeService.setWorkspaceService( workspaceService );

        RepositoryInitializer repositoryInitializer = new RepositoryInitializer( indexService );
        repositoryInitializer.initializeRepository( repository );

        refresh();
    }

    void createContentRepository()
    {
        createRepository( TEST_REPO );
    }


    protected Node createNode( final CreateNodeParams createNodeParams )
    {
        final Node createdNode = CreateNodeCommand.create().
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
            indexService( this.indexService ).
            versionService( this.versionService ).
            queryService( this.queryService ).
            binaryBlobStore( this.binaryBlobStore ).
            params( createNodeParams ).
            build().
            execute();

        refresh();

        return createdNode;
    }

    Node getNodeById( final NodeId nodeId )
    {
        return GetNodeByIdCommand.create().
            versionService( this.versionService ).
            indexService( this.indexService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
            queryService( this.queryService ).
            id( nodeId ).
            resolveHasChild( false ).
            build().
            execute();
    }

    Node getNodeByPath( final NodePath nodePath )
    {
        return GetNodeByPathCommand.create().
            versionService( this.versionService ).
            indexService( this.indexService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
            queryService( this.queryService ).
            nodePath( nodePath ).
            resolveHasChild( false ).
            build().
            execute();
    }


    FindNodesByParentResult findByParent( final FindNodesByParentParams params )
    {
        return FindNodesByParentCommand.create().
            params( params ).
            queryService( queryService ).
            workspaceService( workspaceService ).
            indexService( indexService ).
            versionService( versionService ).
            nodeDao( nodeDao ).
            build().
            execute();
    }

    public FindNodesByQueryResult doFindByQuery( final NodeQuery query )
    {
        return FindNodesByQueryCommand.create().
            query( query ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
            indexService( this.indexService ).
            build().
            execute();
    }

    void printContentRepoIndex()
    {
        printAllIndexContent( IndexNameResolver.resolveSearchIndexName( TEST_REPO.getId() ), "stage" );
    }

    void printWorkspaceIndex()
    {
        printAllIndexContent( StorageNameResolver.resolveStorageIndexName( CTX_DEFAULT.getRepositoryId() ), IndexType.WORKSPACE.getName() );
    }

    void printVersionIndex()
    {
        printAllIndexContent( StorageNameResolver.resolveStorageIndexName( CTX_DEFAULT.getRepositoryId() ), IndexType.VERSION.getName() );
    }

    protected PushNodesResult pushNodes( final Workspace target, final NodeId... nodeIds )
    {
        return doPushNodes( NodeIds.from( Arrays.asList( nodeIds ) ), target );
    }

    protected PushNodesResult pushNodes( final NodeIds nodeIds, final Workspace target )
    {
        return doPushNodes( nodeIds, target );
    }

    private PushNodesResult doPushNodes( final NodeIds nodeIds, final Workspace target )
    {
        return PushNodesCommand.create().
            ids( nodeIds ).
            target( target ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
            indexService( this.indexService ).
            build().
            execute();
    }

    protected Node doDeleteNode( final NodeId nodeId )
    {
        return DeleteNodeByIdCommand.create().
            nodeId( nodeId ).
            queryService( this.queryService ).
            indexService( this.indexService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
            build().
            execute();
    }
}
