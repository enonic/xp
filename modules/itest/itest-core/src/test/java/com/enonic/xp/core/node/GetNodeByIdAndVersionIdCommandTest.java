package com.enonic.xp.core.node;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.node.GetNodeByIdAndVersionIdCommand;
import com.enonic.xp.repo.impl.node.GetNodeByIdCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GetNodeByIdAndVersionIdCommandTest
    extends AbstractNodeTest
{

    @Test
    void testExecute_RootNode()
    {
        // Step 1: Try to find ROOT node
        final Node rootNode = GetNodeByIdCommand.create().
            id( Node.ROOT_UUID ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        assertNotNull( rootNode );

        // Step 2: if ROOT node exists, then try to find it by id and versionId
        final Node result = GetNodeByIdAndVersionIdCommand.create().
            nodeId( rootNode.id() ).
            versionId( rootNode.getNodeVersionId() ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        assertNotNull( result );
        assertEquals( rootNode, result );
    }

    @Test
    void testExecute_NonRootNode()
    {
        final CreateNodeParams createNodeParams = CreateNodeParams.create().
            name( "child-node" ).
            parent( NodePath.ROOT ).
            build();

        final Node createdNode = createNode( createNodeParams );

        final Node result = GetNodeByIdAndVersionIdCommand.create().
            nodeId( createdNode.id() ).
            versionId( createdNode.getNodeVersionId() ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        assertNotNull( result );
        assertEquals( createdNode, result );
    }
}
