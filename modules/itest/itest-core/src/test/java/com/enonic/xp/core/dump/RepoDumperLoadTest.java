package com.enonic.xp.core.dump;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.dump.RepoDumper;
import com.enonic.xp.repo.impl.node.NodeHelper;

import static org.junit.jupiter.api.Assertions.assertAll;

class RepoDumperLoadTest
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        createDefaultRootNode();
    }

    @Test
    void load()
    {
        // create a batch of notes greater than elasticsearch can return in a single query
        final int amountOfNodesToLoad = 10_100;

        for ( int i = 0; i < amountOfNodesToLoad; i++ )
        {
            final Node node = createNodeSkipVerification( CreateNodeParams.create().
                name( "node" + i ).
                parent( NodePath.ROOT ).
                build() );
            // commit every node, so number of commits is also greater than elasticsearch can return in a single query
            commit( node );
        }

        refresh();

        final TestDumpWriter writer = new TestDumpWriter();

        doDump( writer );

        assertAll( () -> Assertions.assertEquals( amountOfNodesToLoad, writer.getCommitCount() ),
                   () -> Assertions.assertEquals( amountOfNodesToLoad + 1, writer.getVersionCount() ) );
    }

    private void doDump( final TestDumpWriter writer )
    {
        NodeHelper.runAsAdmin( () -> RepoDumper.create().
            nodeService( this.nodeService ).
            writer( writer ).
            includeBinaries( true ).
            includeVersions( true ).
            repository( this.repositoryService.get( testRepoId ) ).
            build().
            execute() );
    }
}
