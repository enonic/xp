package com.enonic.xp.core.impl.security.token;

import java.time.Duration;
import java.util.Optional;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.impl.security.TokenSigningKeyService;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.token.AccessToken;
import com.enonic.xp.security.token.AccessTokenParams;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccessTokenServiceImplTest
{
    private AccessTokenServiceImpl service;

    private SecretKey key;

    @BeforeEach
    void setUp()
        throws Exception
    {
        this.key = KeyGenerator.getInstance( "HmacSHA512" ).generateKey();
        this.service = new AccessTokenServiceImpl( new StubTokenSigningKeyService( key ) );
    }

    private AccessTokenParams params( final Duration ttl )
    {
        return AccessTokenParams.create()
            .subject( PrincipalKey.from( "user:myidp:john" ) )
            .issuer( "app:myidp" )
            .addAudience( "https://api.example.com" )
            .clientId( "cli" )
            .scope( "openid" )
            .ttl( ttl )
            .build();
    }

    @Test
    void issue_and_verify_roundtrip()
    {
        final String token = service.issue( params( Duration.ofHours( 1 ) ) );
        final Optional<AccessToken> verified = service.verify( token );

        assertTrue( verified.isPresent() );
        final AccessToken at = verified.get();
        assertEquals( PrincipalKey.from( "user:myidp:john" ), at.subject() );
        assertEquals( IdProviderKey.from( "myidp" ), at.subject().getIdProviderKey() );
        assertEquals( "app:myidp", at.issuer() );
        assertTrue( at.audiences().contains( "https://api.example.com" ) );
        assertEquals( "cli", at.claims().property( "client_id" ).asString() );
    }

    @Test
    void verify_rejects_tampered_token()
    {
        final String token = service.issue( params( Duration.ofHours( 1 ) ) );
        final String tampered = token.substring( 0, token.lastIndexOf( '.' ) + 1 ) + "AAAdeadbeef";
        assertFalse( service.verify( tampered ).isPresent() );
    }

    @Test
    void verify_rejects_token_signed_with_other_key()
    {
        final String token = service.issue( params( Duration.ofHours( 1 ) ) );

        final SecretKey otherKey = newKey();
        final AccessTokenServiceImpl otherService = new AccessTokenServiceImpl( new StubTokenSigningKeyService( otherKey ) );
        assertFalse( otherService.verify( token ).isPresent() );
    }

    @Test
    void verify_rejects_expired_token()
    {
        final String token = service.issue( params( Duration.ofSeconds( -10 ) ) );
        assertFalse( service.verify( token ).isPresent() );
    }

    @Test
    void verify_rejects_unknown_kid()
    {
        // Stub throws for any kid other than the signing kid; simulates the namespace guard.
        final AccessTokenServiceImpl guarded = new AccessTokenServiceImpl( new StubTokenSigningKeyService( key )
        {
            @Override
            public SecretKey getSigningKey( final String kid )
            {
                throw new IllegalArgumentException( "Unknown signing key id: " + kid );
            }
        } );
        final String token = service.issue( params( Duration.ofHours( 1 ) ) );
        assertFalse( guarded.verify( token ).isPresent() );
    }

    private static SecretKey newKey()
    {
        try
        {
            return KeyGenerator.getInstance( "HmacSHA512" ).generateKey();
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private static class StubTokenSigningKeyService
        implements TokenSigningKeyService
    {
        private final SecretKey key;

        StubTokenSigningKeyService( final SecretKey key )
        {
            this.key = key;
        }

        @Override
        public String getCurrentKeyId()
        {
            return "token-signing-hs512";
        }

        @Override
        public SecretKey getSigningKey( final String kid )
        {
            return key;
        }
    }
}
