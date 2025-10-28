package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.node.GetNodeByPathCommand;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class GetNodeByPathCommandTest
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void get_root()
    {
        final Context systemContext = ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( SystemConstants.SYSTEM_REPO_ID ).
            branch( SystemConstants.BRANCH_SYSTEM ).
            build();

        final Node rootNode = systemContext.callWith( () -> doGetNodeByPath( NodePath.ROOT ) );

        assertNotNull( rootNode );
    }

    @Test
    void get_by_path()
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
    void get_by_path_no_access()
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
    void get_by_path_fetch_correct()
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
    void get_by_path_not_found()
    {
        final Node fetchedNode = doGetNodeByPath( new NodePath( "/dummy" ) );
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
