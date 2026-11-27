package com.enonic.xp.impl.server.rest.api;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AccessTokenInfo;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.snapshot.SnapshotService;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebResponse;

import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.request;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ManagementApiScopeTest
{
    private RepositoryApiHandler repositories;

    private SnapshotApiHandler snapshots;

    @BeforeEach
    void setUp()
    {
        final RepositoryService repositoryService = mock( RepositoryService.class );
        when( repositoryService.list() ).thenReturn( Repositories.empty() );
        repositories = new RepositoryApiHandler( repositoryService );
        final TaskService taskService = mock( TaskService.class );
        when( taskService.submitTask( any() ) ).thenReturn( TaskId.from( "t1" ) );
        snapshots = new SnapshotApiHandler( mock( SnapshotService.class ), taskService );
    }

    private static WebResponse withToken( final ManagementApiHandler handler, final HttpMethod method, final String path,
                                          final String... scope )
    {
        final AuthenticationInfo authInfo = AuthenticationInfo.create()
            .user( User.create().key( PrincipalKey.from( "user:system:alice" ) ).login( "alice" ).build() )
            .principals( RoleKeys.ADMIN )
            .accessToken( AccessTokenInfo.create().issuer( "xp" ).scope( List.of( scope ) ).build() )
            .build();
        return ContextBuilder.copyOf( ContextAccessor.current() )
            .authInfo( authInfo )
            .build()
            .callWith( () -> handler.handle( request( method, path ) ) );
    }

    @Test
    void reading_takes_the_read_scope_of_the_area_or_one_that_includes_it()
    {
        for ( String scope : new String[]{"read:repo", "repo", "read", "admin"} )
        {
            assertEquals( HttpStatus.OK, withToken( repositories, HttpMethod.GET, "/server:repo", scope ).getStatus(), scope );
        }
        for ( String[] scope : new String[][]{{}, {"read:content"}, {"content", "system"}, {"com.example.app:read"}} )
        {
            final WebResponse refused = withToken( repositories, HttpMethod.GET, "/server:repo", scope );
            assertEquals( HttpStatus.FORBIDDEN, refused.getStatus(), String.join( " ", scope ) );
            assertEquals( "Bearer error=\"insufficient_scope\", scope=\"read:repo\"", refused.getHeaders().get( "WWW-Authenticate" ) );
        }
    }

    @Test
    void changing_takes_the_scope_of_the_area_or_one_that_includes_it()
    {
        for ( String scope : new String[]{"read:repo", "read", "content"} )
        {
            final WebResponse refused = withToken( snapshots, HttpMethod.POST, "/server:snapshot", scope );
            assertEquals( HttpStatus.FORBIDDEN, refused.getStatus(), scope );
            assertEquals( "Bearer error=\"insufficient_scope\", scope=\"repo\"", refused.getHeaders().get( "WWW-Authenticate" ) );
        }
        for ( String scope : new String[]{"repo", "admin"} )
        {
            assertEquals( HttpStatus.ACCEPTED, withToken( snapshots, HttpMethod.POST, "/server:snapshot", scope ).getStatus(), scope );
        }
    }

    @Test
    void a_request_without_a_token_is_not_limited_by_scopes()
    {
        assertEquals( HttpStatus.OK, repositories.handle( request( HttpMethod.GET, "/server:repo" ) ).getStatus() );
    }
}
