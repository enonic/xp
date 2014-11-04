package com.enonic.wem.itests.core.entity;

import org.junit.Before;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.core.blob.BlobServiceImpl;
import com.enonic.wem.core.elasticsearch.ElasticsearchIndexService;
import com.enonic.wem.core.elasticsearch.ElasticsearchQueryService;
import com.enonic.wem.core.elasticsearch.ElasticsearchVersionService;
import com.enonic.wem.core.elasticsearch.workspace.ElasticsearchWorkspaceService;
import com.enonic.wem.core.entity.CreateNodeCommand;
import com.enonic.wem.core.entity.CreateNodeParams;
import com.enonic.wem.core.entity.FindNodesByParentCommand;
import com.enonic.wem.core.entity.FindNodesByParentParams;
import com.enonic.wem.core.entity.FindNodesByParentResult;
import com.enonic.wem.core.entity.GetNodeByIdCommand;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodeId;
import com.enonic.wem.core.entity.dao.NodeDaoImpl;
import com.enonic.wem.core.repository.RepositoryInitializer;
import com.enonic.wem.itests.core.MemoryBlobStore;
import com.enonic.wem.itests.core.elasticsearch.AbstractElasticsearchIntegrationTest;

public abstract class AbstractNodeTest
    extends AbstractElasticsearchIntegrationTest
{
    NodeDaoImpl nodeDao;

    ElasticsearchVersionService versionService;

    ElasticsearchWorkspaceService workspaceService;

    ElasticsearchIndexService indexService;

    private BlobServiceImpl blobService;

    ElasticsearchQueryService queryService;

    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();

        this.blobService = new BlobServiceImpl();
        this.blobService.setBlobStore( new MemoryBlobStore() );

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
    }

    void setUpContentRepository()
    {
        RepositoryInitializer repositoryInitializer = new RepositoryInitializer();
        repositoryInitializer.setIndexService( this.indexService );

        repositoryInitializer.init( ContentConstants.CONTENT_REPO );

        refresh();
    }


    Node createNode( final CreateNodeParams createNodeParams )
    {
        final Node createdNode = CreateNodeCommand.create().
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
            indexService( this.indexService ).
            versionService( this.versionService ).
            params( createNodeParams ).
            build().
            execute();

        refresh();

        return createdNode;
    }

    protected Node getNodeById( final NodeId nodeId )
    {
        return GetNodeByIdCommand.create().
            versionService( this.versionService ).
            indexService( this.indexService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
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
}
