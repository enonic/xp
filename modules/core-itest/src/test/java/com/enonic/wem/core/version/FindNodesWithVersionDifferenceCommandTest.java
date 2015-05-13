package com.enonic.wem.core.version;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.repo.internal.entity.FindNodesWithVersionDifferenceCommand;
import com.enonic.wem.repo.internal.entity.PushNodesCommand;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionDiffQuery;
import com.enonic.xp.node.NodeVersionDiffResult;
import com.enonic.xp.node.PushNodesResult;

import static org.junit.Assert.*;

public class FindNodesWithVersionDifferenceCommandTest
    extends AbstractVersionServiceTest
{
    @Override
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();

        final Node defaultRootNode = createDefaultRootNode();

        doPushNodes( NodeIds.from( defaultRootNode.id() ), WS_OTHER );
    }

    private PushNodesResult doPushNodes( final NodeIds nodeIds, final Branch target )
    {
        return PushNodesCommand.create().
            ids( nodeIds ).
            target( target ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            branchService( this.branchService ).
            indexServiceInternal( this.indexService ).
            build().
            execute();
    }

    @Test
    public void testName()
        throws Exception
    {
        createNode( "s1", NodePath.ROOT );

        final NodeVersionDiffResult result = FindNodesWithVersionDifferenceCommand.create().
            query( NodeVersionDiffQuery.create().
                target( WS_OTHER ).
                source( ContextAccessor.current().getBranch() ).
                nodePath( NodePath.ROOT ).
                build() ).
            versionService( this.versionService ).
            build().
            execute();

        assertEquals( 1, result.getNodesWithDifferences().getSize() );
    }

    private void createNode( final String name, final NodePath parent )
    {
        createNode( CreateNodeParams.create().
            name( name ).
            setNodeId( NodeId.from( name ) ).
            parent( parent ).
            build() );
    }
}
