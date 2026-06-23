package com.enonic.xp.core.impl.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.security.CryptoService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Component(configurationPid = "com.enonic.xp.security", service = CryptoService.class)
@NullMarked
public class CryptoServiceImpl
    implements CryptoService
{
    private static final String HMAC_SHA512 = "HmacSHA512";

    private static final NodePath KEYS_PATH = NodePath.create().addElement( "keys" ).build();

    private static final String TOKEN_SIGNING_KEY_ID = "token-signing-hs512";

    // Only key ids in this namespace may be resolved; the kid header of a token is
    // untrusted input and must never be able to select an arbitrary node.
    private static final String TOKEN_SIGNING_KEY_PREFIX = "token-signing-";

    private final NodeService nodeService;

    @Nullable
    private final byte[] encryptionKey;

    private final ConcurrentMap<String, SecretKey> keyCache = new ConcurrentHashMap<>();

    /**
     * Used to make sure the SecurityInitializer is run before this component is activated.
     */
    @SuppressWarnings("unused")
    @Reference
    private SecurityService securityService;

    @Activate
    public CryptoServiceImpl( @Reference final NodeService nodeService, final SecurityConfig config )
    {
        this.nodeService = nodeService;
        final String configuredKey = config.encryption_key();
        this.encryptionKey = configuredKey.isEmpty()
            ? null
            : configuredKey.getBytes( StandardCharsets.UTF_8 );
    }

    @Override
    public String tokenSigningKeyId()
    {
        return TOKEN_SIGNING_KEY_ID;
    }

    @Override
    public SecretKey getSigningKey( final String kid )
    {
        if ( kid == null || !kid.startsWith( TOKEN_SIGNING_KEY_PREFIX ) )
        {
            throw new IllegalArgumentException( "Unknown signing key id: " + kid );
        }
        return keyCache.computeIfAbsent( kid, this::resolveSigningKey );
    }

    private SecretKey resolveSigningKey( final String kid )
    {
        final byte[] material = loadKeyMaterial( kid );
        final byte[] effective = deriveKey( encryptionKey, material, "token-sig", kid );
        return new SecretKeySpec( effective, HMAC_SHA512 );
    }

    private byte[] loadKeyMaterial( final String kid )
    {
        final NodePath path = new NodePath( KEYS_PATH, NodeName.from( kid ) );
        final Node node = createSystemContext().callWith( () -> nodeService.getByPath( path ) );
        if ( node == null )
        {
            throw new IllegalArgumentException( "Signing key not found: " + kid );
        }
        final String stored = node.data().getString( "key" );
        if ( stored == null )
        {
            throw new IllegalArgumentException( "Signing key material missing: " + kid );
        }
        return Base64.getDecoder().decode( stored );
    }

    /**
     * Derives the effective key from the stored material. With no encryption key configured
     * the material is used directly; otherwise the material is mixed with the key-encryption-key
     * (and the use/kid for domain separation) via HMAC-SHA512, so identical stored material in
     * different environments yields different effective keys.
     */
    static byte[] deriveKey( @Nullable final byte[] encryptionKey, final byte[] material, final String use, final String kid )
    {
        if ( encryptionKey == null )
        {
            return material;
        }

        try
        {
            final Mac mac = Mac.getInstance( HMAC_SHA512 );
            mac.init( new SecretKeySpec( encryptionKey, HMAC_SHA512 ) );
            mac.update( material );
            mac.update( (byte) 0 );
            mac.update( use.getBytes( StandardCharsets.UTF_8 ) );
            mac.update( (byte) 0 );
            mac.update( kid.getBytes( StandardCharsets.UTF_8 ) );
            return mac.doFinal();
        }
        catch ( Exception e )
        {
            throw new IllegalStateException( "Unable to derive signing key", e );
        }
    }

    private static Context createSystemContext()
    {
        return ContextBuilder.from( ContextAccessor.current() )
            .authInfo( AuthenticationInfo.copyOf( ContextAccessor.current().getAuthInfo() ).principals( RoleKeys.ADMIN ).build() )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .branch( SystemConstants.BRANCH_SYSTEM )
            .build();
    }
}
