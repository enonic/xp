package com.enonic.xp.repo.impl.dump;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.node.AbstractNodeTest;
import com.enonic.xp.repo.impl.node.NodeHelper;

public class RepoDumperLoadTest
    extends AbstractNodeTest
{
    @BeforeEach
    public void setUp()
        throws Exception
    {
        super.setUp();
        createDefaultRootNode();
    }

    @Test
    @Disabled
    public void name()
        throws Exception
    {
        for ( int i = 0; i <= 10_000; i++ )
        {
            createNode( CreateNodeParams.create().
                name( "node" + i ).
                parent( NodePath.ROOT ).
                build(), false );
        }

        refresh();

        final TestDumpWriter writer = new TestDumpWriter();

        doDump( writer );
    }

    private void doDump( final TestDumpWriter writer )
    {
        NodeHelper.runAsAdmin( () -> RepoDumper.create().
            nodeService( this.nodeService ).
            repositoryService( this.repositoryService ).
            writer( writer ).
            includeBinaries( true ).
            includeVersions( true ).
            repositoryId( CTX_DEFAULT.getRepositoryId() ).
            build().
            execute() );
    }
}
