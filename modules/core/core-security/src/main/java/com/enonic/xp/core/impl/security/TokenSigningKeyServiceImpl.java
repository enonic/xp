package com.enonic.xp.core.impl.security;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
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

    private final AtomicReference<KeyState> keyState = new AtomicReference<>();

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
        final String kid = keyState().signingKid;
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
        return keyState().keys.computeIfAbsent( kid, this::resolveSigningKey );
    }

    @Override
    public String rotate()
    {
        // Purely additive: create a new live key. No demote of any existing key, so no read-modify-write
        // and no need for cluster-wide coordination - any live key is a valid signing key.
        final String newKid = KEY_PREFIX + "-" + randomSuffix();
        createSystemContext().runWith( () -> nodeService.create( CreateNodeParams.create()
                                                                     .parent( KEYS_PATH )
                                                                     .name( newKid )
                                                                     .data( newKeyData() )
                                                                     .inheritPermissions( true )
                                                                     .build() ) );
        invalidate();
        return newKid;
    }

    @Override
    public void decommission( final String keyId )
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
            final List<String> liveKids = liveKidsInContext();
            if ( liveKids.size() == 1 && liveKids.contains( keyId ) )
            {
                throw new IllegalStateException( "Cannot decommission the last live signing key; rotate first: " + keyId );
            }
            // Flag, do not delete: an additive marker needs no coordination and leaves an audit trail.
            // A decommissioned key no longer signs and no longer verifies, so its tokens stop working.
            if ( !Boolean.TRUE.equals( node.data().getBoolean( "decommissioned" ) ) )
            {
                nodeService.update( UpdateNodeParams.create()
                                        .path( keyPath( keyId ) )
                                        .editor( editable -> editable.data.setBoolean( "decommissioned", Boolean.TRUE ) )
                                        .build() );
            }
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
            if ( Boolean.TRUE.equals( node.data().getBoolean( "decommissioned" ) ) )
            {
                throw new IllegalArgumentException( "Token-signing key is decommissioned: " + kid );
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

    /**
     * The cached key state, rebuilt when absent or once its short TTL elapses. The TTL bounds how
     * long a rotate/decommission on another cluster node takes to be observed here (there is no event
     * plumbing). Concurrent refreshes race harmlessly: each builds an equivalent state, last one wins.
     */
    private KeyState keyState()
    {
        KeyState current = keyState.get();
        if ( current == null || System.currentTimeMillis() >= current.expiresAt )
        {
            current = new KeyState( findSigningKid(), System.currentTimeMillis() + CACHE_TTL_MS );
            keyState.set( current );
        }
        return current;
    }

    /**
     * Picks the kid to sign with: any live (non-decommissioned) token-signing key. Which one does not
     * matter for correctness - all live keys are valid - so the newest {@code created} is chosen for a
     * stable, deterministic result.
     */
    @Nullable
    private String findSigningKid()
    {
        return createSystemContext().callWith( () -> liveKeysInContext().stream().max( BY_AGE ).map( KeyCandidate::kid ).orElse( null ) );
    }

    /**
     * The live (non-decommissioned) token-signing keys. Must be called within the system context.
     */
    private List<KeyCandidate> liveKeysInContext()
    {
        final Nodes nodes = nodeService.getByIds(
            nodeService.findByParent( FindNodesByParentParams.create().parentPath( KEYS_PATH ).size( -1 ).build() ).getNodeIds() );

        return nodes.stream()
            .filter( node -> USE_TOKEN_SIGNING.equals( node.data().getString( "use" ) ) )
            .filter( node -> !Boolean.TRUE.equals( node.data().getBoolean( "decommissioned" ) ) )
            .map( node -> new KeyCandidate( node.name().toString(), node.data().getInstant( "created" ) ) )
            .toList();
    }

    private List<String> liveKidsInContext()
    {
        return liveKeysInContext().stream().map( KeyCandidate::kid ).toList();
    }

    /**
     * Orders token-signing keys by age, newest last (the maximum). A key with a creation time ranks
     * above one without. Package-private for testing.
     */
    static final Comparator<KeyCandidate> BY_AGE = Comparator.comparing( KeyCandidate::created, Comparator.nullsFirst( Comparator.naturalOrder() ) );

    record KeyCandidate(String kid, @Nullable Instant created)
    {
    }

    private PropertyTree newKeyData()
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
        data.setInstant( "created", Instant.now() );
        return data;
    }

    private void invalidate()
    {
        keyState.set( null );
    }

    /**
     * Immutable snapshot of the signing-key state: the preferred kid and a lazily-populated cache of
     * resolved verification keys, valid until {@link #expiresAt}.
     */
    private static final class KeyState
    {
        @Nullable
        final String signingKid;

        final long expiresAt;

        final ConcurrentMap<String, SecretKey> keys = new ConcurrentHashMap<>();

        KeyState( @Nullable final String signingKid, final long expiresAt )
        {
            this.signingKid = signingKid;
            this.expiresAt = expiresAt;
        }
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
