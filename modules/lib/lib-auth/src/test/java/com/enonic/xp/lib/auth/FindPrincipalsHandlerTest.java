package com.enonic.xp.lib.auth;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalQuery;
import com.enonic.xp.security.PrincipalQueryResult;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.testing.ScriptTestSupport;

class FindPrincipalsHandlerTest
    extends ScriptTestSupport
{
    private SecurityService securityService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        this.securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, this.securityService );
    }

    @Test
    void testExamples()
    {
        final PrincipalQueryResult result = PrincipalQueryResult.create().
            addPrincipal( TestDataFixtures.getTestGroup() ).
            addPrincipal( TestDataFixtures.getTestRole() ).
            addPrincipal( TestDataFixtures.getTestUser() ).
            totalSize( 3 ).
            build();

        Mockito.when( securityService.query( Mockito.any( PrincipalQuery.class ) ) ).thenReturn( result );

        runScript( "/lib/xp/examples/auth/findPrincipals.js" );
    }

    @Test
    void testFindPrincipalsDefaultParameters()
    {
        final PrincipalQuery expectedQuery = PrincipalQuery.create().build();

        final PrincipalQueryResult result = PrincipalQueryResult.create().
            addPrincipal( TestDataFixtures.getTestGroup() ).
            addPrincipal( TestDataFixtures.getTestRole() ).
            addPrincipal( TestDataFixtures.getTestUser() ).
            totalSize( 3 ).
            build();
        Mockito.when( securityService.query( Mockito.eq( expectedQuery ) ) ).thenReturn( result );

        runFunction( "/test/findPrincipals-test.js", "findPrincipalsDefaultParameters" );
    }

    @Test
    void testFindPrincipalsUsers()
    {
        final PrincipalQuery expectedQuery = PrincipalQuery.create().
            includeUsers().
            idProvider( IdProviderKey.from( "enonic" ) ).
            from( 2 ).
            size( 3 ).
            build();

        final PrincipalQueryResult result = PrincipalQueryResult.create().
            addPrincipal( TestDataFixtures.getTestUser() ).
            totalSize( 3 ).
            build();
        Mockito.when( securityService.query( Mockito.eq( expectedQuery ) ) ).thenReturn( result );

        runFunction( "/test/findPrincipals-test.js", "findPrincipalsUsers" );
    }

    @Test
    void testFindPrincipalsGroups()
    {
        final PrincipalQuery expectedQuery = PrincipalQuery.create().
            includeGroups().
            idProvider( IdProviderKey.from( "enonic" ) ).
            from( 2 ).
            size( 3 ).
            build();

        final PrincipalQueryResult result = PrincipalQueryResult.create().
            addPrincipal( TestDataFixtures.getTestGroup() ).
            totalSize( 3 ).
            build();
        Mockito.when( securityService.query( Mockito.eq( expectedQuery ) ) ).thenReturn( result );

        runFunction( "/test/findPrincipals-test.js", "findPrincipalsGroups" );
    }

    @Test
    void testFindPrincipalsRoles()
    {
        final PrincipalQuery expectedQuery = PrincipalQuery.create().
            includeRoles().
            idProvider( IdProviderKey.from( "enonic" ) ).
            from( 2 ).
            size( 3 ).
            build();

        final PrincipalQueryResult result = PrincipalQueryResult.create().
            addPrincipal( TestDataFixtures.getTestRole() ).
            totalSize( 3 ).
            build();
        Mockito.when( securityService.query( Mockito.eq( expectedQuery ) ) ).thenReturn( result );

        runFunction( "/test/findPrincipals-test.js", "findPrincipalsRoles" );
    }

    @Test
    void testFindPrincipalsByName()
    {
        final PrincipalQuery expectedQuery = PrincipalQuery.create().
            idProvider( IdProviderKey.from( "enonic" ) ).
            name( "user1" ).
            build();

        final PrincipalQueryResult result = PrincipalQueryResult.create().
            addPrincipal( TestDataFixtures.getTestUser() ).
            totalSize( 1 ).
            build();
        Mockito.when( securityService.query( Mockito.eq( expectedQuery ) ) ).thenReturn( result );

        runFunction( "/test/findPrincipals-test.js", "findPrincipalsByName" );
    }

    @Test
    void testFindPrincipalsBySearchText()
    {
        final PrincipalQuery expectedQuery = PrincipalQuery.create().
            searchText( "enonic" ).
            build();

        final PrincipalQueryResult result = PrincipalQueryResult.create().
            addPrincipal( TestDataFixtures.getTestUser() ).
            totalSize( 1 ).
            build();
        Mockito.when( securityService.query( Mockito.eq( expectedQuery ) ) ).thenReturn( result );

        runFunction( "/test/findPrincipals-test.js", "findPrincipalsBySearchText" );
    }
}
