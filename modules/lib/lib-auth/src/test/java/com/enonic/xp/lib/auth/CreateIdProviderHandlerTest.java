package com.enonic.xp.lib.auth;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.security.CreateIdProviderParams;
import com.enonic.xp.security.IdProviderAlreadyExistsException;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreateIdProviderHandlerTest
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
        Mockito.when( securityService.createIdProvider( Mockito.any() ) ).thenReturn( TestDataFixtures.getTestIdProvider() );
        runScript( "/lib/xp/examples/auth/createIdProvider.js" );
    }

    @Test
    void testCreateIdProvider()
    {
        Mockito.when( securityService.createIdProvider( Mockito.any() ) ).thenReturn( TestDataFixtures.getTestIdProvider() );

        runFunction( "/test/createIdProvider-test.js", "createIdProvider" );

        final ArgumentCaptor<CreateIdProviderParams> captor = ArgumentCaptor.forClass( CreateIdProviderParams.class );
        Mockito.verify( securityService ).createIdProvider( captor.capture() );

        final CreateIdProviderParams params = captor.getValue();
        assertThat( params.getKey() ).isEqualTo( IdProviderKey.from( "idProviderTestKey" ) );
        assertThat( params.getDisplayName() ).isEqualTo( "Id Provider test" );
        assertThat( params.getDescription() ).isEqualTo( "Id Provider used for testing" );
        assertThat( params.getIdProviderConfig() ).isNotNull();
        assertThat( params.getIdProviderConfig().getApplicationKey().toString() ).isEqualTo( "com.enonic.app.test" );
        assertThat( params.getIdProviderPermissions() ).isNotNull();
        assertThat( params.getIdProviderPermissions().isEmpty() ).isFalse();
    }

    @Test
    void testCreateIdProviderMinimal()
    {
        Mockito.when( securityService.createIdProvider( Mockito.any() ) ).thenReturn( TestDataFixtures.getTestIdProvider() );

        runFunction( "/test/createIdProvider-test.js", "createIdProviderMinimal" );

        final ArgumentCaptor<CreateIdProviderParams> captor = ArgumentCaptor.forClass( CreateIdProviderParams.class );
        Mockito.verify( securityService ).createIdProvider( captor.capture() );

        final CreateIdProviderParams params = captor.getValue();
        assertThat( params.getKey() ).isEqualTo( IdProviderKey.from( "idProviderTestKey" ) );
        assertThat( params.getDisplayName() ).isEqualTo( "Id Provider test" );
        assertThat( params.getDescription() ).isNull();
        assertThat( params.getIdProviderConfig() ).isNull();
        assertThat( params.getIdProviderPermissions().isEmpty() ).isTrue();
    }

    @Test
    void testCreateIdProviderMissingKey()
    {
        assertThatThrownBy( () -> runFunction( "/test/createIdProvider-test.js", "createIdProviderMissingKey" ) ).hasMessageContaining(
            "key" );
    }

    @Test
    void testCreateIdProviderMissingDisplayName()
    {
        assertThatThrownBy(
            () -> runFunction( "/test/createIdProvider-test.js", "createIdProviderMissingDisplayName" ) ).hasMessageContaining(
            "displayName" );
    }

    @Test
    void testCreateIdProviderDuplicate()
    {
        Mockito.when( securityService.createIdProvider( Mockito.any() ) )
            .thenThrow( new IdProviderAlreadyExistsException( IdProviderKey.from( "idProviderTestKey" ) ) );

        assertThatThrownBy( () -> runFunction( "/test/createIdProvider-test.js", "createIdProvider" ) ).isInstanceOf(
            IdProviderAlreadyExistsException.class );
    }
}
