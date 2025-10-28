package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.node.GetNodeByIdCommand;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


class GetNodeByIdCommandTest
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void get_rootNode()
    {
        final Node rootNode = GetNodeByIdCommand.create().
            id( Node.ROOT_UUID ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        assertNotNull( rootNode );
        assertNotNull( rootNode.getNodeVersionId() );
    }

    @Test
    void get_by_id()
    {
        final CreateNodeParams createNodeParams = CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build();

        final Node createdNode = createNode( createNodeParams );

        final Node fetchedNode = GetNodeByIdCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            id( createdNode.id() ).
            build().
            execute();

        assertEquals( createdNode, fetchedNode );
    }

    @Test
    void get_by_id_no_access()
    {
        final CreateNodeParams createNodeParams = CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            permissions( AccessControlList.create().
                add( AccessControlEntry.create().
                    deny( Permission.READ ).
                    principal( PrincipalKey.ofAnonymous() ).
                    build() ).
                add( AccessControlEntry.create().
                    allow( Permission.READ ).
                    principal( PrincipalKey.from( "user:system:rmy" ) ).
                    build() ).
                build() ).
            build();

        final Node createdNode = createNode( createNodeParams );

        final Node fetchedNode = GetNodeByIdCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            id( createdNode.id() ).
            build().
            execute();

        assertNull( fetchedNode );
    }
}
