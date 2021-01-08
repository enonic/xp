package com.enonic.xp.repo.impl.index;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.index.ReindexParams;
import com.enonic.xp.index.ReindexResult;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.node.AbstractNodeTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReindexLoadTest
    extends AbstractNodeTest

{
    private IndexServiceImpl indexService;

    private Node rootNode;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.indexService = new IndexServiceImpl();
        this.indexService.setNodeSearchService( this.searchService );
        this.indexService.setIndexServiceInternal( this.indexServiceInternal );
        this.indexService.setNodeVersionService( this.nodeDao );
        this.indexService.setIndexDataService( this.indexedDataService );
        this.indexService.setIndexDataService( this.indexedDataService );
        this.indexService.setRepositoryEntryService( this.repositoryEntryService );

        this.rootNode = this.createDefaultRootNode();
    }

    @Test
    public void load()
    {
        final int loadSize = 10_100;
        for ( int i = 0; i < loadSize; i++ )
        {
            createNode( CreateNodeParams.create().
                name( "node" + i ).
                parent( NodePath.ROOT ).
                build(), false );
        }

        refresh();

        final ReindexResult result = this.indexService.reindex( ReindexParams.create().
            addBranch( WS_DEFAULT ).
            repositoryId( TEST_REPO_ID ).
            initialize( true ).
            build() );

        assertEquals( loadSize + 1, result.getReindexNodes().getSize() );
    }

}
