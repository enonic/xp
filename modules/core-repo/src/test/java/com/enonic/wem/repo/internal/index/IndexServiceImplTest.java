package com.enonic.wem.repo.internal.index;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.index.PurgeIndexParams;
import com.enonic.wem.api.index.ReindexParams;
import com.enonic.wem.api.index.ReindexResult;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeIds;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.repo.internal.entity.AbstractNodeTest;
import com.enonic.wem.repo.internal.entity.GetNodeByIdCommand;
import com.enonic.wem.repo.internal.entity.PushNodesCommand;

import static org.junit.Assert.*;

public class IndexServiceImplTest
    extends AbstractNodeTest
{
    private IndexServiceImpl indexService;

    private Node rootNode;

    @Override
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.indexService = new IndexServiceImpl();
        this.indexService.setBranchService( this.branchService );
        this.indexService.setIndexServiceInternal( this.indexServiceInternal );
        this.indexService.setNodeDao( this.nodeDao );

        this.rootNode = this.createDefaultRootNode();
    }

    @Test
    public void initialize()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "myNode" ).
            parent( NodePath.ROOT ).
            build() );

        final ReindexResult result = this.indexService.reindex( ReindexParams.create().
            addBranch( CTX_DEFAULT.getBranch() ).
            repositoryId( CTX_DEFAULT.getRepositoryId() ).
            initialize( true ).
            build() );

        assertEquals( 2, result.getReindexNodes().getSize() );

        assertNotNull( getNodeById( node.id() ) );
        assertNotNull( getNodeById( rootNode.id() ) );
    }

    @Test
    public void not_initialize()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "myNode" ).
            parent( NodePath.ROOT ).
            build() );

        final ReindexResult result = this.indexService.reindex( ReindexParams.create().
            addBranch( CTX_DEFAULT.getBranch() ).
            repositoryId( CTX_DEFAULT.getRepositoryId() ).
            initialize( false ).
            build() );

        assertEquals( 2, result.getReindexNodes().getSize() );

        assertNotNull( getNodeById( node.id() ) );
        assertNotNull( getNodeById( rootNode.id() ) );
    }


    @Test
    public void purge_then_reindex()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "myNode" ).
            parent( NodePath.ROOT ).
            build() );

        this.indexService.purgeSearchIndex( new PurgeIndexParams( CTX_DEFAULT.getRepositoryId() ) );

        assertNull( getNodeById( node.id() ) );

        final ReindexResult result = this.indexService.reindex( ReindexParams.create().
            addBranch( CTX_DEFAULT.getBranch() ).
            repositoryId( CTX_DEFAULT.getRepositoryId() ).
            initialize( false ).
            build() );

        assertEquals( 2, result.getReindexNodes().getSize() );

        assertNotNull( getNodeById( node.id() ) );
        assertNotNull( getNodeById( rootNode.id() ) );
    }

    @Test
    public void reindex_other_branch()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "myNode" ).
            parent( NodePath.ROOT ).
            build() );

        PushNodesCommand.create().
            ids( NodeIds.from( node.id() ) ).
            target( CTX_OTHER.getBranch() ).
            indexServiceInternal( this.indexServiceInternal ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            branchService( this.branchService ).
            queryService( this.queryService ).
            build().
            execute();

        assertNotNull( getNodeById( node.id() ) );
        assertNotNull( CTX_OTHER.callWith( () -> getNodeById( node.id() ) ) );

        this.indexService.purgeSearchIndex( new PurgeIndexParams( CTX_DEFAULT.getRepositoryId() ) );

        assertNull( getNodeById( node.id() ) );

        this.indexService.reindex( ReindexParams.create().
            addBranch( CTX_DEFAULT.getBranch() ).
            repositoryId( CTX_DEFAULT.getRepositoryId() ).
            initialize( false ).
            build() );

        assertNotNull( getNodeById( node.id() ) );
        assertNull( CTX_OTHER.callWith( () -> getNodeById( node.id() ) ) );

        this.indexService.reindex( ReindexParams.create().
            addBranch( CTX_OTHER.getBranch() ).
            repositoryId( CTX_DEFAULT.getRepositoryId() ).
            initialize( true ).
            build() );

        assertNull( getNodeById( node.id() ) );
        assertNotNull( CTX_OTHER.callWith( () -> getNodeById( node.id() ) ) );
    }

    private Node getNodeById( final NodeId nodeId )
    {
        return GetNodeByIdCommand.create().
            id( nodeId ).
            indexServiceInternal( this.indexServiceInternal ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            branchService( this.branchService ).
            queryService( this.queryService ).
            build().
            execute();
    }

}