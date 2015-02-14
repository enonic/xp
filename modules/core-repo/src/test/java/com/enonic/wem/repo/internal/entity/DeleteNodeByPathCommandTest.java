package com.enonic.wem.repo.internal.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.core.node.CreateNodeParams;
import com.enonic.xp.core.node.Node;
import com.enonic.xp.core.node.NodePath;

import static org.junit.Assert.*;

public class DeleteNodeByPathCommandTest
    extends AbstractNodeTest
{
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.createDefaultRootNode();
    }

    @Test
    public void delete_by_path()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "myNode" ).
            parent( NodePath.ROOT ).
            build() );

        final Node child1 = createNode( CreateNodeParams.create().
            name( "child1" ).
            parent( node.path() ).
            build() );

        final Node child2 = createNode( CreateNodeParams.create().
            name( "child2" ).
            parent( node.path() ).
            build() );

        final Node child1_1 = createNode( CreateNodeParams.create().
            name( "child1_1" ).
            parent( child1.path() ).
            build() );

        final Node child1_1_1 = createNode( CreateNodeParams.create().
            name( "child1_1_1" ).
            parent( child1_1.path() ).
            build() );

        DeleteNodeByPathCommand.create().
            nodePath( node.path() ).
            versionService( this.versionService ).
            indexServiceInternal( this.indexServiceInternal ).
            queryService( this.queryService ).
            nodeDao( this.nodeDao ).
            branchService( this.branchService ).
            build().
            execute();

        assertNull( getNodeById( node.id() ) );
        assertNull( getNodeById( child1.id() ) );
        assertNull( getNodeById( child2.id() ) );
        assertNull( getNodeById( child1_1.id() ) );
        assertNull( getNodeById( child1_1_1.id() ) );
    }
}