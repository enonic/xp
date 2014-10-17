package com.enonic.wem.core.identity;

import java.util.List;

import com.enonic.wem.api.identity.Agent;
import com.enonic.wem.api.identity.Group;
import com.enonic.wem.api.identity.Identities;
import com.enonic.wem.api.identity.Identity;
import com.enonic.wem.api.identity.IdentityKey;
import com.enonic.wem.api.identity.IdentityService;
import com.enonic.wem.api.identity.IdentityType;
import com.enonic.wem.api.identity.Realm;
import com.enonic.wem.api.identity.RealmKey;
import com.enonic.wem.api.identity.Realms;
import com.enonic.wem.api.identity.User;

import static java.util.stream.Collectors.toList;

public final class IdentityServiceImpl
    implements IdentityService
{

    private static final RealmKey REALM_1 = new RealmKey( "local" );

    private static final RealmKey REALM_2 = new RealmKey( "file-realm" );

    private final Realms dummyRealms;

    private final Identities dummyIdentities;


    public IdentityServiceImpl()
    {
        this.dummyRealms = createRealms();
        this.dummyIdentities = createIdentities();
    }

    @Override
    public Realms getRealms()
    {
        return this.dummyRealms;
    }

    @Override
    public Identities getIdentities( final RealmKey realm, final IdentityType type )
    {
        final List<Identity> identities = this.dummyIdentities.stream().
            filter( identity -> identity.getIdentityKey().getRealm().equals( realm ) ).
            filter( identity -> identity.getIdentityKey().getType() == type ).
            collect( toList() );

        return Identities.from( identities );
    }

    private Identities createIdentities()
    {
        final User user1 = User.newUser().
            identityKey( IdentityKey.ofUser( REALM_1, "a" ) ).
            displayName( "Alice" ).
            email( "alice@a.org" ).
            login( "alice" ).
            build();

        final User user2 = User.newUser().
            identityKey( IdentityKey.ofUser( REALM_1, "b" ) ).
            displayName( "Bob" ).
            email( "bob@b.org" ).
            login( "bob" ).
            build();

        final Group group1 = Group.newGroup().
            identityKey( IdentityKey.ofUser( REALM_1, "devs" ) ).
            displayName( "Developers" ).
            build();

        final Group group2 = Group.newGroup().
            identityKey( IdentityKey.ofUser( REALM_2, "qa" ) ).
            displayName( "QA" ).
            build();

        final Agent agent = Agent.newAgent().
            identityKey( IdentityKey.ofAgent( REALM_2, "a7" ) ).
            displayName( "Agent" ).
            login( "agent7" ).
            build();

        return Identities.from( user1, user2, group1, group2, agent );
    }

    private Realms createRealms()
    {
        final Realm realm1 = Realm.newRealm().
            key( REALM_1 ).
            name( "Local LDAP" ).
            build();

        final Realm realm2 = Realm.newRealm().
            key( REALM_2 ).
            name( "File based Realm" ).
            build();

        return Realms.from( realm1, realm2 );
    }

}
