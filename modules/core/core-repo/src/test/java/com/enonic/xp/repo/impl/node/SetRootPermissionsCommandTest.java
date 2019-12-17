package com.enonic.xp.repo.impl.node;

import org.junit.jupiter.api.Test;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SetRootPermissionsCommandTest
    extends AbstractNodeTest
{

    @Test
    public void test_update_permissions()
        throws Exception
    {
        assertNotNull( getNode( Node.ROOT_UUID ) );
        assertNull( anonymousContext().callWith( () -> getNode( Node.ROOT_UUID ) ) );

        SetRootPermissionsCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            permissions( AccessControlList.create().
                add( AccessControlEntry.create().
                    principal( PrincipalKey.ofAnonymous() ).
                    allow( Permission.READ ).
                    build() ).
                build() ).
            build().
            execute();

        assertNotNull( anonymousContext().callWith( () -> getNode( Node.ROOT_UUID ) ) );
    }

    @Test
    public void update_without_permission()
        throws Exception
    {
        assertThrows(NodeAccessException.class, () -> anonymousContext().callWith( () -> SetRootPermissionsCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            permissions( AccessControlList.create().
                add( AccessControlEntry.create().
                    principal( PrincipalKey.ofAnonymous() ).
                    allow( Permission.READ ).
                    build() ).
                build() ).
            build().
            execute() ));
    }

    private Context anonymousContext()
    {
        return ContextBuilder.from( ContextAccessor.current() ).
            authInfo( AuthenticationInfo.unAuthenticated() ).
            build();
    }


}
