package com.enonic.xp.core.index;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.index.ReindexParams;
import com.enonic.xp.index.ReindexResult;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.NodePath;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReindexLoadTest
    extends AbstractNodeTest

{
    @BeforeEach
    public void setUp()
        throws Exception
    {
        createDefaultRootNode();
    }

    @Test
    public void load()
    {
        final int loadSize = 10_100;
        for ( int i = 0; i < loadSize; i++ )
        {
            createNodeSkipVerification( CreateNodeParams.create().
                name( "node" + i ).
                parent( NodePath.ROOT ).
                build() );
        }

        refresh();

        final ReindexResult result = this.indexService.reindex( ReindexParams.create().
            addBranch( WS_DEFAULT ).
            repositoryId( testRepoId ).
            initialize( true ).
            build() );

        assertEquals( loadSize + 1, result.getReindexNodes().getSize() );
    }
}
