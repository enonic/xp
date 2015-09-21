package com.enonic.wem.repo.internal.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.junit.Assert.*;

public class GetNodeByPathCommandTest
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
    public void get_by_path()
        throws Exception
    {
        final String nodeName = "my-node";
        final Node createdNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( nodeName ).
            build() );

        final Node fetchedNode = doGetNodeByPath( createdNode.path() );

        assertEquals( createdNode, fetchedNode );
    }

    @Test
    public void get_by_path_no_access()
        throws Exception
    {
        final String nodeName = "my-node";

        final Node createdNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( nodeName ).
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
            build() );

        assertNull( doGetNodeByPath( createdNode.path() ) );
    }

    @Test
    public void get_by_path_fetch_correct()
        throws Exception
    {
        final Node createdNode1 = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node-1" ).
            build() );

        final Node createdNode2 = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node-2" ).
            build() );

        final Node fetchedNode1 = doGetNodeByPath( createdNode1.path() );
        final Node fetchedNode2 = doGetNodeByPath( createdNode2.path() );

        assertEquals( createdNode1, fetchedNode1 );
        assertEquals( createdNode2, fetchedNode2 );
    }

    @Test
    public void get_by_path_not_found()
        throws Exception
    {
        final Node fetchedNode = doGetNodeByPath( NodePath.create( "/dummy" ).build() );
        assertNull( fetchedNode );
    }

    private Node doGetNodeByPath( final NodePath nodePath )
    {
        return GetNodeByPathCommand.create().
            nodePath( nodePath ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }


}
