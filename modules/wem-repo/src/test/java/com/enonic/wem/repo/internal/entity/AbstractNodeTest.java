package com.enonic.wem.repo.internal.entity;

import org.junit.Before;

import com.enonic.wem.api.blob.BlobService;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.mock.memory.MockBlobService;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.FindNodesByParentParams;
import com.enonic.wem.api.node.FindNodesByParentResult;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.repo.internal.elasticsearch.AbstractElasticsearchIntegrationTest;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchIndexService;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchQueryService;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchVersionService;
import com.enonic.wem.repo.internal.elasticsearch.workspace.ElasticsearchWorkspaceService;
import com.enonic.wem.repo.internal.entity.dao.NodeDaoImpl;
import com.enonic.wem.repo.internal.repository.IndexNameResolver;
import com.enonic.wem.repo.internal.repository.RepositoryInitializerImpl;

public abstract class AbstractNodeTest
    extends AbstractElasticsearchIntegrationTest
{
    NodeDaoImpl nodeDao;

    ElasticsearchVersionService versionService;

    ElasticsearchWorkspaceService workspaceService;

    ElasticsearchIndexService indexService;

    ElasticsearchQueryService queryService;

    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();

        final BlobService blobService = new MockBlobService();

        this.nodeDao = new NodeDaoImpl();
        nodeDao.setBlobService( blobService );

        this.versionService = new ElasticsearchVersionService();
        this.versionService.setElasticsearchDao( elasticsearchDao );

        this.workspaceService = new ElasticsearchWorkspaceService();
        this.workspaceService.setElasticsearchDao( elasticsearchDao );

        this.indexService = new ElasticsearchIndexService();
        this.indexService.setClient( client );
        this.indexService.setElasticsearchDao( elasticsearchDao );

        this.queryService = new ElasticsearchQueryService();
        this.queryService.setElasticsearchDao( elasticsearchDao );

        createContentRepository();
        waitForClusterHealth();
    }

    void createRepository( final Repository repository )
    {
        RepositoryInitializerImpl repositoryInitializer = new RepositoryInitializerImpl();
        repositoryInitializer.setIndexService( this.indexService );
        repositoryInitializer.init( repository );

        refresh();
    }

    void createContentRepository()
    {
        createRepository( ContentConstants.CONTENT_REPO );
    }


    protected Node createNode( final CreateNodeParams createNodeParams )
    {
        final Node createdNode = CreateNodeCommand.create().
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
            indexService( this.indexService ).
            versionService( this.versionService ).
            queryService( this.queryService ).
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

    void printContentRepoIndex()
    {
        printAllIndexContent( IndexNameResolver.resolveSearchIndexName( ContentConstants.CONTENT_REPO.getId() ), "stage" );
    }
}
