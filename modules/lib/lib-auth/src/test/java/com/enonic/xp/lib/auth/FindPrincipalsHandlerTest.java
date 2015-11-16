package com.enonic.xp.lib.auth;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.PrincipalQuery;
import com.enonic.xp.security.PrincipalQueryResult;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class FindPrincipalsHandlerTest
    extends ScriptTestSupport
{
    private SecurityService securityService;

    @Override
    public void initialize()
    {
        super.initialize();
        this.securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, this.securityService );
    }

    @Test
    public void testFindPrincipalsDefaultParameters()
    {
        final PrincipalQuery expectedQuery = PrincipalQuery.create().build();

        final PrincipalQueryResult result = PrincipalQueryResult.create().
            addPrincipal( TestDataFixtures.getTestGroup() ).
            addPrincipal( TestDataFixtures.getTestRole() ).
            addPrincipal( TestDataFixtures.getTestUser() ).
            totalSize( 3 ).
            build();
        Mockito.when( securityService.query( Mockito.eq( expectedQuery ) ) ).thenReturn( result );

        runFunction( "/site/test/findPrincipals-test.js", "findPrincipalsDefaultParameters" );
    }

    @Test
    public void testFindPrincipalsUsers()
    {
        final PrincipalQuery expectedQuery = PrincipalQuery.create().
            includeUsers().
            userStore( UserStoreKey.from( "enonic" ) ).
            from( 2 ).
            size( 3 ).
            build();

        final PrincipalQueryResult result = PrincipalQueryResult.create().
            addPrincipal( TestDataFixtures.getTestUser() ).
            totalSize( 3 ).
            build();
        Mockito.when( securityService.query( Mockito.eq( expectedQuery ) ) ).thenReturn( result );

        runFunction( "/site/test/findPrincipals-test.js", "findPrincipalsUsers" );
    }

    @Test
    public void testFindPrincipalsGroups()
    {
        final PrincipalQuery expectedQuery = PrincipalQuery.create().
            includeGroups().
            userStore( UserStoreKey.from( "enonic" ) ).
            from( 2 ).
            size( 3 ).
            build();

        final PrincipalQueryResult result = PrincipalQueryResult.create().
            addPrincipal( TestDataFixtures.getTestGroup() ).
            totalSize( 3 ).
            build();
        Mockito.when( securityService.query( Mockito.eq( expectedQuery ) ) ).thenReturn( result );

        runFunction( "/site/test/findPrincipals-test.js", "findPrincipalsGroups" );
    }

    @Test
    public void testFindPrincipalsRoles()
    {
        final PrincipalQuery expectedQuery = PrincipalQuery.create().
            includeRoles().
            userStore( UserStoreKey.from( "enonic" ) ).
            from( 2 ).
            size( 3 ).
            build();

        final PrincipalQueryResult result = PrincipalQueryResult.create().
            addPrincipal( TestDataFixtures.getTestRole() ).
            totalSize( 3 ).
            build();
        Mockito.when( securityService.query( Mockito.eq( expectedQuery ) ) ).thenReturn( result );

        runFunction( "/site/test/findPrincipals-test.js", "findPrincipalsRoles" );
    }

    @Test
    public void testFindPrincipalsByName()
    {
        final PrincipalQuery expectedQuery = PrincipalQuery.create().
            userStore( UserStoreKey.from( "enonic" ) ).
            name( "user1" ).
            build();

        final PrincipalQueryResult result = PrincipalQueryResult.create().
            addPrincipal( TestDataFixtures.getTestUser() ).
            totalSize( 1 ).
            build();
        Mockito.when( securityService.query( Mockito.eq( expectedQuery ) ) ).thenReturn( result );

        runFunction( "/site/test/findPrincipals-test.js", "findPrincipalsByName" );
    }

    @Test
    public void testFindPrincipalsByEmail()
    {
        final PrincipalQuery expectedQuery = PrincipalQuery.create().
            userStore( UserStoreKey.from( "enonic" ) ).
            email( "user1@enonic.com" ).
            build();

        final PrincipalQueryResult result = PrincipalQueryResult.create().
            addPrincipal( TestDataFixtures.getTestUser() ).
            totalSize( 1 ).
            build();
        Mockito.when( securityService.query( Mockito.eq( expectedQuery ) ) ).thenReturn( result );

        runFunction( "/site/test/findPrincipals-test.js", "findPrincipalsByEmail" );
    }

    @Test
    public void testFindPrincipalsByDisplayName()
    {
        final PrincipalQuery expectedQuery = PrincipalQuery.create().
            userStore( UserStoreKey.from( "enonic" ) ).
            displayName( "User 1" ).
            build();

        final PrincipalQueryResult result = PrincipalQueryResult.create().
            addPrincipal( TestDataFixtures.getTestUser() ).
            totalSize( 1 ).
            build();
        Mockito.when( securityService.query( Mockito.eq( expectedQuery ) ) ).thenReturn( result );

        runFunction( "/site/test/findPrincipals-test.js", "findPrincipalsByDisplayName" );
    }

    @Test
    public void testFindPrincipalsBySearchText()
    {
        final PrincipalQuery expectedQuery = PrincipalQuery.create().
            searchText( "enonic" ).
            build();

        final PrincipalQueryResult result = PrincipalQueryResult.create().
            addPrincipal( TestDataFixtures.getTestUser() ).
            totalSize( 1 ).
            build();
        Mockito.when( securityService.query( Mockito.eq( expectedQuery ) ) ).thenReturn( result );

        runFunction( "/site/test/findPrincipals-test.js", "findPrincipalsBySearchText" );
    }
}
