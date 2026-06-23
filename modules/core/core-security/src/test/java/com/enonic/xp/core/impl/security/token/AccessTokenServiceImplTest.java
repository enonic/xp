package com.enonic.xp.core.impl.security.token;

import java.util.Optional;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.security.CryptoService;
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
        this.service = new AccessTokenServiceImpl( new StubCryptoService( key ) );
    }

    private AccessTokenParams params( final long ttl )
    {
        return AccessTokenParams.create()
            .subject( PrincipalKey.from( "user:myidp:john" ) )
            .idProvider( IdProviderKey.from( "myidp" ) )
            .issuer( "app:myidp" )
            .addAudience( "https://api.example.com" )
            .clientId( "cli" )
            .scope( "openid" )
            .ttlSeconds( ttl )
            .build();
    }

    @Test
    void issue_and_verify_roundtrip()
    {
        final String token = service.issue( params( 3600 ) );
        final Optional<AccessToken> verified = service.verify( token );

        assertTrue( verified.isPresent() );
        final AccessToken at = verified.get();
        assertEquals( PrincipalKey.from( "user:myidp:john" ), at.getSubject() );
        assertEquals( IdProviderKey.from( "myidp" ), at.getIdProvider() );
        assertEquals( "app:myidp", at.getIssuer() );
        assertTrue( at.getAudiences().contains( "https://api.example.com" ) );
        assertEquals( "cli", at.getClaims().get( "client_id" ) );
    }

    @Test
    void verify_rejects_tampered_token()
    {
        final String token = service.issue( params( 3600 ) );
        final String tampered = token.substring( 0, token.lastIndexOf( '.' ) + 1 ) + "AAAdeadbeef";
        assertFalse( service.verify( tampered ).isPresent() );
    }

    @Test
    void verify_rejects_token_signed_with_other_key()
    {
        final String token = service.issue( params( 3600 ) );

        final SecretKey otherKey = newKey();
        final AccessTokenServiceImpl otherService = new AccessTokenServiceImpl( new StubCryptoService( otherKey ) );
        assertFalse( otherService.verify( token ).isPresent() );
    }

    @Test
    void verify_rejects_expired_token()
    {
        final String token = service.issue( params( -10 ) );
        assertFalse( service.verify( token ).isPresent() );
    }

    @Test
    void verify_rejects_unknown_kid()
    {
        // Stub throws for any kid other than the signing kid; simulates the namespace guard.
        final AccessTokenServiceImpl guarded = new AccessTokenServiceImpl( new StubCryptoService( key )
        {
            @Override
            public SecretKey getSigningKey( final String kid )
            {
                throw new IllegalArgumentException( "Unknown signing key id: " + kid );
            }
        } );
        final String token = service.issue( params( 3600 ) );
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

    private static class StubCryptoService
        implements CryptoService
    {
        private final SecretKey key;

        StubCryptoService( final SecretKey key )
        {
            this.key = key;
        }

        @Override
        public String tokenSigningKeyId()
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
