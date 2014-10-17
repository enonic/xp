package com.enonic.wem.api.identity;

import org.junit.Test;

import static org.junit.Assert.*;

public class IdentityKeyTest
{

    @Test
    public void testUserIdentity()
        throws Exception
    {
        final RealmKey realm = new RealmKey( "myrealm" );
        final IdentityKey user = IdentityKey.ofUser( realm, "userId" );

        assertEquals( "myrealm:user:userId", user.toString() );
        assertEquals( "myrealm", user.getRealm().toString() );
        assertEquals( "userId", user.getId() );
        assertEquals( IdentityType.USER, user.getType() );
        assertTrue( user.isUser() );
        assertFalse( user.isGroup() );
        assertFalse( user.isAnonymous() );
        assertFalse( user.isAgent() );
    }

    @Test
    public void testGroupIdentity()
        throws Exception
    {
        final RealmKey realm = new RealmKey( "myrealm" );
        final IdentityKey group = IdentityKey.ofGroup( realm, "groupid" );

        assertEquals( "myrealm:group:groupid", group.toString() );
        assertEquals( "myrealm", group.getRealm().toString() );
        assertEquals( "groupid", group.getId() );
        assertEquals( IdentityType.GROUP, group.getType() );
        assertTrue( group.isGroup() );
        assertFalse( group.isUser() );
        assertFalse( group.isAnonymous() );
        assertFalse( group.isAgent() );
    }

    @Test
    public void testAgentIdentity()
        throws Exception
    {
        final RealmKey realm = new RealmKey( "myrealm" );
        final IdentityKey agent = IdentityKey.ofAgent( realm, "agentid" );

        assertEquals( "myrealm:agent:agentid", agent.toString() );
        assertEquals( "myrealm", agent.getRealm().toString() );
        assertEquals( "agentid", agent.getId() );
        assertEquals( IdentityType.AGENT, agent.getType() );
        assertTrue( agent.isAgent() );
        assertFalse( agent.isGroup() );
        assertFalse( agent.isUser() );
        assertFalse( agent.isAnonymous() );
    }

    @Test
    public void testAnonymousIdentity()
        throws Exception
    {
        final IdentityKey anonymous = IdentityKey.ofAnonymous();

        assertEquals( "anonymous:anonymous", anonymous.toString() );
        assertNull( anonymous.getRealm() );
        assertEquals( "anonymous", anonymous.getId() );
        assertEquals( IdentityType.ANONYMOUS, anonymous.getType() );
        assertTrue( anonymous.isAnonymous() );
        assertFalse( anonymous.isAgent() );
        assertFalse( anonymous.isGroup() );
        assertFalse( anonymous.isUser() );
    }

    @Test
    public void testIdentityFrom()
        throws Exception
    {
        final IdentityKey anonymous = IdentityKey.from( "anonymous:anonymous" );
        final IdentityKey user = IdentityKey.from( "myrealm:user:myuser" );
        final IdentityKey group = IdentityKey.from( "myrealm:group:mygroup" );
        final IdentityKey agent = IdentityKey.from( "myrealm:agent:myagent" );

        assertEquals( "anonymous:anonymous", anonymous.toString() );
        assertEquals( IdentityKey.ofAnonymous(), anonymous );
        assertEquals( "myrealm:agent:myagent", agent.toString() );
        assertEquals( "myrealm:group:mygroup", group.toString() );
        assertEquals( "myrealm:user:myuser", user.toString() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidIdentityKey()
        throws Exception
    {
        IdentityKey.from( "user:myuser" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidAnonymousKey()
        throws Exception
    {
        IdentityKey.from( "myrealm:anonymous:anonymous" );
    }
}