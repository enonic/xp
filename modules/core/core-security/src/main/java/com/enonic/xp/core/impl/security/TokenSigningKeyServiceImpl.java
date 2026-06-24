package com.enonic.xp.core.impl.security;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

import javax.crypto.KeyGenerator;
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
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DeleteNodeParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Component(configurationPid = "com.enonic.xp.security", service = TokenSigningKeyService.class)
@NullMarked
public class TokenSigningKeyServiceImpl
    implements TokenSigningKeyService
{
    static final String KEY_PREFIX = "token-signing-hs512";

    static final String USE_TOKEN_SIGNING = "token-sig";

    private static final String HMAC_SHA512 = "HmacSHA512";

    private static final NodePath KEYS_PATH = NodePath.create().addElement( "keys" ).build();

    // The kid is a node name. As it is read from an untrusted token header, restrict it to safe
    // characters so it can never select a node outside the keys folder.
    private static final Pattern KID_PATTERN = Pattern.compile( "[a-zA-Z0-9_-]{1,64}" );

    // Rotation/decommission are deliberate, infrequent operations; a short cache bounds how long a
    // change takes to propagate across the cluster without any event plumbing.
    private static final long CACHE_TTL_MS = 60_000;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final NodeService nodeService;

    @Nullable
    private final byte[] encryptionKey;

    private final ConcurrentMap<String, SecretKey> keyCache = new ConcurrentHashMap<>();

    private volatile long preferredKidExpiresAt;

    @Nullable
    private volatile String preferredKid;

    /**
     * Used to make sure the SecurityInitializer is run before this component is activated.
     */
    @SuppressWarnings("unused")
    @Reference
    private SecurityService securityService;

    @Activate
    public TokenSigningKeyServiceImpl( @Reference final NodeService nodeService, final SecurityConfig config )
    {
        this.nodeService = nodeService;
        final String configuredKey = config.encryption_key();
        this.encryptionKey = configuredKey.isEmpty() ? null : configuredKey.getBytes( StandardCharsets.UTF_8 );
    }

    @Override
    public String getCurrentKeyId()
    {
        final String kid = currentPreferredKid();
        if ( kid == null )
        {
            throw new IllegalStateException( "No token-signing key is available" );
        }
        return kid;
    }

    @Override
    public SecretKey getSigningKey( final String kid )
    {
        if ( kid == null || !KID_PATTERN.matcher( kid ).matches() )
        {
            throw new IllegalArgumentException( "Invalid signing key id: " + kid );
        }
        expireCacheIfStale();
        return keyCache.computeIfAbsent( kid, this::resolveSigningKey );
    }

    @Override
    public synchronized String rotate()
    {
        final String newKid = KEY_PREFIX + "-" + randomSuffix();
        createSystemContext().runWith( () -> {
            final String previous = findPreferredKid();
            if ( previous != null )
            {
                nodeService.update( UpdateNodeParams.create()
                                        .path( keyPath( previous ) )
                                        .editor( editable -> editable.data.setBoolean( "preferred", Boolean.FALSE ) )
                                        .build() );
            }
            nodeService.create( CreateNodeParams.create()
                                    .parent( KEYS_PATH )
                                    .name( newKid )
                                    .data( newKeyData( true ) )
                                    .inheritPermissions( true )
                                    .build() );
        } );
        invalidate();
        return newKid;
    }

    @Override
    public synchronized void decommission( final String keyId )
    {
        if ( keyId == null || !KID_PATTERN.matcher( keyId ).matches() )
        {
            throw new IllegalArgumentException( "Invalid signing key id: " + keyId );
        }
        createSystemContext().runWith( () -> {
            final Node node = nodeService.getByPath( keyPath( keyId ) );
            if ( node == null || !USE_TOKEN_SIGNING.equals( node.data().getString( "use" ) ) )
            {
                throw new IllegalArgumentException( "Token-signing key not found: " + keyId );
            }
            if ( Boolean.TRUE.equals( node.data().getBoolean( "preferred" ) ) )
            {
                throw new IllegalStateException( "Cannot decommission the preferred signing key; rotate first: " + keyId );
            }
            nodeService.delete( DeleteNodeParams.create().nodeId( node.id() ).build() );
        } );
        invalidate();
    }

    private SecretKey resolveSigningKey( final String kid )
    {
        final byte[] material = createSystemContext().callWith( () -> {
            final Node node = nodeService.getByPath( keyPath( kid ) );
            if ( node == null || !USE_TOKEN_SIGNING.equals( node.data().getString( "use" ) ) )
            {
                throw new IllegalArgumentException( "Token-signing key not found: " + kid );
            }
            final String stored = node.data().getString( "key" );
            if ( stored == null )
            {
                throw new IllegalArgumentException( "Signing key material missing: " + kid );
            }
            return Base64.getDecoder().decode( stored );
        } );
        return new SecretKeySpec( deriveKey( encryptionKey, material, USE_TOKEN_SIGNING, kid ), HMAC_SHA512 );
    }

    @Nullable
    private String currentPreferredKid()
    {
        expireCacheIfStale();
        String kid = preferredKid;
        if ( kid == null )
        {
            synchronized ( this )
            {
                kid = preferredKid;
                if ( kid == null )
                {
                    kid = findPreferredKid();
                    preferredKid = kid;
                    preferredKidExpiresAt = System.currentTimeMillis() + CACHE_TTL_MS;
                }
            }
        }
        return kid;
    }

    /**
     * Scans the keys folder and selects the preferred token-signing key: the one flagged
     * {@code preferred}, breaking ties (or a missing flag) by the most recent {@code created}.
     */
    @Nullable
    private String findPreferredKid()
    {
        return createSystemContext().callWith( () -> {
            final Nodes nodes = nodeService.getByIds(
                nodeService.findByParent( FindNodesByParentParams.create().parentPath( KEYS_PATH ).size( -1 ).build() ).getNodeIds() );

            Node best = null;
            for ( final Node node : nodes )
            {
                if ( !USE_TOKEN_SIGNING.equals( node.data().getString( "use" ) ) )
                {
                    continue;
                }
                if ( best == null || isBetter( node, best ) )
                {
                    best = node;
                }
            }
            return best == null ? null : best.name().toString();
        } );
    }

    private static boolean isBetter( final Node candidate, final Node current )
    {
        return preferOver( Boolean.TRUE.equals( candidate.data().getBoolean( "preferred" ) ), candidate.data().getInstant( "created" ),
                           Boolean.TRUE.equals( current.data().getBoolean( "preferred" ) ), current.data().getInstant( "created" ) );
    }

    /**
     * Selection rule for the preferred signing key: an explicitly preferred key wins; otherwise (or
     * on a tie) the most recently created key wins. Package-private for testing.
     */
    static boolean preferOver( final boolean candidatePreferred, @Nullable final Instant candidateCreated, final boolean currentPreferred,
                               @Nullable final Instant currentCreated )
    {
        if ( candidatePreferred != currentPreferred )
        {
            return candidatePreferred;
        }
        return candidateCreated != null && ( currentCreated == null || candidateCreated.isAfter( currentCreated ) );
    }

    private PropertyTree newKeyData( final boolean preferred )
    {
        final SecretKey key;
        try
        {
            key = KeyGenerator.getInstance( HMAC_SHA512 ).generateKey();
        }
        catch ( Exception e )
        {
            throw new IllegalStateException( "Unable to generate token-signing key", e );
        }
        final PropertyTree data = new PropertyTree();
        data.setString( "key", Base64.getEncoder().encodeToString( key.getEncoded() ) );
        data.setString( "alg", "HS512" );
        data.setString( "use", USE_TOKEN_SIGNING );
        data.setBoolean( "preferred", preferred );
        data.setInstant( "created", Instant.now() );
        return data;
    }

    private void expireCacheIfStale()
    {
        if ( System.currentTimeMillis() >= preferredKidExpiresAt )
        {
            invalidate();
        }
    }

    private synchronized void invalidate()
    {
        this.preferredKid = null;
        this.preferredKidExpiresAt = 0;
        this.keyCache.clear();
    }

    private static NodePath keyPath( final String kid )
    {
        return new NodePath( KEYS_PATH, NodeName.from( kid ) );
    }

    private static String randomSuffix()
    {
        final byte[] bytes = new byte[5];
        SECURE_RANDOM.nextBytes( bytes );
        return HexFormat.of().formatHex( bytes );
    }

    /**
     * Derives the effective key from the stored material. With no encryption key configured the
     * material is used directly; otherwise the material is mixed with the key-encryption-key (and
     * the use/kid for domain separation) via HMAC-SHA512, so identical stored material in different
     * environments yields different effective keys.
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
