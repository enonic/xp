package com.enonic.wem.api.identity;

import org.junit.Test;

import static org.junit.Assert.*;

public class IdentityTest
{

    @Test
    public void testCreateUser()
    {
        final User user = User.newUser().
            login( "userlogin" ).
            displayName( "my user" ).
            identityKey( IdentityKey.ofUser( new RealmKey( "myrealm" ), "userid" ) ).
            email( "user@email" ).
            build();

        assertEquals( "userlogin", user.getLogin() );
        assertEquals( "my user", user.getDisplayName() );
        assertEquals( IdentityKey.from( "myrealm:user:userid" ), user.getIdentityKey() );
        assertEquals( "user@email", user.getEmail() );

        final User userCopy = User.newUser( user ).build();
        assertEquals( "userlogin", userCopy.getLogin() );
        assertEquals( "my user", userCopy.getDisplayName() );
        assertEquals( IdentityKey.from( "myrealm:user:userid" ), userCopy.getIdentityKey() );
        assertEquals( "user@email", userCopy.getEmail() );
    }

    @Test
    public void testCreateGroup()
    {
        final Group group = Group.newGroup().
            displayName( "my group" ).
            identityKey( IdentityKey.ofGroup( new RealmKey( "myrealm" ), "groupid" ) ).
            build();

        assertEquals( "my group", group.getDisplayName() );
        assertEquals( IdentityKey.from( "myrealm:group:groupid" ), group.getIdentityKey() );

        final Group groupCopy = Group.newGroup( group ).build();
        assertEquals( "my group", groupCopy.getDisplayName() );
        assertEquals( IdentityKey.from( "myrealm:group:groupid" ), groupCopy.getIdentityKey() );
    }

    @Test
    public void testGetAnonymous()
    {
        final Anonymous anonymous = Anonymous.get();

        assertTrue( anonymous.getIdentityKey().isAnonymous() );
        assertEquals( "anonymous", anonymous.getDisplayName() );
        assertEquals( IdentityKey.from( "anonymous:anonymous" ), anonymous.getIdentityKey() );
    }

    @Test
    public void testCreateAgent()
    {
        final Agent agent = Agent.newAgent().
            login( "agentlogin" ).
            displayName( "my agent" ).
            identityKey( IdentityKey.ofAgent( new RealmKey( "myrealm" ), "agentid" ) ).
            build();

        assertEquals( "agentlogin", agent.getLogin() );
        assertEquals( "my agent", agent.getDisplayName() );
        assertEquals( IdentityKey.from( "myrealm:agent:agentid" ), agent.getIdentityKey() );

        final Agent groupCopy = Agent.newAgent( agent ).build();
        assertEquals( "agentlogin", groupCopy.getLogin() );
        assertEquals( "my agent", groupCopy.getDisplayName() );
        assertEquals( IdentityKey.from( "myrealm:agent:agentid" ), groupCopy.getIdentityKey() );
    }

}