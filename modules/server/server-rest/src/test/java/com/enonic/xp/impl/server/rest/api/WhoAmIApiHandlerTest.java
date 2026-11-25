package com.enonic.xp.impl.server.rest.api;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AccessTokenInfo;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.server.VersionInfo;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebResponse;

import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.request;
import static org.assertj.core.api.Assertions.assertThat;

class WhoAmIApiHandlerTest
{
    private static final User ALICE = User.create()
        .key( PrincipalKey.from( "user:system:alice" ) )
        .login( "alice" )
        .displayName( "Alice" )
        .email( "alice@example.com" )
        .build();

    private static final String SHORT_HASH_PROPERTY = "xp.build.shortHash";

    private final WhoAmIApiHandler handler = new WhoAmIApiHandler();

    @BeforeEach
    void setUp()
    {
        VersionInfo.set( "8.2.0" );
    }

    @AfterEach
    void tearDown()
    {
        VersionInfo.setDefault();
        System.clearProperty( SHORT_HASH_PROPERTY );
    }

    private String version()
        throws Exception
    {
        final String body = as( AuthenticationInfo.create().user( ALICE ).build() ).getBody().toString();
        return new ObjectMapper().readTree( body ).at( "/server/version" ).asText();
    }

    @Test
    void a_snapshot_version_carries_the_commit_it_was_built_from()
        throws Exception
    {
        VersionInfo.set( "8.2.0-SNAPSHOT" );
        System.setProperty( SHORT_HASH_PROPERTY, "b741000" );
        assertThat( version() ).isEqualTo( "8.2.0-SNAPSHOT+b741000" );

        System.setProperty( SHORT_HASH_PROPERTY, "N/A" );
        assertThat( version() ).isEqualTo( "8.2.0-SNAPSHOT" );

        System.clearProperty( SHORT_HASH_PROPERTY );
        assertThat( version() ).isEqualTo( "8.2.0-SNAPSHOT" );
    }

    @Test
    void a_released_version_stands_alone()
        throws Exception
    {
        System.setProperty( SHORT_HASH_PROPERTY, "b741000" );
        assertThat( version() ).isEqualTo( "8.2.0" );
    }

    private WebResponse as( final AuthenticationInfo authInfo )
    {
        return ContextBuilder.copyOf( ContextAccessor.current() )
            .authInfo( authInfo )
            .build()
            .callWith( () -> handler.handle( request( HttpMethod.GET, "/server:whoami" ) ) );
    }

    @Test
    void the_user_its_roles_and_the_server_version()
    {
        final WebResponse response = as( AuthenticationInfo.create()
                                             .user( ALICE )
                                             .principals( RoleKeys.EVERYONE, RoleKeys.AUTHENTICATED, RoleKeys.ADMIN,
                                                          PrincipalKey.from( "group:system:staff" ) )
                                             .build() );

        assertThat( response.getStatus() ).isEqualTo( HttpStatus.OK );
        assertThat( response.getBody() ).isEqualTo(
            "{\"user\":{\"key\":\"user:system:alice\",\"login\":\"alice\",\"displayName\":\"Alice\",\"email\":\"alice@example.com\"," +
                "\"idProvider\":\"system\"},\"roles\":[\"role:system.admin\",\"role:system.authenticated\",\"role:system.everyone\"]," +
                "\"server\":{\"version\":\"8.2.0\"}}" );
    }

    @Test
    void the_access_token_and_the_actor_when_there_are_any()
    {
        final WebResponse response = as( AuthenticationInfo.create()
                                             .user( User.create().key( PrincipalKey.from( "user:myidp:bob" ) ).login( "bob" ).build() )
                                             .principals( RoleKeys.AUTHENTICATED )
                                             .actor( PrincipalKey.from( "user:system:ci" ) )
                                             .accessToken( AccessTokenInfo.create()
                                                               .issuer( "xp" )
                                                               .clientId( "enonic-cli" )
                                                               .scope( List.of( "write", "read" ) )
                                                               .audience( List.of( "https://mgmt.example.com" ) )
                                                               .build() )
                                             .build() );

        assertThat( response.getBody() ).isEqualTo(
            "{\"user\":{\"key\":\"user:myidp:bob\",\"login\":\"bob\",\"displayName\":\"bob\",\"idProvider\":\"myidp\"}," +
                "\"roles\":[\"role:system.authenticated\"],\"actor\":\"user:system:ci\"," +
                "\"accessToken\":{\"issuer\":\"xp\",\"clientId\":\"enonic-cli\",\"scope\":[\"read\",\"write\"]," +
                "\"audience\":[\"https://mgmt.example.com\"]},\"server\":{\"version\":\"8.2.0\"}}" );
    }

    @Test
    void nobody_when_the_request_is_not_authenticated()
    {
        assertThat( handler.handle( request( HttpMethod.GET, "/server:whoami" ) ).getStatus() ).isEqualTo( HttpStatus.UNAUTHORIZED );
    }

    @Test
    void a_mapping_cannot_switch_it_off()
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.create().user( ALICE ).principals( RoleKeys.AUTHENTICATED ).build();

        for ( String verbs : new String[]{"none", "list"} )
        {
            final Context context = ContextBuilder.copyOf( ContextAccessor.current() ).authInfo( authInfo ).build();
            context.getLocalScope().setAttribute( "api.server:whoami.verbs", verbs );
            final WebResponse response = context.callWith( () -> handler.handle( request( HttpMethod.GET, "/server:whoami" ) ) );
            assertThat( response.getStatus() ).as( verbs ).isEqualTo( HttpStatus.OK );
        }
    }

    @Test
    void only_get()
    {
        assertThat( as( AuthenticationInfo.create().user( ALICE ).build() ).getStatus() ).isEqualTo( HttpStatus.OK );
        assertThat( handler.handle( request( HttpMethod.POST, "/server:whoami" ) ).getStatus() ).isEqualTo( HttpStatus.METHOD_NOT_ALLOWED );
    }
}
