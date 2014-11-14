package com.enonic.wem.itests.core.entity;

import org.junit.Before;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.core.elasticsearch.ElasticsearchIndexService;
import com.enonic.wem.core.elasticsearch.ElasticsearchQueryService;
import com.enonic.wem.core.elasticsearch.ElasticsearchVersionService;
import com.enonic.wem.core.elasticsearch.workspace.ElasticsearchWorkspaceService;
import com.enonic.wem.core.entity.CreateNodeCommand;
import com.enonic.wem.core.entity.FindNodesByParentCommand;
import com.enonic.wem.core.entity.GetNodeByIdCommand;
import com.enonic.wem.core.entity.dao.NodeDaoImpl;
import com.enonic.wem.core.index.IndexType;
import com.enonic.wem.core.repository.IndexNameResolver;
import com.enonic.wem.core.repository.RepositoryInitializerImpl;
import com.enonic.wem.core.repository.StorageNameResolver;
import com.enonic.wem.internal.blob.BlobServiceImpl;
import com.enonic.wem.itests.core.MemoryBlobStore;
import com.enonic.wem.itests.core.elasticsearch.AbstractElasticsearchIntegrationTest;
import com.enonic.wem.repo.CreateNodeParams;
import com.enonic.wem.repo.FindNodesByParentParams;
import com.enonic.wem.repo.FindNodesByParentResult;
import com.enonic.wem.repo.Node;
import com.enonic.wem.repo.NodeId;

public abstract class AbstractNodeTest
    extends AbstractElasticsearchIntegrationTest
{
    protected NodeDaoImpl nodeDao;

    protected ElasticsearchVersionService versionService;

    protected ElasticsearchWorkspaceService workspaceService;

    protected ElasticsearchIndexService indexService;

    private BlobServiceImpl blobService;

    protected ElasticsearchQueryService queryService;

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

        createContentRepository();
        waitForClusterHealth();
    }

    protected void createRepository( final Repository repository )
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


    public Node createNode( final CreateNodeParams createNodeParams )
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

    protected Node getNodeById( final NodeId nodeId )
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

    protected void printContentRepoIndex()
    {
        printAllIndexContent( IndexNameResolver.resolveSearchIndexName( ContentConstants.CONTENT_REPO.getId() ), "stage" );
    }

    protected void printVersionIndex()
    {
        printAllIndexContent( StorageNameResolver.resolveStorageIndexName( ContentConstants.CONTENT_REPO.getId() ),
                              IndexType.VERSION.getName() );
    }


}
