package com.enonic.xp.repo.impl.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.junit.jupiter.api.Assertions.*;

public class NodesHasPermissionResolverTest
    extends AbstractNodeTest
{

    @BeforeEach
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.createDefaultRootNode();
    }


    @Test
    public void single_no_delete_access()
        throws Exception
    {
        final AccessControlList noDeletePermission = AccessControlList.create().
            add( AccessControlEntry.create().
                allowAll().
                deny( Permission.DELETE ).
                principal( TEST_DEFAULT_USER.getKey() ).
                build() ).
            build();

        final Node node = createNode( CreateNodeParams.create().
            name( "myNode" ).
            parent( NodePath.ROOT ).
            permissions( noDeletePermission ).
            build() );

        refresh();

        printContentRepoIndex();

        assertTrue( hasPermission( NodeIds.from( node.id() ), Permission.READ ) );
        assertTrue( hasPermission( NodeIds.from( node.id() ), Permission.CREATE ) );
        assertFalse( hasPermission( NodeIds.from( node.id() ), Permission.DELETE ) );
    }


    @Test
    public void multiple_no_delete_access()
        throws Exception
    {
        final Node node1 = createNode( CreateNodeParams.create().
            name( "myNode1" ).
            parent( NodePath.ROOT ).
            permissions( AccessControlList.create().
                add( AccessControlEntry.create().
                    allowAll().
                    principal( TEST_DEFAULT_USER.getKey() ).
                    build() ).
                build() ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            name( "myNode2" ).
            parent( NodePath.ROOT ).
            permissions( AccessControlList.create().
                add( AccessControlEntry.create().
                    allowAll().
                    deny( Permission.DELETE ).
                    principal( TEST_DEFAULT_USER.getKey() ).
                    build() ).
                build() ).
            build() );

        refresh();

        printContentRepoIndex();

        assertTrue( hasPermission( NodeIds.from( node1.id(), node2.id() ), Permission.READ ) );
        assertTrue( hasPermission( NodeIds.from( node1.id(), node2.id() ), Permission.CREATE ) );
        assertFalse( hasPermission( NodeIds.from( node1.id(), node2.id() ), Permission.DELETE ) );
    }

    private boolean hasPermission( final NodeIds nodeIds, final Permission permission )
    {
        return NodesHasPermissionResolver.create().
            nodeIds( nodeIds ).
            permission( permission ).
            indexServiceInternal( indexServiceInternal ).
            storageService( storageService ).
            searchService( searchService ).
            build().
            execute();
    }
}
