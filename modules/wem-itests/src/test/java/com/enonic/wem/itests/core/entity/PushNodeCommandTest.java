package com.enonic.wem.itests.core.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextBuilder;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.entity.CreateNodeParams;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodePath;
import com.enonic.wem.core.entity.PushNodeCommand;

import static org.junit.Assert.*;

public class PushNodeCommandTest
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
    public void push_to_other_workspace()
        throws Exception
    {
        final Workspace testWorkspace = Workspace.create().
            name( "test-workspace" ).
            build();

        final Context testContext = ContextBuilder.create().
            workspace( testWorkspace ).
            repositoryId( ContentConstants.CONTENT_REPO.getId() ).
            build();

        final Node node = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() );

        Node testWsNode = testContext.callWith( () -> getNodeById( node.id() ) );

        assertTrue( testWsNode == null );

        PushNodeCommand.create().
            id( node.id() ).
            target( testWorkspace ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
            indexService( this.indexService ).
            build().
            execute();

        testWsNode = testContext.callWith( () -> getNodeById( node.id() ) );

        assertTrue( testWsNode != null );

    }
}
