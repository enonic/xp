package com.enonic.xp.repo.impl.node;

import org.junit.jupiter.api.Test;

import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GetNodeByPathAndVersionIdCommandTest
    extends AbstractNodeTest
{

    @Test
    public void testExecute_NonRootNode()
    {
        final CreateNodeParams createNodeParams = CreateNodeParams.create().
            name( "child-node" ).
            parent( NodePath.ROOT ).
            build();

        final Node createdNode = createNode( createNodeParams );

        final Node result = GetNodeByPathAndVersionIdCommand.create().
            nodePath( createdNode.path() ).
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
