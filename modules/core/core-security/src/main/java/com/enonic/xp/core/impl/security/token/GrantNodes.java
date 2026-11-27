package com.enonic.xp.core.impl.security.token;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;

import org.jspecify.annotations.Nullable;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleRestriction;
import com.enonic.xp.security.token.Grant;

/**
 * How grants are stored under a user node:
 * <pre>
 * /identity/&lt;idp&gt;/users/&lt;user&gt;/
 *   auth_tokens/                 {@value #AUTH_TOKENS_TYPE_NAME}: a folder
 *     grants/                    {@value #GRANTS_TYPE_NAME}: a folder
 *       &lt;grant id&gt;/             {@value #GRANT_TYPE_NAME}: one grant, node id = grant id
 * </pre>
 * Revoking all grants deletes the {@code auth_tokens} subtree. All lookups are by id or parent, so
 * no property is indexed. Deleting the user deletes everything.
 */
final class GrantNodes
{
    static final String AUTH_TOKENS = "auth_tokens";

    static final String GRANTS = "grants";

    static final String AUTH_TOKENS_TYPE_NAME = "auth_tokens";

    static final String GRANTS_TYPE_NAME = "grants";

    static final String GRANT_TYPE_NAME = "grant";

    static final NodeType AUTH_TOKENS_TYPE = NodeType.from( AUTH_TOKENS_TYPE_NAME );

    static final NodeType GRANTS_TYPE = NodeType.from( GRANTS_TYPE_NAME );

    static final NodeType GRANT_TYPE = NodeType.from( GRANT_TYPE_NAME );

    static final IndexConfigDocument NO_INDEX = PatternIndexConfigDocument.create().defaultConfig( IndexConfig.NONE ).build();

    private GrantNodes()
    {
    }

    static NodePath authTokensPath( final PrincipalKey user )
    {
        return new NodePath( user.toPath(), NodeName.from( AUTH_TOKENS ) );
    }

    static NodePath grantsPath( final PrincipalKey user )
    {
        return new NodePath( authTokensPath( user ), NodeName.from( GRANTS ) );
    }

    /**
     * The successor kept for a retired refresh token: the retired token's hash, how long a redeem
     * with it is forgiven, and the successor wrapped under it and the cluster secret.
     */
    record Grace(String hash, Instant until, String next)
    {
    }

    /**
     * One grant as stored: what was approved, the hash of the current refresh token, the grant's
     * tag key, the generation of the current token, and the grace successor while there is one.
     */
    record GrantNode(String id, PrincipalKey subject, String clientId, Set<String> audience, Set<String> scope,
                     RoleRestriction restriction, Instant createdAt, Instant rotatedAt, Instant expiresAt, String hash, byte[] key,
                     long generation, @Nullable Grace grace)
    {
        GrantNode
        {
            audience = ImmutableSet.copyOf( audience );
            scope = ImmutableSet.copyOf( scope );
            key = key.clone();
        }

        static GrantNode from( final Node node )
        {
            final PropertySet data = node.data().getRoot();
            final RoleRestriction restriction = Boolean.TRUE.equals( data.getBoolean( "unrestricted" ) )
                ? RoleRestriction.none()
                : RoleRestriction.to( PrincipalKeys.from( strings( data, "roles" ).stream().map( PrincipalKey::from ).toList() ) );
            final PropertySet graceSet = data.getSet( "grace" );
            final Grace grace = graceSet == null
                ? null
                : new Grace( graceSet.getString( "hash" ), graceSet.getInstant( "until" ), graceSet.getString( "next" ) );
            return new GrantNode( node.id().toString(), PrincipalKey.from( data.getString( "subject" ) ), data.getString( "clientId" ),
                                  ImmutableSet.copyOf( strings( data, "audience" ) ), ImmutableSet.copyOf( strings( data, "scope" ) ),
                                  restriction, data.getInstant( "createdAt" ),
                                  data.getInstant( "rotatedAt" ), data.getInstant( "expiresAt" ), data.getString( "hash" ),
                                  Base64.getDecoder().decode( data.getString( "key" ) ), data.getLong( "generation" ), grace );
        }

        PropertyTree toData()
        {
            final PropertyTree tree = new PropertyTree();
            final PropertySet data = tree.getRoot();
            data.setString( "subject", subject.toString() );
            data.setString( "clientId", clientId );
            data.addStrings( "audience", audience );
            data.addStrings( "scope", scope );
            switch ( restriction )
            {
                case RoleRestriction.None _ -> data.setBoolean( "unrestricted", true );
                case RoleRestriction.To( PrincipalKeys roles ) ->
                    data.addStrings( "roles", roles.stream().map( PrincipalKey::toString ).toList() );
            }
            data.setInstant( "createdAt", createdAt );
            data.setInstant( "rotatedAt", rotatedAt );
            data.setInstant( "expiresAt", expiresAt );
            data.setString( "hash", hash );
            data.setString( "key", Base64.getEncoder().encodeToString( key ) );
            data.setLong( "generation", generation );
            if ( grace != null )
            {
                final PropertySet graceSet = data.addSet( "grace" );
                graceSet.setString( "hash", grace.hash() );
                graceSet.setInstant( "until", grace.until() );
                graceSet.setString( "next", grace.next() );
            }
            return tree;
        }

        /**
         * The grant after rotation: the new hash is current, the old token is kept in grace.
         */
        GrantNode rotated( final String newHash, final Instant now, final Grace grace )
        {
            return new GrantNode( id, subject, clientId, audience, scope, restriction, createdAt, now, expiresAt, newHash, key,
                                  generation + 1, grace );
        }

        /**
         * The grant with its grace successor dropped.
         */
        GrantNode withoutGrace()
        {
            return new GrantNode( id, subject, clientId, audience, scope, restriction, createdAt, rotatedAt, expiresAt, hash, key,
                                  generation, null );
        }

        /**
         * The grant with new scopes, audiences and restriction.
         */
        GrantNode changed( final Set<String> scope, final Set<String> audience, final RoleRestriction restriction )
        {
            return new GrantNode( id, subject, clientId, audience, scope, restriction, createdAt, rotatedAt, expiresAt, hash, key,
                                  generation, grace );
        }

        boolean isLive( final Instant now )
        {
            return expiresAt.isAfter( now );
        }

        Grant toGrant()
        {
            return Grant.create()
                .id( id )
                .subject( subject )
                .clientId( clientId )
                .audience( audience )
                .scope( scope )
                .restriction( restriction )
                .createdAt( createdAt )
                .rotatedAt( rotatedAt )
                .expiresAt( expiresAt )
                .build();
        }

        private static List<String> strings( final PropertySet set, final String name )
        {
            final List<String> values = new ArrayList<>();
            set.getStrings( name ).forEach( values::add );
            return values;
        }
    }
}
