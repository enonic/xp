package com.enonic.wem.repo.internal.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.junit.Assert.*;


public class GetNodeByIdCommandTest
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
    public void get_by_id()
        throws Exception
    {
        final CreateNodeParams createNodeParams = CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build();

        final Node createdNode = createNode( createNodeParams );

        final Node fetchedNode = GetNodeByIdCommand.create().
            versionService( this.versionService ).
            indexServiceInternal( this.indexServiceInternal ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            branchService( this.branchService ).
            queryService( this.queryService ).
            id( createdNode.id() ).
            resolveHasChild( false ).
            build().
            execute();

        assertEquals( createdNode, fetchedNode );

        printBranchIndex();
    }

    @Test(expected = NodeNotFoundException.class)
    public void get_by_id_no_access()
        throws Exception
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
            versionService( this.versionService ).
            indexServiceInternal( this.indexServiceInternal ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            branchService( this.branchService ).
            queryService( this.queryService ).
            id( createdNode.id() ).
            resolveHasChild( false ).
            build().
            execute();

        assertNull( fetchedNode );
    }

    @Test
    public void get_by_id_resolve_hasChild()
        throws Exception
    {
        final CreateNodeParams createNodeParams = CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build();

        final Node createdNode = createNode( createNodeParams );

        createNode( CreateNodeParams.create().
            parent( createdNode.path() ).
            name( "child-1" ).
            build() );

        final Node fetchedNode = GetNodeByIdCommand.create().
            versionService( this.versionService ).
            indexServiceInternal( this.indexServiceInternal ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            branchService( this.branchService ).
            queryService( this.queryService ).
            id( createdNode.id() ).
            resolveHasChild( true ).
            build().
            execute();

        assertTrue( fetchedNode.getHasChildren() );

        final Node fetchedNodeSkipResolve = GetNodeByIdCommand.create().
            versionService( this.versionService ).
            indexServiceInternal( this.indexServiceInternal ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            branchService( this.branchService ).
            queryService( this.queryService ).
            id( createdNode.id() ).
            resolveHasChild( false ).
            build().
            execute();

        assertFalse( fetchedNodeSkipResolve.getHasChildren() );
    }

}
