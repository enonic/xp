package com.enonic.xp.core.impl.security;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.Value;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeHit;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleRestriction;
import com.enonic.xp.security.PrincipalType;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.VerifiedUserKeyAuthToken;
import com.enonic.xp.security.auth.VerifiedUsernameAuthToken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

/**
 * The principal restriction on an authentication token: a ceiling on the memberships the resulting
 * context holds. Memberships are resolved from the node store, so the store is mocked with a user,
 * two roles the user holds directly, and a group that holds a third role.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SecurityServiceImplTest_roleRestriction
{
    private static final IdProviderKey IDP = IdProviderKey.from( "enonic" );

    private static final PrincipalKey USER = PrincipalKey.ofUser( IDP, "alice" );

    private static final PrincipalKey GROUP = PrincipalKey.ofGroup( IDP, "editors" );

    private static final PrincipalKey READER = PrincipalKey.ofRole( "cms.project.p.viewer" );

    private static final PrincipalKey WRITER = PrincipalKey.ofRole( "cms.project.p.editor" );

    private static final PrincipalKey APP = PrincipalKey.ofRole( "cms.cm.app" );

    private static final PrincipalKey NOT_HELD = PrincipalKey.ofRole( "system.admin" );

    @Mock
    private NodeService nodeService;

    @Mock(stubOnly = true)
    private SecurityAuditLogSupport auditLogSupport;

    private SecurityServiceImpl securityService;

    private final Map<NodeId, Node> nodes = new LinkedHashMap<>();

    private final Map<PrincipalKey, PrincipalKeys> directMembers = new LinkedHashMap<>();

    @BeforeEach
    void setUp()
    {
        final SecurityConfig securityConfig = mock( SecurityConfig.class, withSettings().stubOnly()
            .defaultAnswer( invocation -> invocation.getMethod().getDefaultValue() ) );
        final PasswordSecurityService passwordSecurityService = new PasswordSecurityService( new SuPasswordVerifier( "" ) );
        passwordSecurityService.activate( securityConfig );
        securityService = new SecurityServiceImpl( nodeService, auditLogSupport, passwordSecurityService, new InMemorySharedMapService() );

        principal( USER );
        principal( GROUP );
        principal( READER );
        principal( WRITER );
        principal( APP );
        principal( NOT_HELD );
        directMembers.put( USER, PrincipalKeys.from( READER, GROUP ) );
        directMembers.put( GROUP, PrincipalKeys.from( WRITER, APP ) );

        when( nodeService.findByQuery( any( NodeQuery.class ) ) ).thenAnswer( invocation -> {
            final NodeQuery query = invocation.getArgument( 0 );
            final FindNodesByQueryResult.Builder result = FindNodesByQueryResult.create();
            if ( query.getQuery() != null )
            {
                result.addNodeHit( NodeHit.create().nodeId( nodeId( USER ) ).build() );
            }
            else
            {
                for ( Filter filter : query.getQueryFilters() )
                {
                    final ValueFilter valueFilter = (ValueFilter) filter;
                    final Value value = valueFilter.getValues().iterator().next();
                    final PrincipalKey member = PrincipalKey.from( value.asString() );
                    directMembers.getOrDefault( member, PrincipalKeys.empty() )
                        .forEach( key -> result.addNodeHit( NodeHit.create().nodeId( nodeId( key ) ).build() ) );
                }
            }
            return result.build();
        } );
        when( nodeService.getByIds( any( NodeIds.class ) ) ).thenAnswer(
            invocation -> Nodes.from( invocation.<NodeIds>getArgument( 0 ).stream().map( nodes::get ).toList() ) );
    }

    @Test
    void no_restriction_gives_all_memberships()
    {
        final AuthenticationInfo authInfo = securityService.authenticate( new VerifiedUsernameAuthToken( IDP, "alice" ) );

        assertThat( authInfo.isAuthenticated() ).isTrue();
        assertThat( authInfo.getPrincipals() ).containsExactlyInAnyOrder( USER, GROUP, READER, WRITER, APP, RoleKeys.AUTHENTICATED,
                                                                          RoleKeys.EVERYONE );
        assertThat( authInfo.getRestriction() ).isEqualTo( RoleRestriction.none() );
    }

    @Test
    void restriction_keeps_only_held_memberships_among_the_listed()
    {
        final RoleRestriction restriction = RoleRestriction.to( READER, APP, NOT_HELD );
        final AuthenticationInfo authInfo = securityService.authenticate( new VerifiedUsernameAuthToken( IDP, "alice", restriction ) );

        assertThat( authInfo.isAuthenticated() ).isTrue();
        assertThat( authInfo.getUser().getKey() ).isEqualTo( USER );
        assertThat( authInfo.getPrincipals() ).containsExactlyInAnyOrder( USER, READER, APP, RoleKeys.AUTHENTICATED, RoleKeys.EVERYONE );
        assertThat( authInfo.getRestriction() ).as( "the limit is recorded as given, held or not" ).isEqualTo( restriction );
        assertThat( authInfo.hasRole( NOT_HELD ) ).as( "a listed role the user does not hold is not gained" ).isFalse();
        assertThat( authInfo.hasRole( WRITER ) ).as( "a held role not listed is dropped" ).isFalse();
    }

    @Test
    void transitive_membership_is_kept_without_its_group()
    {
        final AuthenticationInfo authInfo =
            securityService.authenticate( new VerifiedUsernameAuthToken( IDP, "alice", RoleRestriction.to( WRITER ) ) );

        assertThat( authInfo.getPrincipals() ).containsExactlyInAnyOrder( USER, WRITER, RoleKeys.AUTHENTICATED, RoleKeys.EVERYONE );
    }

    @Test
    void revoked_membership_is_gone_at_the_next_authentication()
    {
        final VerifiedUsernameAuthToken token = new VerifiedUsernameAuthToken( IDP, "alice", RoleRestriction.to( WRITER ) );

        assertThat( securityService.authenticate( token ).getPrincipals() ).as( "held through the group" ).contains( WRITER );

        directMembers.put( GROUP, PrincipalKeys.from( APP ) );

        final AuthenticationInfo after = securityService.authenticate( token );
        assertThat( after.isAuthenticated() ).isTrue();
        assertThat( after.getPrincipals() ).containsExactlyInAnyOrder( USER, RoleKeys.AUTHENTICATED, RoleKeys.EVERYONE );
    }

    @Test
    void by_user_key()
    {
        when( nodeService.getByPath( USER.toPath() ) ).thenReturn( nodes.get( nodeId( USER ) ) );

        final AuthenticationInfo full = securityService.authenticate( new VerifiedUserKeyAuthToken( USER ) );
        assertThat( full.isAuthenticated() ).isTrue();
        assertThat( full.getUser().getKey() ).isEqualTo( USER );
        assertThat( full.getPrincipals() ).containsExactlyInAnyOrder( USER, GROUP, READER, WRITER, APP, RoleKeys.AUTHENTICATED,
                                                                      RoleKeys.EVERYONE );

        final AuthenticationInfo restricted =
            securityService.authenticate( new VerifiedUserKeyAuthToken( USER, RoleRestriction.to( READER, NOT_HELD ) ) );
        assertThat( restricted.getPrincipals() ).containsExactlyInAnyOrder( USER, READER, RoleKeys.AUTHENTICATED, RoleKeys.EVERYONE );
    }

    @Test
    void by_user_key_unknown()
    {
        final PrincipalKey nobody = PrincipalKey.ofUser( IDP, "nobody" );
        when( nodeService.getByPath( nobody.toPath() ) ).thenReturn( null );
        assertThat( securityService.authenticate( new VerifiedUserKeyAuthToken( nobody ) ).isAuthenticated() ).isFalse();
    }

    @Test
    void an_administrator_gains_the_listed_roles_and_loses_system_admin()
    {
        final PrincipalKey market = PrincipalKey.ofRole( "cms.project.market.editor" );
        principal( market );
        directMembers.put( USER, PrincipalKeys.from( READER, GROUP, NOT_HELD ) );

        final AuthenticationInfo full = securityService.authenticate( new VerifiedUsernameAuthToken( IDP, "alice" ) );
        assertThat( full.hasRole( NOT_HELD ) ).as( "alice is an administrator" ).isTrue();
        assertThat( full.hasRole( market ) ).as( "without holding the market role" ).isFalse();

        final AuthenticationInfo restricted =
            securityService.authenticate( new VerifiedUsernameAuthToken( IDP, "alice", RoleRestriction.to( market, APP ) ) );
        assertThat( restricted.getPrincipals() ).containsExactlyInAnyOrder( USER, APP, market, RoleKeys.AUTHENTICATED, RoleKeys.EVERYONE );
        assertThat( restricted.hasRole( NOT_HELD ) ).as( "no administrator bypass under a restriction that does not name it" ).isFalse();

        assertThat( securityService.authenticate( new VerifiedUsernameAuthToken( IDP, "alice", RoleRestriction.to() ) ).getPrincipals() )
            .containsExactlyInAnyOrder( USER, RoleKeys.AUTHENTICATED, RoleKeys.EVERYONE );
    }

    @Test
    void empty_restriction_leaves_the_structural_principals()
    {
        final AuthenticationInfo authInfo =
            securityService.authenticate( new VerifiedUsernameAuthToken( IDP, "alice", RoleRestriction.to() ) );

        assertThat( authInfo.isAuthenticated() ).isTrue();
        assertThat( authInfo.getPrincipals() ).containsExactlyInAnyOrder( USER, RoleKeys.AUTHENTICATED, RoleKeys.EVERYONE );
    }

    private void principal( final PrincipalKey key )
    {
        final PropertyTree data = new PropertyTree();
        data.setString( PrincipalPropertyNames.PRINCIPAL_TYPE_KEY, key.getType().toString() );
        data.setString( PrincipalPropertyNames.DISPLAY_NAME_KEY, key.getId() );
        if ( key.getType() != PrincipalType.ROLE )
        {
            data.setString( PrincipalPropertyNames.ID_PROVIDER_KEY, key.getIdProviderKey().toString() );
        }
        if ( key.isUser() )
        {
            data.setString( PrincipalPropertyNames.LOGIN_KEY, key.getId() );
            data.setString( PrincipalPropertyNames.EMAIL_KEY, key.getId() + "@example.com" );
        }
        nodes.put( nodeId( key ), Node.create().id( nodeId( key ) ).name( PrincipalKeyNodeTranslator.toNodeName( key ) ).data( data ).build() );
    }

    private static NodeId nodeId( final PrincipalKey key )
    {
        return NodeId.from( key.toString().replace( ':', '-' ) );
    }
}
