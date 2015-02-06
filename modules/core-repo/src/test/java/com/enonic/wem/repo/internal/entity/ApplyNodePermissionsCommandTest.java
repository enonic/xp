package com.enonic.wem.repo.internal.entity;

import org.junit.Ignore;
import org.junit.Test;

import com.enonic.wem.api.node.ApplyNodePermissionsParams;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.AccessControlList;

import static com.enonic.wem.api.security.acl.Permission.CREATE;
import static com.enonic.wem.api.security.acl.Permission.DELETE;
import static com.enonic.wem.api.security.acl.Permission.MODIFY;
import static com.enonic.wem.api.security.acl.Permission.PUBLISH;
import static com.enonic.wem.api.security.acl.Permission.READ;
import static org.junit.Assert.*;

@Ignore
public class ApplyNodePermissionsCommandTest
    extends AbstractNodeTest
{

    @Test
    public void applyPermissionsWithOverwrite()
        throws Exception
    {
        final UserStoreKey usk = UserStoreKey.system();
        final PrincipalKey user1 = PrincipalKey.ofUser( usk, "user1" );
        final PrincipalKey user2 = PrincipalKey.ofUser( usk, "user2" );
        final PrincipalKey group1 = PrincipalKey.ofGroup( usk, "group1" );

        final AccessControlList permissions =
            AccessControlList.of( AccessControlEntry.create().principal( PrincipalKey.ofAnonymous() ).allow( READ ).build(),
                                  AccessControlEntry.create().principal( user1 ).allow( READ, MODIFY ).build(),
                                  AccessControlEntry.create().principal( group1 ).allow( READ, CREATE, DELETE, MODIFY ).build() );

        final Node topNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            permissions( permissions ).
            inheritPermissions( false ).
            build() );

        final Node child1_1 = createNode( CreateNodeParams.create().
            name( "child1_1" ).
            parent( topNode.path() ).
            build() );

        final Node child1_2 = createNode( CreateNodeParams.create().
            name( "child1_2" ).
            parent( topNode.path() ).
            build() );

        final AccessControlList child1_1_1Permissions =
            AccessControlList.of( AccessControlEntry.create().principal( PrincipalKey.ofAnonymous() ).allow( READ ).build(),
                                  AccessControlEntry.create().principal( user1 ).allow( READ, MODIFY ).build(),
                                  AccessControlEntry.create().principal( user2 ).allow( READ, CREATE, DELETE, MODIFY, PUBLISH ).build() );
        final Node child1_1_1 = createNode( CreateNodeParams.create().
            name( "child1_1_1" ).
            parent( child1_1.path() ).
            permissions( child1_1_1Permissions ).
            inheritPermissions( false ).
            build() );

        final Node child1_2_1 = createNode( CreateNodeParams.create().
            name( "child1_2_1" ).
            parent( child1_2.path() ).
            build() );

        final Node child1_2_2 = createNode( CreateNodeParams.create().
            name( "child1_2_2" ).
            parent( child1_2.path() ).
            build() );

        refresh();

        final ApplyNodePermissionsParams params = ApplyNodePermissionsParams.create().
            nodeId( topNode.id() ).
            modifier( user1 ).
            overwriteChildPermissions( true ).
            build();

        final int appliedNodeCount = ApplyNodePermissionsCommand.create().
            params( params ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            branchService( this.branchService ).
            queryService( this.queryService ).
            indexService( this.indexService ).
            build().
            execute();

        refresh();

        assertEquals( 5, appliedNodeCount );

        final Node topNodeUpdated = getNodeById( topNode.id() );
        assertEquals( permissions, topNodeUpdated.getPermissions() );

        final Node child1_1Updated = getNodeById( child1_1.id() );
        assertEquals( permissions, child1_1Updated.getPermissions() );

        final Node child1_2Updated = getNodeById( child1_2.id() );
        assertEquals( permissions, child1_2Updated.getPermissions() );

        final Node child1_1_1Updated = getNodeById( child1_1_1.id() );
        assertEquals( permissions, child1_1_1Updated.getPermissions() );

        final Node child1_2_1Updated = getNodeById( child1_2_1.id() );
        assertEquals( permissions, child1_2_1Updated.getPermissions() );

        final Node child1_2_2Updated = getNodeById( child1_2_2.id() );
        assertEquals( permissions, child1_2_2Updated.getPermissions() );
    }

    @Test
    public void applyPermissionsWithMerge()
        throws Exception
    {
        final UserStoreKey usk = UserStoreKey.system();
        final PrincipalKey user1 = PrincipalKey.ofUser( usk, "user1" );
        final PrincipalKey user2 = PrincipalKey.ofUser( usk, "user2" );
        final PrincipalKey group1 = PrincipalKey.ofGroup( usk, "group1" );
        final AccessControlList permissions =
            AccessControlList.of( AccessControlEntry.create().principal( PrincipalKey.ofAnonymous() ).allow( READ ).build(),
                                  AccessControlEntry.create().principal( user1 ).allow( READ, MODIFY ).build(),
                                  AccessControlEntry.create().principal( group1 ).allow( READ, CREATE, DELETE, MODIFY ).build() );

        final AccessControlList initialChildPermissions =
            AccessControlList.of( AccessControlEntry.create().principal( PrincipalKey.ofAnonymous() ).allow( READ ).build() );

        final Node topNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            permissions( permissions ).
            inheritPermissions( false ).
            build() );

        final Node child1_1 = createNode( CreateNodeParams.create().
            name( "child1_1" ).
            parent( topNode.path() ).
            permissions( initialChildPermissions ).
            inheritPermissions( true ).
            build() );

        final Node child1_2 = createNode( CreateNodeParams.create().
            name( "child1_2" ).
            parent( topNode.path() ).
            permissions( initialChildPermissions ).
            inheritPermissions( true ).
            build() );

        final AccessControlList child1_1_1Permissions =
            AccessControlList.of( AccessControlEntry.create().principal( PrincipalKey.ofAnonymous() ).allow( READ ).build(),
                                  AccessControlEntry.create().principal( user1 ).allow( READ, MODIFY, DELETE ).build(),
                                  AccessControlEntry.create().principal( user2 ).allow( READ, CREATE, DELETE, MODIFY, PUBLISH ).build() );
        final Node child1_1_1 = createNode( CreateNodeParams.create().
            name( "child1_1_1" ).
            parent( child1_1.path() ).
            permissions( child1_1_1Permissions ).
            inheritPermissions( false ).
            build() );

        final Node child1_2_1 = createNode( CreateNodeParams.create().
            name( "child1_2_1" ).
            parent( child1_2.path() ).
            permissions( initialChildPermissions ).
            inheritPermissions( true ).
            build() );

        final Node child1_2_2 = createNode( CreateNodeParams.create().
            name( "child1_2_2" ).
            parent( child1_2.path() ).
            permissions( initialChildPermissions ).
            inheritPermissions( true ).
            build() );

        refresh();

        final ApplyNodePermissionsParams params = ApplyNodePermissionsParams.create().
            nodeId( topNode.id() ).
            modifier( user1 ).
            overwriteChildPermissions( false ).
            build();
        final int appliedNodeCount = ApplyNodePermissionsCommand.create().
            params( params ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            branchService( this.branchService ).
            queryService( this.queryService ).
            indexService( this.indexService ).
            build().
            execute();

        refresh();

        assertEquals( 5, appliedNodeCount );

        final Node topNodeUpdated = getNodeById( topNode.id() );
        assertEquals( permissions, topNodeUpdated.getPermissions() );

        final Node child1_1Updated = getNodeById( child1_1.id() );
        assertEquals( permissions, child1_1Updated.getPermissions() );

        final Node child1_2Updated = getNodeById( child1_2.id() );
        assertEquals( permissions, child1_2Updated.getPermissions() );

        final Node child1_1_1Updated = getNodeById( child1_1_1.id() );
        assertEquals( "[user:system:anonymous[+read], " +
                          "group:system:group1[+read, +create, +modify, +delete], " +
                          "user:system:user1[+read, +modify, +delete], " +
                          "user:system:user2[+read, +create, +modify, +delete, +publish]]", child1_1_1Updated.getPermissions().toString() );

        final Node child1_2_1Updated = getNodeById( child1_2_1.id() );
        assertEquals( permissions, child1_2_1Updated.getPermissions() );

        final Node child1_2_2Updated = getNodeById( child1_2_2.id() );
        assertEquals( permissions, child1_2_2Updated.getPermissions() );
    }
}
