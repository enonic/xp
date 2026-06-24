package com.enonic.xp.core.impl.security;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.security.KeyStore;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TokenSigningKeyServiceImplTest
{
    private static final char[] PASSWORD = "secret-pass".toCharArray();

    @TempDir
    Path tempDir;

    @Test
    void resolves_the_configured_signing_key()
        throws Exception
    {
        final Path keystore = tempDir.resolve( "tokens.p12" );
        final SecretKey key = newKey();
        writeKeystore( keystore, Map.of( "sign-1", key ) );

        final TokenSigningKeyServiceImpl service = service( keystore, "sign-1" );

        assertEquals( "sign-1", service.getCurrentKeyId() );
        assertArrayEquals( key.getEncoded(), service.getSigningKey( "sign-1" ).getEncoded() );
    }

    @Test
    void verification_resolves_any_alias_in_the_keystore()
        throws Exception
    {
        final Path keystore = tempDir.resolve( "tokens.p12" );
        final SecretKey signing = newKey();
        final SecretKey other = newKey();
        writeKeystore( keystore, Map.of( "sign-1", signing, "sign-2", other ) );

        final TokenSigningKeyServiceImpl service = service( keystore, "sign-1" );

        // A token's kid resolves to its alias regardless of which one is the signing alias.
        assertArrayEquals( other.getEncoded(), service.getSigningKey( "sign-2" ).getEncoded() );
    }

    @Test
    void unknown_alias_is_rejected()
        throws Exception
    {
        final Path keystore = tempDir.resolve( "tokens.p12" );
        writeKeystore( keystore, Map.of( "sign-1", newKey() ) );

        final TokenSigningKeyServiceImpl service = service( keystore, "sign-1" );

        assertThrows( IllegalArgumentException.class, () -> service.getSigningKey( "removed" ) );
    }

    @Test
    void invalid_alias_is_rejected()
        throws Exception
    {
        final Path keystore = tempDir.resolve( "tokens.p12" );
        writeKeystore( keystore, Map.of( "sign-1", newKey() ) );

        final TokenSigningKeyServiceImpl service = service( keystore, "sign-1" );

        assertThrows( IllegalArgumentException.class, () -> service.getSigningKey( "../evil" ) );
    }

    @Test
    void missing_signing_alias_fails()
        throws Exception
    {
        final Path keystore = tempDir.resolve( "tokens.p12" );
        writeKeystore( keystore, Map.of( "sign-1", newKey() ) );

        final TokenSigningKeyServiceImpl service = service( keystore, "not-there" );

        assertThrows( IllegalStateException.class, service::getCurrentKeyId );
    }

    @Test
    void rotate_and_decommission_are_unsupported()
        throws Exception
    {
        final Path keystore = tempDir.resolve( "tokens.p12" );
        writeKeystore( keystore, Map.of( "sign-1", newKey() ) );

        final TokenSigningKeyServiceImpl service = service( keystore, "sign-1" );

        assertThrows( UnsupportedOperationException.class, service::rotate );
        assertThrows( UnsupportedOperationException.class, () -> service.decommission( "sign-1" ) );
    }

    @Test
    void reloads_when_the_keystore_file_changes()
        throws Exception
    {
        final Path keystore = tempDir.resolve( "tokens.p12" );
        writeKeystore( keystore, Map.of( "sign-1", newKey() ) );

        final TokenSigningKeyServiceImpl service = service( keystore, "sign-1" );
        assertThrows( IllegalArgumentException.class, () -> service.getSigningKey( "sign-2" ) );

        final SecretKey added = newKey();
        final Map<String, SecretKey> updated = new LinkedHashMap<>();
        updated.put( "sign-1", newKey() );
        updated.put( "sign-2", added );
        writeKeystore( keystore, updated );
        Files.setLastModifiedTime( keystore, FileTime.fromMillis( Files.getLastModifiedTime( keystore ).toMillis() + 5000 ) );

        assertArrayEquals( added.getEncoded(), service.getSigningKey( "sign-2" ).getEncoded() );
    }

    private TokenSigningKeyServiceImpl service( final Path keystore, final String signingAlias )
    {
        final SecurityConfig config = mock( SecurityConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( config.tokenSigningKeystore() ).thenReturn( keystore.toString() );
        when( config.tokenSigningKeystorePassword() ).thenReturn( new String( PASSWORD ) );
        when( config.tokenSigningKeyAlias() ).thenReturn( signingAlias );
        return new TokenSigningKeyServiceImpl( config );
    }

    private static void writeKeystore( final Path path, final Map<String, SecretKey> entries )
        throws Exception
    {
        final KeyStore keystore = KeyStore.getInstance( "PKCS12" );
        keystore.load( null, PASSWORD );
        for ( final Map.Entry<String, SecretKey> entry : entries.entrySet() )
        {
            keystore.setEntry( entry.getKey(), new KeyStore.SecretKeyEntry( entry.getValue() ),
                               new KeyStore.PasswordProtection( PASSWORD ) );
        }
        try (OutputStream out = Files.newOutputStream( path ))
        {
            keystore.store( out, PASSWORD );
        }
    }

    private static SecretKey newKey()
        throws Exception
    {
        return KeyGenerator.getInstance( "HmacSHA512" ).generateKey();
    }
}
