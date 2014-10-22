package com.enonic.wem.security.internal;

import java.util.List;

import com.enonic.wem.api.security.Group;
import com.enonic.wem.api.security.Principal;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalType;
import com.enonic.wem.api.security.Principals;
import com.enonic.wem.api.security.Role;
import com.enonic.wem.api.security.SecurityService;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.UserStore;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.api.security.UserStores;

import static java.util.stream.Collectors.toList;

public final class SecurityServiceImpl
    implements SecurityService
{

    private static final UserStoreKey REALM_1 = new UserStoreKey( "local" );

    private static final UserStoreKey REALM_2 = new UserStoreKey( "file-store" );

    private final UserStores dummyUserStores;

    private final Principals dummyIdentities;


    public SecurityServiceImpl()
    {
        this.dummyUserStores = createRealms();
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
            userKey( PrincipalKey.ofUser( REALM_1, "a" ) ).
            displayName( "Alice" ).
            email( "alice@a.org" ).
            login( "alice" ).
            build();

        final User user2 = User.newUser().
            userKey( PrincipalKey.ofUser( REALM_1, "b" ) ).
            displayName( "Bob" ).
            email( "bob@b.org" ).
            login( "bob" ).
            build();

        final Group group1 = Group.newGroup().
            groupKey( PrincipalKey.ofUser( REALM_1, "devs" ) ).
            displayName( "Developers" ).
            build();

        final Group group2 = Group.newGroup().
            groupKey( PrincipalKey.ofUser( REALM_2, "qa" ) ).
            displayName( "QA" ).
            build();

        final Role agent = Role.newRole().
            roleKey( PrincipalKey.ofRole( "administrators" ) ).
            displayName( "Administrators" ).
            build();

        return Principals.from( user1, user2, group1, group2, agent );
    }

    private UserStores createRealms()
    {
        final UserStore userStore1 = UserStore.newRealm().
            key( REALM_1 ).
            name( "Local LDAP" ).
            build();

        final UserStore userStore2 = UserStore.newRealm().
            key( REALM_2 ).
            name( "File based Realm" ).
            build();

        return UserStores.from( userStore1, userStore2 );
    }
}
