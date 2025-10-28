package com.enonic.xp.lib.content;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

class GetPermissionsHandlerTest
    extends BaseContentHandlerTest
{
    @Test
    void testExample()
    {
        final AccessControlList acl = AccessControlList.create().
            add( AccessControlEntry.create().principal( PrincipalKey.ofAnonymous() ).allow( Permission.READ ).build() ).
            build();

        final Content content = TestDataFixtures.newExampleContentBuilder().
            permissions( acl ).
            build();
        Mockito.when( this.contentService.getByPath( Mockito.any() ) ).thenReturn( content );

        runScript( "/lib/xp/examples/content/getPermissions.js" );
    }
}
