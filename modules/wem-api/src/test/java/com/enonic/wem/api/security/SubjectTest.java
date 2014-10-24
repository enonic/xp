package com.enonic.wem.api.security;

import org.junit.Test;

import static org.junit.Assert.*;

public class SubjectTest
{
    @Test
    public void testSubjectWithoutPrincipals()
    {
        final User user = User.newUser().
            login( "userlogin" ).
            displayName( "my user" ).
            userKey( PrincipalKey.ofUser( new UserStoreKey( "myuserstore" ), "userid" ) ).
            email( "user@email" ).
            build();

        final Subject subject = Subject.newSubject().user( user ).build();

        assertEquals( "userlogin", subject.getUser().getLogin() );
        assertEquals( "my user", subject.getUser().getDisplayName() );
        assertEquals( PrincipalKey.from( "myuserstore:user:userid" ), subject.getUser().getKey() );
        assertEquals( 1, subject.getPrincipals().getSize() );
        assertEquals( user.getKey(), subject.getPrincipals().first() );
        assertFalse( subject.hasRole( "userid" ) );
    }

    @Test
    public void testSubjectWithPrincipals()
    {
        final User user = User.newUser().
            login( "userlogin" ).
            displayName( "my user" ).
            userKey( PrincipalKey.ofUser( new UserStoreKey( "myuserstore" ), "userid" ) ).
            email( "user@email" ).
            build();

        final UserStoreKey userStore = new UserStoreKey( "myStore" );
        final PrincipalKey group1 = PrincipalKey.ofGroup( userStore, "group1" );
        final PrincipalKey group2 = PrincipalKey.from( "myStore:group:group2" );
        final PrincipalKey role1 = PrincipalKey.from( "system:role:administrators" );
        final Subject subject = Subject.newSubject().
            user( user ).
            principal( group1 ).
            principals( PrincipalKeys.from( group2, role1 ) ).
            build();

        assertEquals( "userlogin", subject.getUser().getLogin() );
        assertEquals( "my user", subject.getUser().getDisplayName() );
        assertEquals( PrincipalKey.from( "myuserstore:user:userid" ), subject.getUser().getKey() );
        assertEquals( 4, subject.getPrincipals().getSize() );
        assertTrue( subject.getPrincipals().contains( PrincipalKey.from( "myuserstore:user:userid" ) ) );
        assertTrue( subject.getPrincipals().contains( group1 ) );
        assertTrue( subject.getPrincipals().contains( group2 ) );
        assertTrue( subject.getPrincipals().contains( role1 ) );
        assertFalse( subject.hasRole( "userid" ) );
        assertFalse( subject.hasRole( "group1" ) );
        assertFalse( subject.hasRole( "group2" ) );
        assertTrue( subject.hasRole( "administrators" ) );
    }

}