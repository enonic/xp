package com.enonic.wem.security.internal;

import java.util.List;

import com.enonic.wem.security.Group;
import com.enonic.wem.security.Principal;
import com.enonic.wem.security.PrincipalKey;
import com.enonic.wem.security.PrincipalType;
import com.enonic.wem.security.Principals;
import com.enonic.wem.security.Role;
import com.enonic.wem.security.SecurityService;
import com.enonic.wem.security.User;
import com.enonic.wem.security.UserStore;
import com.enonic.wem.security.UserStoreKey;
import com.enonic.wem.security.UserStores;

import static java.util.stream.Collectors.toList;

public final class SecurityServiceImpl
    implements SecurityService
{

    private static final UserStoreKey USER_STORE_1 = new UserStoreKey( "local" );

    private static final UserStoreKey USER_STORE_2 = new UserStoreKey( "file-store" );

    private final UserStores dummyUserStores;

    private final Principals dummyIdentities;


    public SecurityServiceImpl()
    {
        this.dummyUserStores = createUserStore();
        this.dummyIdentities = createIdentities();
    }

    @Override
    public UserStores getUserStores()
    {
        return this.dummyUserStores;
    }

    @Override
    public Principals getPrincipals( final UserStoreKey useStore, final PrincipalType type )
    {
        final List<Principal> identities = this.dummyIdentities.stream().
            filter( identity -> identity.getKey().getUserStore().equals( useStore ) ).
            filter( identity -> identity.getKey().getType() == type ).
            collect( toList() );

        return Principals.from( identities );
    }

    private Principals createIdentities()
    {
        final User user1 = User.newUser().
            userKey( PrincipalKey.ofUser( USER_STORE_1, "a" ) ).
            displayName( "Alice" ).
            email( "alice@a.org" ).
            login( "alice" ).
            build();

        final User user2 = User.newUser().
            userKey( PrincipalKey.ofUser( USER_STORE_1, "b" ) ).
            displayName( "Bob" ).
            email( "bob@b.org" ).
            login( "bob" ).
            build();

        final Group group1 = Group.newGroup().
            groupKey( PrincipalKey.ofUser( USER_STORE_1, "devs" ) ).
            displayName( "Developers" ).
            build();

        final Group group2 = Group.newGroup().
            groupKey( PrincipalKey.ofUser( USER_STORE_2, "qa" ) ).
            displayName( "QA" ).
            build();

        final Role agent = Role.newRole().
            roleKey( PrincipalKey.ofRole( "administrators" ) ).
            displayName( "Administrators" ).
            build();

        return Principals.from( user1, user2, group1, group2, agent );
    }

    private UserStores createUserStore()
    {
        final UserStore userStore1 = UserStore.newUserStore().
            key( USER_STORE_1 ).
            name( "Local LDAP" ).
            build();

        final UserStore userStore2 = UserStore.newUserStore().
            key( USER_STORE_2 ).
            name( "File based user store" ).
            build();

        return UserStores.from( userStore1, userStore2 );
    }
}
