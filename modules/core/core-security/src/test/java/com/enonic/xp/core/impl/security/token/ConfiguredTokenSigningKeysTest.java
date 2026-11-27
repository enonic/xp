package com.enonic.xp.core.impl.security.token;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConfiguredTokenSigningKeysTest
{
    private static final String SECRET_A = Base64.getEncoder().encodeToString( new byte[64] );

    private static final String SECRET_B = Base64.getEncoder().encodeToString( new byte[64] );

    @Test
    void no_keys_means_empty()
    {
        assertThat( ConfiguredTokenSigningKeys.from( Map.of( "auditlog.enabled", "true" ) ) ).isEmpty();
    }

    @Test
    void single_key_signs_without_signingKey_setting()
    {
        final ConfiguredTokenSigningKeys keys = ConfiguredTokenSigningKeys.from( Map.of( "token.keys.a", SECRET_A ) ).orElseThrow();
        assertThat( keys.signingKey().kid() ).isEqualTo( "a" );
        assertThat( keys.verificationKey( "a" ) ).isPresent();
        assertThat( keys.verificationKey( "b" ) ).isEmpty();
    }

    @Test
    void multiple_keys_require_signingKey()
    {
        assertThatThrownBy( () -> ConfiguredTokenSigningKeys.from( Map.of( "token.keys.a", SECRET_A, "token.keys.b", SECRET_B ) ) )
            .isInstanceOf( IllegalArgumentException.class )
            .hasMessageContaining( "token.signingKey is required" );
    }

    @Test
    void signingKey_selects_among_keys_and_all_verify()
    {
        final ConfiguredTokenSigningKeys keys = ConfiguredTokenSigningKeys.from(
            Map.of( "token.keys.a", SECRET_A, "token.keys.b", SECRET_B, "token.signingKey", " b " ) ).orElseThrow();
        assertThat( keys.signingKey().kid() ).isEqualTo( "b" );
        assertThat( keys.verificationKey( "a" ) ).map( SigningKey::kid ).contains( "a" );
        assertThat( keys.verificationKey( "b" ) ).map( SigningKey::kid ).contains( "b" );
    }

    @Test
    void signingKey_must_be_a_configured_key()
    {
        assertThatThrownBy( () -> ConfiguredTokenSigningKeys.from( Map.of( "token.keys.a", SECRET_A, "token.signingKey", "zzz" ) ) )
            .isInstanceOf( IllegalArgumentException.class )
            .hasMessageContaining( "[zzz]" );
        assertThatThrownBy( () -> ConfiguredTokenSigningKeys.from( Map.of( "token.signingKey", "a" ) ) )
            .isInstanceOf( IllegalArgumentException.class )
            .hasMessageContaining( "no token.keys.<kid>" );
    }

    @Test
    void short_or_invalid_secrets_are_rejected()
    {
        for ( int length : new int[]{16, 32, 63} )
        {
            final String tooShort = Base64.getEncoder().encodeToString( new byte[length] );
            assertThatThrownBy( () -> ConfiguredTokenSigningKeys.from( Map.of( "token.keys.a", tooShort ) ) )
                .as( length + " bytes" )
                .isInstanceOf( IllegalArgumentException.class )
                .hasMessageContaining( "too short" );
        }
        assertThat( ConfiguredTokenSigningKeys.from( Map.of( "token.keys.a", Base64.getEncoder().encodeToString( new byte[64] ) ) ) )
            .as( "64 bytes, the HS512 output length" )
            .isPresent();
        assertThatThrownBy( () -> ConfiguredTokenSigningKeys.from( Map.of( "token.keys.a", "not base64 !!" ) ) )
            .isInstanceOf( IllegalArgumentException.class )
            .hasMessageContaining( "not valid base64" );
    }

    @Test
    void secret_is_not_in_toString()
    {
        final Optional<ConfiguredTokenSigningKeys> keys = ConfiguredTokenSigningKeys.from( Map.of( "token.keys.a", SECRET_A ) );
        assertThat( keys.orElseThrow().signingKey().toString() ).isEqualTo( "SigningKey{a}" );
    }
}
