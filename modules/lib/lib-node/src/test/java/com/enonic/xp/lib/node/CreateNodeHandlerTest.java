package com.enonic.xp.lib.node;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

class CreateNodeHandlerTest
    extends BaseNodeHandlerTest
{
    private void mockCreateNode( final Node node )
    {
        Mockito.when( this.nodeService.create( Mockito.isA( CreateNodeParams.class ) ) ).
            thenReturn( node );
    }

    @Test
    void example_1()
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "likes", "plywood" );
        data.addDouble( "numberOfUselessGadgets", 123.0 );

        final Node node = Node.create().
            id( NodeId.from( "a-random-node-id" ) ).
            data( data ).
            permissions( AccessControlList.create().
                add( AccessControlEntry.create().
                    allowAll().
                    principal( RoleKeys.ADMIN ).
                    build() ).
                build() ).
            build();

        mockCreateNode( node );

        Mockito.when( this.repositoryService.get( RepositoryId.from( "com.enonic.cms.default" ) ) ).
            thenReturn( Repository.create().
                id( RepositoryId.from( "com.enonic.cms.default" ) ).
                branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) ).
                build() );

        runScript( "/lib/xp/examples/node/create-1.js" );
    }

    @Test
    void example_2()
    {
        mockCreateNode( createNode() );

        Mockito.when( this.repositoryService.get( RepositoryId.from( "com.enonic.cms.default" ) ) ).
            thenReturn( Repository.create().
                id( RepositoryId.from( "com.enonic.cms.default" ) ).
                branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) ).
                build() );

        runScript( "/lib/xp/examples/node/create-2.js" );
    }

    @Test
    void example_3()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "displayName", "Child node inheriting permissions" );
        final Node node = Node.create().
            name( "myName" ).
            parentPath( new NodePath( "/parent" ) ).
            id( NodeId.from( "b186d24f-ac38-42ca-a6db-1c1bda6c6c26" ) ).
            data( data ).
            permissions( AccessControlList.create().
                add( AccessControlEntry.create().
                    allowAll().
                    principal( RoleKeys.ADMIN ).
                    build() ).
                add( AccessControlEntry.create().
                    allow( Permission.READ ).
                    principal( RoleKeys.EVERYONE ).
                    build() ).
                add( AccessControlEntry.create().
                    allow( Permission.READ, Permission.MODIFY, Permission.CREATE, Permission.DELETE ).
                    principal( PrincipalKey.ofUser( IdProviderKey.system(), "user1" ) ).
                    build() ).
                build() ).
            build();
        mockCreateNode( node );

        Mockito.when( this.repositoryService.get( RepositoryId.from( "com.enonic.cms.default" ) ) ).
            thenReturn( Repository.create().
                id( RepositoryId.from( "com.enonic.cms.default" ) ).
                branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) ).
                build() );

        runScript( "/lib/xp/examples/node/create-3.js" );
    }
}
