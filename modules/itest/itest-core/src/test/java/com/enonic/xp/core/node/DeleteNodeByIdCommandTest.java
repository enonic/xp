package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.OperationNotPermittedException;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DeleteNodeByIdCommandTest
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void delete()
    {
        final Node createdNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() );

        doDeleteNode( createdNode.id() );

        assertNull( getNodeById( createdNode.id() ) );
    }

    @Test
    void delete_with_children()
    {
        final Node parentNode = createNode( CreateNodeParams.create().parent( NodePath.ROOT ).name( "my-node" ).build() );

        final Node childNode = createNode( CreateNodeParams.create().parent( parentNode.path() ).name( "my-node" ).build() );

        // Deliberately has only DELETE but not READ permission: Node is not visible to user, but user can delete it.
        final Node childChildNode = createNode( CreateNodeParams.create()
                                                    .parent( childNode.path() )
                                                    .name( "my-node" )
                                                    .permissions( AccessControlList.of( AccessControlEntry.create()
                                                                                            .principal( TEST_DEFAULT_USER.getKey() )
                                                                                            .allow( Permission.DELETE )
                                                                                            .build() ) )
                                                    .build() );

        doDeleteNode( parentNode.id() );

        assertNull( ctxDefaultAdmin().callWith( () -> getNodeById( parentNode.id() ) ) );
        assertNull( ctxDefaultAdmin().callWith( () -> getNodeById( childNode.id() ) ) );
        assertNull( ctxDefaultAdmin().callWith( () -> getNodeById( childChildNode.id() ) ) );
    }

    @Test
    void delete_with_children_other_on_level()
    {
        final Node parentNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() );

        final Node childNode = createNode( CreateNodeParams.create().
            parent( parentNode.path() ).
            name( "child1" ).
            build() );

        final Node childNode2 = createNode( CreateNodeParams.create().
            parent( parentNode.path() ).
            name( "child2" ).
            build() );

        final Node childChildNode = createNode( CreateNodeParams.create().
            parent( childNode.path() ).
            name( "child1-1" ).
            build() );

        final Node childChildNode2 = createNode( CreateNodeParams.create().
            parent( childNode2.path() ).
            name( "child2-1" ).
            build() );

        assertNotNull( getNodeById( parentNode.id() ) );
        assertNotNull( getNodeById( childNode.id() ) );
        assertNotNull( getNodeById( childNode2.id() ) );
        assertNotNull( getNodeById( childChildNode.id() ) );
        assertNotNull( getNodeById( childChildNode2.id() ) );

        doDeleteNode( parentNode.id() );

        assertNull( getNodeById( parentNode.id() ) );
        assertNull( getNodeById( childNode.id() ) );
        assertNull( getNodeById( childNode2.id() ) );
        assertNull( getNodeById( childChildNode.id() ) );
        assertNull( getNodeById( childChildNode2.id() ) );
    }

    @Test
    void delete_with_children_require_permission()
    {
        final AccessControlList noDeletePermission = AccessControlList.create().
            add( AccessControlEntry.create().
                allowAll().
                deny( Permission.DELETE ).
                principal( TEST_DEFAULT_USER.getKey() ).
                build() ).
            add( AccessControlEntry.create().
                allowAll().
                deny( Permission.DELETE ).
                principal( RoleKeys.AUTHENTICATED ).
                build() ).
            build();

        final Node parentNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() );

        createNode( CreateNodeParams.create().
            parent( parentNode.path() ).
            name( "my-node" ).
            permissions( noDeletePermission ).
            build() );

        assertThrows(NodeAccessException.class, () -> doDeleteNode( parentNode.id() ));
    }

    @Test
    void delete_with_capital_letter_in_id()
    {
        final Node node = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "myNodeId" ) ).
            name( "myNode" ).
            parent( NodePath.ROOT ).
            build() );

        final Node childNode = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "myChildNodeId" ) ).
            name( "myChildNode" ).
            parent( node.path() ).
            build() );

        final NodeIds nodeIds = doDeleteNode( node.id() );

        assertEquals( 2, nodeIds.getSize() );
        assertNull( getNode( node.id() ) );
        assertNull( getNode( childNode.id() ) );
    }

    @Test
    void cannot_delete_root_node()
    {
        assertThrows(OperationNotPermittedException.class, () -> doDeleteNode( Node.ROOT_UUID ));
    }
}
