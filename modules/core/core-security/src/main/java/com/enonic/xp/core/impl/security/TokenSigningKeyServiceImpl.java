package com.enonic.xp.core.impl.security;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyStore;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import javax.crypto.SecretKey;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * Resolves the symmetric keys that sign/verify self-issued access tokens (device / native login)
 * from a PKCS12 keystore configured for the security bundle.
 * <p>
 * Key lifecycle is the operator's responsibility, managed with {@code keytool} on the keystore:
 * rotate by adding an entry and repointing the signing alias; retire one by removing it. So
 * "decommission" and "removal" are the same thing - a change to the keystore - and there is no
 * in-process key-management API. The keystore file is reloaded automatically when it changes.
 * <p>
 * Internal to core-security: it deals in raw key material ({@link SecretKey}) that must not leak
 * onto the public security API.
 */
@Component(configurationPid = "com.enonic.xp.security", service = TokenSigningKeyService.class)
@NullMarked
public class TokenSigningKeyServiceImpl
    implements TokenSigningKeyService
{
    // The kid is a keystore alias read from an untrusted token header; restrict it to safe characters.
    private static final Pattern ALIAS_PATTERN = Pattern.compile( "[A-Za-z0-9_.-]{1,256}" );

    @Nullable
    private final Path keystorePath;

    private final char[] keystorePassword;

    private final String signingAlias;

    private final AtomicReference<Loaded> loaded = new AtomicReference<>();

    @Activate
    public TokenSigningKeyServiceImpl( final SecurityConfig config )
    {
        final String path = config.tokenSigningKeystore();
        this.keystorePath = path.isBlank() ? null : Path.of( path );
        this.keystorePassword = config.tokenSigningKeystorePassword().toCharArray();
        this.signingAlias = config.tokenSigningKeyAlias();
    }

    @Override
    public String getCurrentKeyId()
    {
        if ( signingAlias.isBlank() )
        {
            throw new IllegalStateException( "No token-signing key alias is configured" );
        }
        if ( !isSecretKeyEntry( keystore(), signingAlias ) )
        {
            throw new IllegalStateException( "Configured token-signing key alias not found in keystore: " + signingAlias );
        }
        return signingAlias;
    }

    @Override
    public SecretKey getSigningKey( final String kid )
    {
        if ( kid == null || !ALIAS_PATTERN.matcher( kid ).matches() )
        {
            throw new IllegalArgumentException( "Invalid signing key id: " + kid );
        }
        final KeyStore keystore = keystore();
        try
        {
            if ( !isSecretKeyEntry( keystore, kid ) )
            {
                throw new IllegalArgumentException( "Token-signing key not found: " + kid );
            }
            final Key key = keystore.getKey( kid, keystorePassword );
            if ( !( key instanceof SecretKey ) )
            {
                throw new IllegalArgumentException( "Token-signing key not found: " + kid );
            }
            return (SecretKey) key;
        }
        catch ( GeneralSecurityException e )
        {
            throw new IllegalArgumentException( "Unable to read token-signing key: " + kid, e );
        }
    }

    /**
     * The loaded keystore, reloaded when the file's modification time changes (so a rotated/swapped
     * keystore is picked up without a restart).
     */
    private KeyStore keystore()
    {
        if ( keystorePath == null )
        {
            throw new IllegalStateException( "No token-signing keystore is configured" );
        }
        final long mtime = lastModified( keystorePath );
        Loaded current = loaded.get();
        if ( current == null || current.mtime != mtime )
        {
            current = load( keystorePath, mtime );
            loaded.set( current );
        }
        return current.keystore;
    }

    private Loaded load( final Path path, final long mtime )
    {
        try (InputStream in = Files.newInputStream( path ))
        {
            final KeyStore keystore = KeyStore.getInstance( "PKCS12" );
            keystore.load( in, keystorePassword );
            return new Loaded( keystore, mtime );
        }
        catch ( IOException | GeneralSecurityException e )
        {
            throw new IllegalStateException( "Unable to load token-signing keystore: " + path, e );
        }
    }

    private static long lastModified( final Path path )
    {
        try
        {
            return Files.getLastModifiedTime( path ).toMillis();
        }
        catch ( IOException e )
        {
            throw new IllegalStateException( "Token-signing keystore is not readable: " + path, e );
        }
    }

    private static boolean isSecretKeyEntry( final KeyStore keystore, final String alias )
    {
        try
        {
            return keystore.entryInstanceOf( alias, KeyStore.SecretKeyEntry.class );
        }
        catch ( GeneralSecurityException e )
        {
            return false;
        }
    }

    private record Loaded(KeyStore keystore, long mtime)
    {
    }
}
