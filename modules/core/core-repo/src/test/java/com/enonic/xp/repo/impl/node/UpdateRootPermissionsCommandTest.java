package com.enonic.xp.repo.impl.node;

import org.junit.Test;

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

import static org.junit.Assert.*;

public class UpdateRootPermissionsCommandTest
    extends AbstractNodeTest
{

    @Test
    public void test_update_permissions()
        throws Exception
    {
        assertNotNull( getNode( Node.ROOT_UUID ) );
        assertNull( anonymousContext().callWith( () -> getNode( Node.ROOT_UUID ) ) );

        UpdateRootPermissionsCommand.create().
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

    @Test(expected = NodeAccessException.class)
    public void update_without_permission()
        throws Exception
    {
        anonymousContext().callWith( () -> UpdateRootPermissionsCommand.create().
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
            execute() );
    }

    private Context anonymousContext()
    {
        return ContextBuilder.from( ContextAccessor.current() ).
            authInfo( AuthenticationInfo.unAuthenticated() ).
            build();
    }


}