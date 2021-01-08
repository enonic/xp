package com.enonic.xp.repo.impl.dump;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.node.AbstractNodeTest;
import com.enonic.xp.repo.impl.node.NodeHelper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RepoDumperLoadTest
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
        throws Exception
    {
        // create a batch of notes greater than elasticsearch can return in a single query
        final int amountOfNodesToLoad = 10_100;

        for ( int i = 0; i < amountOfNodesToLoad; i++ )
        {
            final Node node = createNode( CreateNodeParams.create().
                name( "node" + i ).
                parent( NodePath.ROOT ).
                build(), false );
            // commit every node, so number of commits is also greater than elasticsearch can return in a single query
            commit( NodeIds.from( node.id() ) );
        }

        refresh();

        final TestDumpWriter writer = new TestDumpWriter();

        doDump( writer );

        assertAll( () -> assertEquals( amountOfNodesToLoad, writer.getCommitCount() ),
                   () -> assertEquals( amountOfNodesToLoad + 1, writer.getVersionCount() ) );
    }

    private void doDump( final TestDumpWriter writer )
    {
        NodeHelper.runAsAdmin( () -> RepoDumper.create().
            nodeService( this.nodeService ).
            writer( writer ).
            includeBinaries( true ).
            includeVersions( true ).
            repository( this.repositoryService.get( TEST_REPO_ID ) ).
            build().
            execute() );
    }
}
