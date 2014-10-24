package com.enonic.wem.admin.rest.resource.security;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.security.SecurityService;
import com.enonic.wem.api.security.UserStore;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.api.security.UserStores;

public class SecurityResourceTest
    extends AbstractResourceTest
{

    private SecurityService securityService;

    private static final UserStoreKey USER_STORE_1 = new UserStoreKey( "local" );

    private static final UserStoreKey USER_STORE_2 = new UserStoreKey( "file-store" );

    @Override
    protected Object getResourceInstance()
    {
        securityService = Mockito.mock( SecurityService.class );

        final SecurityResource resource = new SecurityResource();

        securityService = Mockito.mock( SecurityService.class );
        resource.setSecurityService( securityService );

        return resource;
    }

    @Test
    public void get_userStores()
        throws Exception
    {
        final UserStores userStores = createUserStores();

        Mockito.when( securityService.getUserStores() ).
            thenReturn( userStores );

        String jsonString = request().
            path( "userstore/list" ).get().getAsString();

        assertJson( "get_userstores.json", jsonString );
    }

    private UserStores createUserStores()
    {
        final UserStore userStore1 = UserStore.newUserStore().
            key( USER_STORE_1 ).
            displayName( "Local LDAP" ).
            build();

        final UserStore userStore2 = UserStore.newUserStore().
            key( USER_STORE_2 ).
            displayName( "File based user store" ).
            build();

        return UserStores.from( userStore1, userStore2 );
    }
}
