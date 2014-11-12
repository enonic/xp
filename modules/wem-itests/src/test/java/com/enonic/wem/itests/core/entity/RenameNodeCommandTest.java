package com.enonic.wem.itests.core.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.core.entity.CreateNodeParams;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodeName;
import com.enonic.wem.core.entity.NodePath;
import com.enonic.wem.core.entity.RenameNodeCommand;
import com.enonic.wem.core.entity.RenameNodeParams;

import static org.junit.Assert.*;

public class RenameNodeCommandTest
    extends AbstractNodeTest
{
    @Override
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
    }

    @Test
    public void rename()
        throws Exception
    {
        final Node createdNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        RenameNodeCommand.create().
            params( RenameNodeParams.create().
                nodeId( createdNode.id() ).
                nodeName( NodeName.from( "my-node-edited" ) ).
                build() ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
            queryService( this.queryService ).
            indexService( this.indexService ).
            build().
            execute();

        final Node renamedNode = getNodeById( createdNode.id() );

        assertEquals( "my-node-edited", renamedNode.name().toString() );
    }

    @Test
    public void rename_with_children()
        throws Exception
    {
        final Node createdNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        final Node child1_1 = createNode( CreateNodeParams.create().
            name( "child1_1" ).
            parent( createdNode.path() ).
            build() );

        final Node child1_2 = createNode( CreateNodeParams.create().
            name( "child1_2" ).
            parent( createdNode.path() ).
            build() );

        final Node child1_1_1 = createNode( CreateNodeParams.create().
            name( "child1_1_1" ).
            parent( child1_1.path() ).
            build() );

        final Node child1_2_1 = createNode( CreateNodeParams.create().
            name( "child1_2_1" ).
            parent( child1_2.path() ).
            build() );

        final Node child1_2_2 = createNode( CreateNodeParams.create().
            name( "child1_2_2" ).
            parent( child1_2.path() ).
            build() );

        refresh();

        RenameNodeCommand.create().
            params( RenameNodeParams.create().
                nodeId( createdNode.id() ).
                nodeName( NodeName.from( "my-node-edited" ) ).
                build() ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            workspaceService( this.workspaceService ).
            queryService( this.queryService ).
            indexService( this.indexService ).
            build().
            execute();

        refresh();

        final Node renamedNode = getNodeById( createdNode.id() );
        assertEquals( "my-node-edited", renamedNode.name().toString() );

        final Node renamedChild1_1 = getNodeById( child1_1.id() );
        assertEquals( renamedNode.path(), renamedChild1_1.parent() );

        final Node renamedChild1_2 = getNodeById( child1_2.id() );
        assertEquals( renamedNode.path(), renamedChild1_2.parent() );

        final Node renamedChild1_1_1 = getNodeById( child1_1_1.id() );
        assertEquals( renamedChild1_1.path(), renamedChild1_1_1.parent() );

        final Node renamedChild1_2_1 = getNodeById( child1_2_1.id() );
        assertEquals( renamedChild1_2.path(), renamedChild1_2_1.parent() );

        final Node renamedChild1_2_2 = getNodeById( child1_2_2.id() );
        assertEquals( renamedChild1_2.path(), renamedChild1_2_2.parent() );
    }
}
