package com.enonic.wem.admin.rest.resource.security;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalType;
import com.enonic.wem.api.security.Principals;
import com.enonic.wem.api.security.SecurityService;
import com.enonic.wem.api.security.User;
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

    @Test
    public void get_principals()
        throws Exception
    {
        final UserStores userStores = createUserStores();
        final Principals principals = createPrincipals();
        Mockito.when( securityService.getPrincipals( userStores.get( 0 ).getKey(), PrincipalType.USER ) ).
            thenReturn( principals );

        String jsonString = request().
            path( "userstore/principals" ).queryParam( "type", "user" ).queryParam( "userStoreKey", "local" ).get().getAsString();

        assertJson( "get_principals.json", jsonString );
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

    private Principals createPrincipals()
    {
        final User user1 = User.create().
            key( PrincipalKey.ofUser( USER_STORE_1, "a" ) ).
            displayName( "Alice" ).
            email( "alice@a.org" ).
            login( "alice" ).
            build();

        final User user2 = User.create().
            key( PrincipalKey.ofUser( USER_STORE_2, "b" ) ).
            displayName( "Bobby" ).
            email( "bobby@b.org" ).
            login( "bobby" ).
            build();
        return Principals.from( user1, user2 );
    }
}
