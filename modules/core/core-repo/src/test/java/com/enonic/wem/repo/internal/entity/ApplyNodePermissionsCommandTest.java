package com.enonic.wem.repo.internal.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.ApplyNodePermissionsParams;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.CreateRootNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static com.enonic.xp.security.acl.Permission.CREATE;
import static com.enonic.xp.security.acl.Permission.DELETE;
import static com.enonic.xp.security.acl.Permission.MODIFY;
import static com.enonic.xp.security.acl.Permission.PUBLISH;
import static com.enonic.xp.security.acl.Permission.READ;
import static com.enonic.xp.security.acl.Permission.WRITE_PERMISSIONS;
import static org.junit.Assert.*;


public class ApplyNodePermissionsCommandTest
    extends AbstractNodeTest
{

    public static final UserStoreKey USK = UserStoreKey.system();

    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.createDefaultRootNode();
    }

    @Test
    public void testApplyPermissionsWithOverwrite()
        throws Exception
    {
        runAs( PrincipalKey.ofAnonymous(), this::applyPermissionsWithOverwrite );
    }

    private void applyPermissionsWithOverwrite()
    {
        final PrincipalKey user1 = PrincipalKey.ofUser( USK, "user1" );
        final PrincipalKey user2 = PrincipalKey.ofUser( USK, "user2" );
        final PrincipalKey group1 = PrincipalKey.ofGroup( USK, "group1" );

        final AccessControlList permissions =
            AccessControlList.of( AccessControlEntry.create().principal( PrincipalKey.ofAnonymous() ).allow( READ ).build(),
                                  AccessControlEntry.create().principal( user1 ).allow( READ, MODIFY ).build(),
                                  AccessControlEntry.create().principal( group1 ).allow( READ, CREATE, DELETE, MODIFY ).build() );

        CreateRootNodeCommand.create().
            params( CreateRootNodeParams.create().
                permissions( AccessControlList.create().add(
                    AccessControlEntry.create().principal( TEST_DEFAULT_USER.getKey() ).allowAll().build() ).
                    build() ).
                build() ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

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

        final AccessControlList child1_1_1Permissions = AccessControlList.of(
            AccessControlEntry.create().principal( PrincipalKey.ofAnonymous() ).allow( READ, WRITE_PERMISSIONS ).build(),
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
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
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
    public void testApplyPermissionsWithMerge()
        throws Exception
    {
        runAs( PrincipalKey.ofAnonymous(), this::applyPermissionsWithMerge );
    }

    private void applyPermissionsWithMerge()
    {
        final PrincipalKey user1 = PrincipalKey.ofUser( USK, "user1" );
        final PrincipalKey user2 = PrincipalKey.ofUser( USK, "user2" );
        final PrincipalKey group1 = PrincipalKey.ofGroup( USK, "group1" );
        final AccessControlList permissions = AccessControlList.of(
            AccessControlEntry.create().principal( PrincipalKey.ofAnonymous() ).allow( READ, WRITE_PERMISSIONS ).build(),
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

        final AccessControlList child1_1_1Permissions = AccessControlList.of(
            AccessControlEntry.create().principal( PrincipalKey.ofAnonymous() ).allow( READ, WRITE_PERMISSIONS ).build(),
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
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
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
        assertEquals( "[user:system:anonymous[+read, +write_permissions], " +
                          "group:system:group1[+read, +create, +modify, +delete], " +
                          "user:system:user1[+read, +modify, +delete], " +
                          "user:system:user2[+read, +create, +modify, +delete, +publish]]", child1_1_1Updated.getPermissions().toString() );

        final Node child1_2_1Updated = getNodeById( child1_2_1.id() );
        assertEquals( permissions, child1_2_1Updated.getPermissions() );

        final Node child1_2_2Updated = getNodeById( child1_2_2.id() );
        assertEquals( permissions, child1_2_2Updated.getPermissions() );
    }

    private void runAs( final PrincipalKey principal, final Runnable runnable )
    {
        final Context context = ContextAccessor.current();
        final AuthenticationInfo authInfo = context.getAuthInfo();
        ContextBuilder.from( context ).
            authInfo( AuthenticationInfo.copyOf( authInfo ).principals( principal, PrincipalKey.ofGroup( USK, "group1" ) ).build() ).
            build().
            runWith( runnable );
    }
}
