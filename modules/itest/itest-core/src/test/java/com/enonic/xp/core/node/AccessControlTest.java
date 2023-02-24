package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AccessControlTest
    extends AbstractNodeTest
{

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.createDefaultRootNode();
    }

    @Test
    public void index_has_read()
        throws Exception
    {
        final AccessControlList aclList = AccessControlList.create().
            add( AccessControlEntry.create().
                principal( PrincipalKey.from( "user:myidprovider:rmy" ) ).
                allow( Permission.READ ).
                build() ).
            add( AccessControlEntry.create().
                principal( PrincipalKey.from( "user:myidprovider:tsi" ) ).
                allow( Permission.READ ).
                build() ).
            build();

        final CreateNodeParams params = CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            permissions( aclList ).
            build();

        this.nodeService.create( params );
    }


    @Test
    public void role_system_admin_can_access_everything()
        throws Exception
    {
        final AccessControlList aclList = AccessControlList.create().
            add( AccessControlEntry.create().
                principal( PrincipalKey.from( "user:myidprovider:rmy" ) ).
                allow( Permission.READ ).
                build() ).
            add( AccessControlEntry.create().
                principal( PrincipalKey.from( "user:myidprovider:tsi" ) ).
                allow( Permission.READ ).
                build() ).
            build();

        final CreateNodeParams params = CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            permissions( aclList ).
            build();

        final Node node = this.nodeService.create( params );

        final Context anonContext = ContextBuilder.from( ctxDefault() ).
            authInfo( AuthenticationInfo.create().
                user( User.ANONYMOUS ).
                build() ).
            build();

        assertNull( anonContext.callWith( () -> getNode( node.id() ) ) );

        final Context anonContextWithAdminUserRole = ContextBuilder.from( ctxDefault() ).
            authInfo( AuthenticationInfo.create().
                principals( RoleKeys.ADMIN ).
                user( User.ANONYMOUS ).
                build() ).
            build();

        assertNotNull( anonContextWithAdminUserRole.callWith( () -> getNode( node.id() ) ) );
    }

}
