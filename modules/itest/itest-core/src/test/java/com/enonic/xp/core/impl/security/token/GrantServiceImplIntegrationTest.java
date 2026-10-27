package com.enonic.xp.core.impl.security.token;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.core.impl.security.PasswordSecurityService;
import com.enonic.xp.core.impl.security.SecurityAuditLogSupport;
import com.enonic.xp.core.impl.security.SecurityConfig;
import com.enonic.xp.core.impl.security.SecurityInitializer;
import com.enonic.xp.core.impl.security.SecurityServiceImpl;
import com.enonic.xp.impl.shared.LocalSharedMapService;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.security.CreateUserParams;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleRestriction;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityConstants;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.UpdateUserParams;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.token.CreateGrantParams;
import com.enonic.xp.security.token.Grant;
import com.enonic.xp.security.token.GrantWithRefreshToken;
import com.enonic.xp.security.token.RedeemRefreshTokenParams;
import com.enonic.xp.security.token.UpdateGrantParams;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

/**
 * The grant service against a real node store: what is written where, what a redeem writes and
 * what it does not, and what two writers racing for one grant or one user get.
 */
class GrantServiceImplIntegrationTest
    extends AbstractNodeTest
{
    private static final PrincipalKey ALICE = PrincipalKey.ofUser( IdProviderKey.system(), "alice" );

    private static final PrincipalKey BOB = PrincipalKey.ofUser( IdProviderKey.system(), "bob" );

    private final LocalSharedMapService sharedMapService = new LocalSharedMapService();

    private volatile Instant now = Instant.parse( "2026-10-14T12:00:00.250Z" );

    private SecurityServiceImpl securityService;

    private GrantServiceImpl service;

    GrantServiceImplIntegrationTest()
    {
        super( true );
    }

    @BeforeEach
    void setUp()
    {
        final SecurityConfig securityConfig = mock( SecurityConfig.class, withSettings().stubOnly()
            .defaultAnswer( invocation -> invocation.getMethod().getDefaultValue() ) );
        final PasswordSecurityService passwordSecurityService = new PasswordSecurityService();
        passwordSecurityService.activate( securityConfig );
        securityService =
            new SecurityServiceImpl( nodeService, mock( SecurityAuditLogSupport.class, withSettings().stubOnly() ), passwordSecurityService, sharedMapService );
        asAdmin( () -> {
            SecurityInitializer.create()
                .setIndexService( indexService )
                .setSecurityService( securityService )
                .setNodeService( nodeService )
                .build()
                .initialize();
            user( ALICE );
            user( BOB );
            return null;
        } );
        service = service( Map.of() );
    }

    @Test
    void create_stores_the_grant_as_an_unindexed_node_under_the_user_with_only_a_hash()
    {
        final GrantWithRefreshToken issued = grantForAlice();
        final Grant grant = issued.grant();

        assertThat( grant.getSubject() ).isEqualTo( ALICE );
        assertThat( grant.getClientId() ).isEqualTo( "cli" );
        assertThat( grant.getAudience() ).containsExactly( "api" );
        assertThat( grant.getScope() ).containsExactly( "content:read" );
        assertThat( grant.getRestriction() ).isEqualTo( RoleRestriction.to( RoleKeys.ADMIN_LOGIN ) );
        final Instant second = now.truncatedTo( ChronoUnit.SECONDS );
        assertThat( grant.getCreatedAt() ).as( "whole seconds, like a token's claims" ).isEqualTo( second );
        assertThat( grant.getRotatedAt() ).isEqualTo( second );
        assertThat( grant.getExpiresAt() ).isEqualTo( second.plus( GrantServiceImpl.DEFAULT_TTL ) );
        assertThat( issued.refreshToken() ).matches( "[A-Za-z0-9_-]{43}\\." + grant.getId() + "\\.0\\.[A-Za-z0-9_-]{43}" );
        assertThat( issued.toString() ).doesNotContain( issued.refreshToken() );

        final Node node = asAdmin( () -> nodeService.getById( NodeId.from( grant.getId() ) ) );
        assertThat( node.path() ).isEqualTo( new NodePath( "/identity/system/users/alice/auth_tokens/grants/" + grant.getId() ) );
        assertThat( node.getNodeType() ).isEqualTo( GrantNodes.GRANT_TYPE );
        assertThat( node.data().toString() ).doesNotContain( issued.refreshToken().substring( 0, 43 ) );
        assertThat( node.data().getString( "hash" ) ).isEqualTo( RefreshTokens.hash( issued.refreshToken() ) );
        assertThat( node.data().getLong( "generation" ) ).isZero();
        assertThat( node.data().getString( "key" ) ).isNotBlank();
        assertThat( RefreshTokens.isIssuedUnder( RefreshTokens.parse( issued.refreshToken() ).orElseThrow(),
                                                 java.util.Base64.getDecoder().decode( node.data().getString( "key" ) ) ) ).isTrue();
        assertThat( node.getIndexConfigDocument().getConfigForPath( IndexPath.from( "hash" ) ) ).isEqualTo( IndexConfig.NONE );
        assertThat( node.getIndexConfigDocument().getConfigForPath( IndexPath.from( "subject" ) ) ).isEqualTo( IndexConfig.NONE );
        for ( String folder : new String[]{"/identity/system/users/alice/auth_tokens", "/identity/system/users/alice/auth_tokens/grants"} )
        {
            final Node folderNode = asAdmin( () -> nodeService.getByPath( new NodePath( folder ) ) );
            assertThat( folderNode ).as( folder ).isNotNull();
            assertThat( folderNode.getIndexConfigDocument().getConfigForPath( IndexPath.from( "x" ) ) ).isEqualTo( IndexConfig.NONE );
        }
        final Node authTokens = asAdmin( () -> nodeService.getByPath( new NodePath( "/identity/system/users/alice/auth_tokens" ) ) );
        assertThat( authTokens.getNodeType() ).isEqualTo( GrantNodes.AUTH_TOKENS_TYPE );
        assertThat( service.get( ALICE, grant.getId() ) ).contains( grant );
        assertThat( service.get( BOB, grant.getId() ) ).as( "another user's grant" ).isEmpty();
        assertThat( service.get( ALICE, "no such grant" ) ).isEmpty();

        assertThat( service.list( ALICE ) ).containsExactly( grant );
        assertThat( service.list( BOB ) ).isEmpty();
    }

    @Test
    void unrestricted_and_empty_restrictions_round_trip()
    {
        final GrantWithRefreshToken issued = create( "cli" );
        assertThat( issued.grant().getRestriction() ).isEqualTo( RoleRestriction.none() );
        assertThat( service.list( ALICE ).getFirst().getRestriction() ).isEqualTo( RoleRestriction.none() );
        assertThat( grantNode( issued ).data().getBoolean( "unrestricted" ) ).isTrue();
        assertThat( grantNode( issued ).data().hasProperty( "roles" ) ).isFalse();

        final GrantWithRefreshToken empty =
            service.create( CreateGrantParams.create().subject( ALICE ).clientId( "cli" ).restriction( RoleRestriction.to() ).build() );
        assertThat( service.list( ALICE ).stream().filter( g -> g.getId().equals( empty.grant().getId() ) ).findFirst().orElseThrow()
                        .getRestriction() ).isEqualTo( RoleRestriction.to() );
        assertThat( grantNode( empty ).data().hasProperty( "unrestricted" ) ).isFalse();
        assertThat( grantNode( empty ).data().hasProperty( "roles" ) ).isFalse();
    }

    @Test
    void redeem_within_rotation_window_keeps_the_token_and_writes_nothing()
    {
        final GrantWithRefreshToken issued = grantForAlice();
        final Instant issuedAt = now.truncatedTo( ChronoUnit.SECONDS );
        final Node before = grantNode( issued );
        now = now.plusSeconds( 60 );

        final GrantWithRefreshToken redeemed = redeem( issued.refreshToken() ).orElseThrow();
        assertThat( redeemed.refreshToken() ).isEqualTo( issued.refreshToken() );
        assertThat( redeemed.grant().getId() ).isEqualTo( issued.grant().getId() );
        assertThat( redeemed.grant().getRotatedAt() ).isEqualTo( issuedAt );
        assertThat( grantNode( issued ).getNodeVersionId() ).as( "no write" ).isEqualTo( before.getNodeVersionId() );
    }

    @Test
    void rotation_retires_the_token_and_a_retry_within_grace_gets_the_same_successor_without_a_write()
    {
        final GrantWithRefreshToken issued = grantForAlice();
        now = now.plus( GrantServiceImpl.DEFAULT_ROTATE_AFTER ).plusSeconds( 1 );

        final GrantWithRefreshToken rotated = redeem( issued.refreshToken() ).orElseThrow();
        assertThat( rotated.refreshToken() ).isNotEqualTo( issued.refreshToken() ).contains( "." + issued.grant().getId() + ".1." );
        assertThat( rotated.grant().getRotatedAt() ).isEqualTo( now.truncatedTo( ChronoUnit.SECONDS ) );
        final Node afterRotation = grantNode( issued );
        assertThat( afterRotation.data().getString( "hash" ) ).isEqualTo( RefreshTokens.hash( rotated.refreshToken() ) );
        assertThat( afterRotation.data().getLong( "generation" ) ).isEqualTo( 1 );
        assertThat( afterRotation.data().getSet( "grace" ).getString( "hash" ) ).isEqualTo( RefreshTokens.hash( issued.refreshToken() ) );
        final String wrapped = afterRotation.data().getSet( "grace" ).getString( "next" );
        assertThat( wrapped ).doesNotContain( rotated.refreshToken().substring( 0, 43 ) );
        assertThat( RefreshTokens.unwrap( new byte[64], issued.refreshToken(), wrapped ) ).as( "the node and the retired token are not enough" )
            .isEmpty();
        assertThat( RefreshTokens.unwrap( clusterSecret(), issued.refreshToken(), wrapped ) ).as( "the cluster secret is the missing part" )
            .contains( rotated.refreshToken() );

        now = now.plusSeconds( 10 );
        for ( int i = 0; i < 3; i++ )
        {
            final GrantWithRefreshToken retried = redeem( issued.refreshToken() ).orElseThrow();
            assertThat( retried.refreshToken() ).as( "retry %d returns the successor already issued", i ).isEqualTo( rotated.refreshToken() );
            assertThat( retried.grant().getId() ).isEqualTo( issued.grant().getId() );
        }
        assertThat( grantNode( issued ).getNodeVersionId() ).as( "retries write nothing" ).isEqualTo( afterRotation.getNodeVersionId() );
        assertThat( redeem( rotated.refreshToken() ).orElseThrow().refreshToken() ).as( "the successor is current" )
            .isEqualTo( rotated.refreshToken() );

        now = now.plus( GrantServiceImpl.DEFAULT_REUSE_GRACE );
        assertThat( redeem( issued.refreshToken() ) ).as( "past grace the retired token is a replay" ).isEmpty();
        assertThat( service.list( ALICE ) ).as( "the grant is revoked" ).isEmpty();
        assertThat( redeem( rotated.refreshToken() ) ).as( "the current token died with the grant" ).isEmpty();
    }

    @Test
    void replay_of_an_older_retired_token_revokes_the_grant_even_within_the_latest_grace()
    {
        final GrantWithRefreshToken r0 = grantForAlice();
        now = now.plus( GrantServiceImpl.DEFAULT_ROTATE_AFTER ).plusSeconds( 1 );
        final GrantWithRefreshToken r1 = redeem( r0.refreshToken() ).orElseThrow();
        now = now.plus( GrantServiceImpl.DEFAULT_ROTATE_AFTER ).plusSeconds( 1 );
        final GrantWithRefreshToken r2 = redeem( r1.refreshToken() ).orElseThrow();
        assertThat( List.of( r0.refreshToken(), r1.refreshToken(), r2.refreshToken() ) ).doesNotHaveDuplicates();
        assertThat( grantNode( r0 ).data().getLong( "generation" ) ).isEqualTo( 2 );

        now = now.plusSeconds( 1 );
        assertThat( redeem( r1.refreshToken() ).orElseThrow().refreshToken() ).as( "r1 is in grace" ).isEqualTo( r2.refreshToken() );
        assertThat( redeem( r0.refreshToken() ) ).as( "r0 was retired a rotation ago" ).isEmpty();
        assertThat( service.list( ALICE ) ).isEmpty();
        assertThat( redeem( r2.refreshToken() ) ).isEmpty();
    }

    @Test
    void a_copied_token_is_recognised_after_any_number_of_rotations()
    {
        final GrantWithRefreshToken copied = grantForAlice();
        String current = copied.refreshToken();
        for ( int i = 0; i < 150; i++ )
        {
            now = now.plus( GrantServiceImpl.DEFAULT_ROTATE_AFTER ).plusSeconds( 1 );
            current = redeem( current ).orElseThrow().refreshToken();
        }
        assertThat( grantNode( copied ).data().getLong( "generation" ) ).isEqualTo( 150 );
        assertThat( grantNode( copied ).data().getStrings( "retired" ) ).as( "no history is kept" ).isEmpty();

        now = now.plusSeconds( 1 );
        assertThat( redeem( copied.refreshToken() ) ).as( "the token retired 150 rotations ago is a replay" ).isEmpty();
        assertThat( service.list( ALICE ) ).as( "and revokes the grant" ).isEmpty();
        assertThat( redeem( current ) ).isEmpty();
    }

    @Test
    void an_expired_grace_successor_is_dropped_at_the_next_redeem()
    {
        final GrantWithRefreshToken issued = grantForAlice();
        now = now.plus( GrantServiceImpl.DEFAULT_ROTATE_AFTER ).plusSeconds( 1 );
        final GrantWithRefreshToken rotated = redeem( issued.refreshToken() ).orElseThrow();
        assertThat( grantNode( issued ).data().getSet( "grace" ) ).isNotNull();

        now = now.plusSeconds( 10 );
        assertThat( redeem( rotated.refreshToken() ).orElseThrow().refreshToken() ).isEqualTo( rotated.refreshToken() );
        assertThat( grantNode( issued ).data().getSet( "grace" ) ).as( "kept while the grace runs" ).isNotNull();

        now = now.plus( GrantServiceImpl.DEFAULT_REUSE_GRACE );
        assertThat( redeem( rotated.refreshToken() ).orElseThrow().refreshToken() ).isEqualTo( rotated.refreshToken() );
        assertThat( grantNode( issued ).data().getSet( "grace" ) ).as( "dropped once it ran out" ).isNull();
        assertThat( grantNode( issued ).data().getString( "hash" ) ).isEqualTo( RefreshTokens.hash( rotated.refreshToken() ) );
    }

    @Test
    void a_token_forged_with_the_stored_key_is_not_the_current_token()
    {
        final GrantWithRefreshToken issued = grantForAlice();
        final Node before = grantNode( issued );
        final byte[] storedKey = java.util.Base64.getDecoder().decode( before.data().getString( "key" ) );
        final long generation = before.data().getLong( "generation" );

        final String forged = RefreshTokens.newToken( storedKey, issued.grant().getId(), generation );
        assertThat( RefreshTokens.isIssuedUnder( RefreshTokens.parse( forged ).orElseThrow(), storedKey ) ).as( "the tag verifies" ).isTrue();
        assertThat( forged ).isNotEqualTo( issued.refreshToken() );

        now = now.plusSeconds( 1 );
        assertThat( redeem( forged ) ).as( "provenance is not possession" ).isEmpty();
        assertThat( grantNode( issued ).getNodeVersionId() ).as( "nothing written" ).isEqualTo( before.getNodeVersionId() );
        assertThat( service.list( ALICE ) ).hasSize( 1 );
        assertThat( redeem( issued.refreshToken() ).orElseThrow().refreshToken() ).isEqualTo( issued.refreshToken() );

        now = now.plus( GrantServiceImpl.DEFAULT_ROTATE_AFTER );
        final GrantWithRefreshToken rotated = redeem( issued.refreshToken() ).orElseThrow();
        final String forgedAhead = RefreshTokens.newToken( storedKey, issued.grant().getId(), generation + 1 );
        assertThat( redeem( forgedAhead ) ).as( "same for the current generation after a rotation" ).isEmpty();
        assertThat( redeem( rotated.refreshToken() ).orElseThrow().refreshToken() ).isEqualTo( rotated.refreshToken() );
    }

    @Test
    void a_redeem_the_grant_does_not_meet_changes_nothing()
    {
        final GrantWithRefreshToken issued = grantForAlice();
        now = now.plus( GrantServiceImpl.DEFAULT_ROTATE_AFTER ).plusSeconds( 1 );
        final Node before = grantNode( issued );

        final RedeemRefreshTokenParams otherIdProvider = RedeemRefreshTokenParams.create()
            .refreshToken( issued.refreshToken() )
            .idProvider( IdProviderKey.from( "other" ) )
            .clientId( "cli" )
            .build();
        final RedeemRefreshTokenParams otherClient =
            RedeemRefreshTokenParams.create().refreshToken( issued.refreshToken() ).clientId( "someone-else" ).build();
        assertThat( service.redeem( otherIdProvider ) ).as( "wrong id provider" ).isEmpty();
        assertThat( service.redeem( otherClient ) ).as( "wrong client" ).isEmpty();
        assertThat( grantNode( issued ).getNodeVersionId() ).as( "not rotated, not revoked" ).isEqualTo( before.getNodeVersionId() );

        final GrantWithRefreshToken rotated = service.redeem( RedeemRefreshTokenParams.create()
                                                                  .refreshToken( issued.refreshToken() )
                                                                  .idProvider( IdProviderKey.system() )
                                                                  .clientId( "cli" )
                                                                  .build() ).orElseThrow();
        assertThat( rotated.refreshToken() ).as( "the right conditions redeem the same token" ).isNotEqualTo( issued.refreshToken() );

        now = now.plus( GrantServiceImpl.DEFAULT_REUSE_GRACE ).plusSeconds( 1 );
        assertThat( service.redeem( RedeemRefreshTokenParams.create()
                                        .refreshToken( issued.refreshToken() )
                                        .idProvider( IdProviderKey.from( "other" ) )
                                        .clientId( "cli" )
                                        .build() ) ).as( "a retired token at the wrong endpoint" ).isEmpty();
        assertThat( service.list( ALICE ) ).as( "is refused, not counted as a replay" ).hasSize( 1 );
        assertThat( redeem( rotated.refreshToken() ) ).isPresent();
    }

    @Test
    void a_client_is_any_string_and_the_grant_keeps_to_it()
    {
        final String odd = "any string the client calls itself, even with spaces";
        final GrantWithRefreshToken issued = create( odd );
        assertThat( issued.grant().getClientId() ).isEqualTo( odd );
        assertThat( service.redeem( RedeemRefreshTokenParams.create().refreshToken( issued.refreshToken() ).clientId( odd ).build() ) )
            .as( "the same string redeems" )
            .isPresent();
        assertThat( service.redeem(
            RedeemRefreshTokenParams.create().refreshToken( issued.refreshToken() ).clientId( odd.toUpperCase() ).build() ) )
            .as( "compared as a string, nothing more" )
            .isEmpty();

        assertThatThrownBy( () -> CreateGrantParams.create().subject( ALICE ).build() ).isInstanceOf( NullPointerException.class );
        assertThatThrownBy( () -> CreateGrantParams.create().subject( ALICE ).clientId( " " ).build() ).isInstanceOf(
            IllegalArgumentException.class );
        assertThatThrownBy( () -> RedeemRefreshTokenParams.create().refreshToken( issued.refreshToken() ).build() ).isInstanceOf(
            NullPointerException.class );
        assertThatThrownBy( () -> RedeemRefreshTokenParams.create().refreshToken( issued.refreshToken() ).clientId( "" ).build() ).isInstanceOf(
            IllegalArgumentException.class );
    }

    @Test
    void redeem_rejects_garbage_unknown_and_expired()
    {
        assertThat( redeem( "" ) ).isEmpty();
        assertThat( redeem( "abc" ) ).isEmpty();
        assertThat( redeem( "abc." ) ).isEmpty();
        assertThat( redeem( "abc.not a node id" ) ).isEmpty();
        final byte[] key = RefreshTokens.newGrantKey();
        assertThat( redeem( RefreshTokens.newToken( key, "unknown", 0 ) ) ).as( "well-formed but unknown" ).isEmpty();
        assertThat( redeem( RefreshTokens.newToken( key, ALICE.toString().toLowerCase(), 0 ) ) ).as( "the user node is not a grant" )
            .isEmpty();

        final GrantWithRefreshToken issued =
            service.create( CreateGrantParams.create().subject( ALICE ).clientId( "cli" ).ttl( Duration.ofDays( 1 ) ).build() );
        assertThat( redeem( RefreshTokens.newToken( key, issued.grant().getId(), 0 ) ) ).as( "right grant, another key" ).isEmpty();
        assertThat( redeem( RefreshTokens.newToken( key, issued.grant().getId(), 5 ) ) ).as( "a generation ahead, another key" ).isEmpty();
        assertThat( service.list( ALICE ) ).as( "a token that was never the grant's does nothing to it" ).hasSize( 1 );
        now = now.plus( Duration.ofDays( 1 ) );
        assertThat( redeem( issued.refreshToken() ) ).isEmpty();
        assertThat( service.list( ALICE ) ).isEmpty();
    }

    @Test
    void revoke_one_and_all()
    {
        final GrantWithRefreshToken first = grantForAlice();
        final GrantWithRefreshToken second = grantForAlice();
        assertThat( service.list( ALICE ) ).hasSize( 2 );

        assertThat( service.revoke( ALICE, first.grant().getId() ) ).isTrue();
        assertThat( service.revoke( ALICE, first.grant().getId() ) ).isFalse();
        assertThat( service.revoke( BOB, second.grant().getId() ) ).as( "another user's grant" ).isFalse();
        assertThat( service.revoke( ALICE, "no such grant" ) ).isFalse();
        assertThat( redeem( first.refreshToken() ) ).isEmpty();
        assertThat( redeem( second.refreshToken() ) ).isPresent();
        assertThat( service.get( ALICE, first.grant().getId() ) ).as( "a revoked grant is gone at once" ).isEmpty();
        assertThat( service.get( ALICE, second.grant().getId() ) ).isPresent();

        service.revokeAll( ALICE );
        assertThat( service.list( ALICE ) ).isEmpty();
        assertThat( redeem( second.refreshToken() ) ).isEmpty();
        assertThat( service.get( ALICE, second.grant().getId() ) ).isEmpty();
        assertThat( asAdmin( () -> nodeService.nodeExists( GrantNodes.authTokensPath( ALICE ) ) ) ).isFalse();

        final GrantWithRefreshToken later = grantForAlice();
        assertThat( redeem( later.refreshToken() ) ).isPresent();

        service.revokeAll( PrincipalKey.ofUser( IdProviderKey.system(), "nobody" ) );
        assertThat( service.list( PrincipalKey.ofUser( IdProviderKey.system(), "nobody" ) ) ).isEmpty();
    }

    @Test
    void update_changes_what_the_grants_tokens_run_with()
    {
        final GrantWithRefreshToken issued = grantForAlice();
        final Node before = asAdmin( () -> nodeService.getById( NodeId.from( issued.grant().getId() ) ) );

        final Grant changed = service.update( UpdateGrantParams.create()
                                                  .subject( ALICE )
                                                  .grantId( issued.grant().getId() )
                                                  .scope( Set.of() )
                                                  .restriction( RoleRestriction.to() )
                                                  .build() ).orElseThrow();
        assertThat( changed.getScope() ).isEmpty();
        assertThat( changed.getRestriction() ).isEqualTo( RoleRestriction.to() );
        assertThat( changed.getAudience() ).as( "unset values are kept" ).containsExactly( "api" );
        assertThat( changed.getClientId() ).isEqualTo( "cli" );
        assertThat( service.get( ALICE, issued.grant().getId() ) ).contains( changed );
        assertThat( service.list( ALICE ) ).containsExactly( changed );

        final Node after = asAdmin( () -> nodeService.getById( NodeId.from( issued.grant().getId() ) ) );
        assertThat( after.data().getString( "hash" ) ).as( "the refresh token is untouched" ).isEqualTo( before.data().getString( "hash" ) );
        assertThat( redeem( issued.refreshToken() ) ).map( GrantWithRefreshToken::grant ).contains( changed );

        assertThat( service.update( UpdateGrantParams.create().subject( BOB ).grantId( issued.grant().getId() ).scope( Set.of( "x" ) ).build() ) )
            .as( "another user's grant" )
            .isEmpty();
        assertThat( service.update( UpdateGrantParams.create().subject( ALICE ).grantId( "no such grant" ).build() ) ).isEmpty();
        assertThat( service.get( ALICE, issued.grant().getId() ) ).as( "nothing changed" ).contains( changed );
    }

    @Test
    void caps_evict_the_oldest()
    {
        service = service( Map.of( "token.grant.maxPerClient", "2", "token.grant.maxPerUser", "3" ) );
        final GrantWithRefreshToken a1 = create( "a" );
        now = now.plusSeconds( 1 );
        final GrantWithRefreshToken a2 = create( "a" );
        now = now.plusSeconds( 1 );
        final GrantWithRefreshToken a3 = create( "a" );
        assertThat( service.list( ALICE ) ).extracting( Grant::getId ).containsExactly( a3.grant().getId(), a2.grant().getId() );
        assertThat( redeem( a1.refreshToken() ) ).isEmpty();

        now = now.plusSeconds( 1 );
        final GrantWithRefreshToken b1 = create( "b" );
        now = now.plusSeconds( 1 );
        final GrantWithRefreshToken c1 = create( "c" );
        assertThat( service.list( ALICE ) ).extracting( Grant::getId )
            .containsExactly( c1.grant().getId(), b1.grant().getId(), a3.grant().getId() );
    }

    @Test
    void list_is_newest_first_and_hides_expired()
    {
        final GrantWithRefreshToken old = create( "a" );
        now = now.plusSeconds( 1 );
        final GrantWithRefreshToken shortLived =
            service.create( CreateGrantParams.create().subject( ALICE ).clientId( "a" ).ttl( Duration.ofSeconds( 10 ) ).build() );
        now = now.plusSeconds( 1 );
        final GrantWithRefreshToken fresh = create( "a" );

        assertThat( service.list( ALICE ) ).extracting( Grant::getId )
            .containsExactly( fresh.grant().getId(), shortLived.grant().getId(), old.grant().getId() );
        now = now.plusSeconds( 10 );
        assertThat( service.list( ALICE ) ).extracting( Grant::getId ).containsExactly( fresh.grant().getId(), old.grant().getId() );
    }

    @Test
    void unknown_user_cannot_be_granted()
    {
        assertThatThrownBy( () -> service.create(
            CreateGrantParams.create().subject( PrincipalKey.ofUser( IdProviderKey.system(), "nobody" ) ).clientId( "cli" ).build() ) ).isInstanceOf(
            IllegalArgumentException.class );
    }

    @Test
    void the_user_node_is_left_alone_and_goes_with_its_grants()
    {
        final Node userBefore = asAdmin( () -> nodeService.getByPath( ALICE.toPath() ) );
        final GrantWithRefreshToken issued = grantForAlice();
        service.revokeAll( ALICE );
        final GrantWithRefreshToken again = grantForAlice();
        assertThat( asAdmin( () -> nodeService.getByPath( ALICE.toPath() ) ).getNodeVersionId() ).as( "grants never write the user" )
            .isEqualTo( userBefore.getNodeVersionId() );

        asAdmin( () -> securityService.updateUser( UpdateUserParams.create().userKey( ALICE ).editor( editable -> {
            editable.displayName = "Alice Renamed";
            editable.profile.setString( "myapp.theme", "dark" );
        } ).build() ) );
        assertThat( redeem( again.refreshToken() ) ).as( "a user edit leaves the grant" ).isPresent();
        assertThat( asAdmin( () -> securityService.getUser( ALICE ) ).orElseThrow().getProfile().getString( "myapp.theme" ) ).isEqualTo( "dark" );

        asAdmin( () -> {
            securityService.deletePrincipal( ALICE );
            return null;
        } );
        assertThat( redeem( again.refreshToken() ) ).isEmpty();
        assertThat( redeem( issued.refreshToken() ) ).isEmpty();
        assertThat( service.list( ALICE ) ).isEmpty();
        assertThat( service.get( ALICE, again.grant().getId() ) ).isEmpty();
        assertThat( asAdmin( () -> nodeService.nodeExists( GrantNodes.authTokensPath( ALICE ) ) ) ).isFalse();

        asAdmin( () -> {
            user( ALICE );
            return null;
        } );
        assertThat( asAdmin( () -> nodeService.getByPath( ALICE.toPath() ) ).id() ).as( "the user node id is the key, so it is back" )
            .isEqualTo( userBefore.id() );
        final GrantWithRefreshToken recreated = grantForAlice();
        assertThat( redeem( recreated.refreshToken() ) ).isPresent();
        assertThat( redeem( again.refreshToken() ) ).as( "the former user's grant stays gone" ).isEmpty();
        assertThat( service.get( ALICE, again.grant().getId() ) ).isEmpty();
    }

    @Test
    void a_grant_node_outside_its_subjects_folder_is_nothing()
    {
        final GrantWithRefreshToken issued = grantForAlice();
        asAdmin( () -> nodeService.update( UpdateNodeParams.create()
                                               .id( NodeId.from( issued.grant().getId() ) )
                                               .editor( editable -> editable.data.setString( "subject", BOB.toString() ) )
                                               .build() ) );
        assertThat( redeem( issued.refreshToken() ) ).as( "a grant node under another user's folder" ).isEmpty();
        assertThat( service.revoke( BOB, issued.grant().getId() ) ).isFalse();
        assertThat( service.get( BOB, issued.grant().getId() ) ).isEmpty();
        assertThat( service.get( ALICE, issued.grant().getId() ) ).isEmpty();
        assertThat( asAdmin( () -> nodeService.nodeExists( GrantNodes.grantsPath( ALICE ) ) ) ).as( "nothing was deleted" ).isTrue();
    }

    @Test
    void concurrent_redeems_of_one_token_all_get_the_same_successor()
        throws Exception
    {
        final GrantWithRefreshToken issued = grantForAlice();
        now = now.plus( GrantServiceImpl.DEFAULT_ROTATE_AFTER ).plusSeconds( 1 );

        final List<String> tokens = concurrently( 8, () -> redeem( issued.refreshToken() ).orElseThrow().refreshToken() );

        assertThat( tokens ).hasSize( 8 ).doesNotContain( issued.refreshToken() );
        assertThat( tokens.stream().distinct() ).hasSize( 1 );
        final Node node = grantNode( issued );
        assertThat( node.data().getString( "hash" ) ).isEqualTo( RefreshTokens.hash( tokens.getFirst() ) );
        assertThat( node.data().getLong( "generation" ) ).isEqualTo( 1 );
    }

    @Test
    void concurrent_creates_respect_the_caps()
        throws Exception
    {
        service = service( Map.of( "token.grant.maxPerUser", "5", "token.grant.maxPerClient", "3" ) );

        concurrently( 12, () -> create( "cli" ).grant().getId() );

        assertThat( service.list( ALICE ) ).hasSize( 3 );
        assertThat( asAdmin( () -> nodeService.findByParent(
            com.enonic.xp.node.FindNodesByParentParams.create().parentPath( GrantNodes.grantsPath( ALICE ) ).build() ).getNodeIds() ) )
            .hasSize( 3 );
    }

    private <T> List<T> concurrently( final int threads, final Callable<T> body )
        throws Exception
    {
        final ExecutorService pool = Executors.newFixedThreadPool( threads );
        try
        {
            final CountDownLatch start = new CountDownLatch( 1 );
            final List<Future<T>> futures = new ArrayList<>();
            for ( int i = 0; i < threads; i++ )
            {
                futures.add( pool.submit( () -> {
                    start.await();
                    return body.call();
                } ) );
            }
            start.countDown();
            final List<T> results = new ArrayList<>();
            for ( Future<T> future : futures )
            {
                results.add( future.get() );
            }
            return results;
        }
        finally
        {
            pool.shutdownNow();
        }
    }

    private byte[] clusterSecret()
    {
        return new SharedMapTokenSigningKeys( sharedMapService, GrantServiceImpl.GRACE_SECRET_MAP, "" ).signingKey().secret();
    }

    private Optional<GrantWithRefreshToken> redeem( final String refreshToken )
    {
        return service.redeem( RedeemRefreshTokenParams.create().refreshToken( refreshToken ).clientId( "cli" ).build() );
    }

    private Node grantNode( final GrantWithRefreshToken issued )
    {
        return asAdmin( () -> nodeService.getById( NodeId.from( issued.grant().getId() ) ) );
    }

    private GrantWithRefreshToken grantForAlice()
    {
        return service.create( CreateGrantParams.create()
                                   .subject( ALICE )
                                   .clientId( "cli" )
                                   .audience( "api" )
                                   .scope( "content:read" )
                                   .restriction( RoleRestriction.to( RoleKeys.ADMIN_LOGIN ) )
                                   .build() );
    }

    private GrantWithRefreshToken create( final String clientId )
    {
        return service.create( CreateGrantParams.create().subject( ALICE ).clientId( clientId ).build() );
    }

    private GrantServiceImpl service( final Map<String, ?> config )
    {
        final GrantServiceImpl service = new GrantServiceImpl( nodeService, sharedMapService, new Clock()
        {
            @Override
            public ZoneId getZone()
            {
                return ZoneOffset.UTC;
            }

            @Override
            public Clock withZone( final ZoneId zone )
            {
                return this;
            }

            @Override
            public Instant instant()
            {
                return now;
            }
        } );
        service.activate( config );
        return service;
    }

    private void user( final PrincipalKey key )
    {
        securityService.createUser( CreateUserParams.create()
                                        .userKey( key )
                                        .displayName( key.getId() )
                                        .login( key.getId() )
                                        .email( key.getId() + "@example.com" )
                                        .build() );
    }

    private <T> T asAdmin( final Callable<T> body )
    {
        final Context context = ContextBuilder.create()
            .authInfo( AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( User.anonymous() ).build() )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .branch( SecurityConstants.BRANCH_SECURITY )
            .build();
        return context.callWith( body );
    }
}
