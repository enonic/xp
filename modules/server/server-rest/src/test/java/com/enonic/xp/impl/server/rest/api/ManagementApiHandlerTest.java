package com.enonic.xp.impl.server.rest.api;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpSession;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.task.TaskId;

import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.request;
import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.withVirtualHostContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ManagementApiHandlerTest
{
    private TestHandler handler;

    @BeforeEach
    void setUp()
    {
        handler = new TestHandler();
    }

    @Test
    void rootRoute()
    {
        assertEquals( "list", body( handler.handle( request( HttpMethod.GET, "/server:test" ) ) ) );
        assertEquals( "list", body( handler.handle( request( HttpMethod.GET, "/server:test/" ) ) ) );
    }

    @Test
    void pathParams()
    {
        assertEquals( "get:abc", body( handler.handle( request( HttpMethod.GET, "/server:test/abc" ) ) ) );
        assertEquals( "load:abc", body( handler.handle( request( HttpMethod.POST, "/server:test/abc/load" ) ) ) );
    }

    @Test
    void notFound()
    {
        final WebResponse response = handler.handle( request( HttpMethod.GET, "/server:test/abc/nope" ) );
        assertEquals( HttpStatus.NOT_FOUND, response.getStatus() );
        assertTrue( body( response ).contains( "\"status\":404" ) );
    }

    @Test
    void methodNotAllowed()
    {
        assertEquals( HttpStatus.METHOD_NOT_ALLOWED, handler.handle( request( HttpMethod.DELETE, "/server:test" ) ).getStatus() );
    }

    @Test
    void aliasResolvesToCanonicalVerb()
    {
        assertEquals( "prune", body( handler.handle( request( HttpMethod.POST, "/server:test/prune" ) ) ) );
        assertEquals( "prune", body( handler.handle( request( HttpMethod.POST, "/server:test/vacuum" ) ) ) );
    }

    @Test
    void verbForbiddenByPolicy()
    {
        final Map<String, String> policy = Map.of( "api.server:test.verbs", "list, get" );

        assertEquals( HttpStatus.OK, withVirtualHostContext( policy, () -> handler.handle( request( HttpMethod.GET, "/server:test" ) ) ).getStatus() );
        assertEquals( HttpStatus.FORBIDDEN,
                      withVirtualHostContext( policy, () -> handler.handle( request( HttpMethod.POST, "/server:test/prune" ) ) ).getStatus() );
    }

    @Test
    void aliasCannotBypassPolicy()
    {
        final Map<String, String> policy = Map.of( "api.server:test.verbs", "list" );

        final WebResponse response = withVirtualHostContext( policy, () -> handler.handle( request( HttpMethod.POST, "/server:test/vacuum" ) ) );

        assertEquals( HttpStatus.FORBIDDEN, response.getStatus() );
        assertTrue( body( response ).contains( "[prune]" ) );
    }

    @Test
    void policyCheckedBeforeMethod()
    {
        // an unknown path stays a 404 even when the API is fully locked: the policy applies to operations, not to routing
        final Map<String, String> policy = Map.of( "api.server:test.verbs", "-" );

        assertEquals( HttpStatus.NOT_FOUND,
                      withVirtualHostContext( policy, () -> handler.handle( request( HttpMethod.GET, "/server:test/a/b/c" ) ) ).getStatus() );
        assertEquals( HttpStatus.FORBIDDEN,
                      withVirtualHostContext( policy, () -> handler.handle( request( HttpMethod.GET, "/server:test" ) ) ).getStatus() );
    }

    @Test
    void badRequestOnInvalidBody()
    {
        final WebResponse response = handler.handle( request( HttpMethod.POST, "/server:test/parse", "not json" ) );
        assertEquals( HttpStatus.BAD_REQUEST, response.getStatus() );

        assertEquals( HttpStatus.BAD_REQUEST, handler.handle( request( HttpMethod.POST, "/server:test/parse" ) ).getStatus() );
    }

    @Test
    void acceptedTask()
    {
        final WebResponse response = handler.handle( request( HttpMethod.POST, "/server:test/task" ) );
        assertEquals( HttpStatus.ACCEPTED, response.getStatus() );
        assertEquals( "{\"taskId\":\"t1\"}", body( response ) );
    }

    @Test
    void wildcardPolicy()
    {
        assertEquals( HttpStatus.OK, withVirtualHostContext( Map.of( "api.server:test.verbs", "*" ),
                                                             () -> handler.handle( request( HttpMethod.POST, "/server:test/prune" ) ) ).getStatus() );
    }

    @Test
    void errorWithoutMessageUsesReasonPhrase()
    {
        final WebResponse response = handler.handle( request( HttpMethod.POST, "/server:test/fail" ) );

        assertEquals( HttpStatus.BAD_REQUEST, response.getStatus() );
        assertTrue( body( response ).contains( "\"message\":\"Bad Request\"" ) );
    }

    @Test
    void queryParam()
    {
        assertEquals( "param:null", body( handler.handle( request( HttpMethod.GET, "/server:test/param" ) ) ) );

        final WebRequest request = request( HttpMethod.GET, "/server:test/param" );
        request.getParams().put( "name", "x" );
        assertEquals( "param:x", body( handler.handle( request ) ) );
    }

    @Test
    void wrongApiPrefixIsRejected()
    {
        assertThrows( WebException.class, () -> handler.handle( request( HttpMethod.GET, "/server:other/abc" ) ) );
    }

    @Test
    void crossOriginStateChangeRejectedForSessionUser()
    {
        final WebRequest request = request( HttpMethod.POST, "/server:test/prune" );
        request.setScheme( "https" );
        request.setHost( "example.com" );
        request.setPort( 443 );
        request.getHeaders().put( "Origin", "https://evil.example.org" );
        when( request.getRawRequest().getSession( false ) ).thenReturn( mock( HttpSession.class ) );

        final WebResponse response = authenticatedContext().callWith( () -> handler.handle( request ) );

        assertEquals( HttpStatus.FORBIDDEN, response.getStatus() );
        assertTrue( body( response ).contains( "Origin [https://evil.example.org] is not allowed" ) );
    }

    @Test
    void sameOriginStateChangeAllowed()
    {
        final WebRequest request = request( HttpMethod.POST, "/server:test/prune" );
        request.setScheme( "https" );
        request.setHost( "example.com" );
        request.setPort( 443 );
        request.getHeaders().put( "Origin", "https://example.com" );
        when( request.getRawRequest().getSession( false ) ).thenReturn( mock( HttpSession.class ) );

        assertEquals( HttpStatus.OK, authenticatedContext().callWith( () -> handler.handle( request ) ).getStatus() );
    }

    @Test
    void crossOriginSafeMethodAllowed()
    {
        final WebRequest request = request( HttpMethod.GET, "/server:test" );
        request.setScheme( "https" );
        request.setHost( "example.com" );
        request.setPort( 443 );
        request.getHeaders().put( "Origin", "https://evil.example.org" );
        when( request.getRawRequest().getSession( false ) ).thenReturn( mock( HttpSession.class ) );

        assertEquals( HttpStatus.OK, authenticatedContext().callWith( () -> handler.handle( request ) ).getStatus() );
    }

    @Test
    void crossOriginStateChangeWithoutSessionAllowed()
    {
        final WebRequest request = request( HttpMethod.POST, "/server:test/prune" );
        request.setScheme( "https" );
        request.setHost( "example.com" );
        request.setPort( 443 );
        request.getHeaders().put( "Origin", "https://evil.example.org" );

        assertEquals( HttpStatus.OK, authenticatedContext().callWith( () -> handler.handle( request ) ).getStatus() );
    }

    private static String body( final WebResponse response )
    {
        return String.valueOf( response.getBody() );
    }

    private static Context authenticatedContext()
    {
        final User user = User.create().key( PrincipalKey.ofUser( IdProviderKey.system(), "user" ) ).login( "user" ).build();
        return ContextBuilder.from( ContextAccessor.current() )
            .authInfo( AuthenticationInfo.create().principals( RoleKeys.EVERYONE, RoleKeys.AUTHENTICATED ).user( user ).build() )
            .build();
    }

    private static final class TestHandler
        extends ManagementApiHandler
    {
        TestHandler()
        {
            super( "server:test" );
            route( HttpMethod.GET, "/", "list", ( request, params ) -> text( "list" ) );
            // literal routes are registered before the {name} capture: routes match in registration order
            route( HttpMethod.GET, "/param", "param", ( request, params ) -> text( "param:" + param( request, "name" ) ) );
            route( HttpMethod.GET, "/{name}", "get", ( request, params ) -> text( "get:" + params.get( "name" ) ) );
            route( HttpMethod.POST, "/{name}/load", "load", ( request, params ) -> text( "load:" + params.get( "name" ) ) );
            route( HttpMethod.POST, "/prune", "prune", ( request, params ) -> text( "prune" ) );
            route( HttpMethod.POST, "/vacuum", "prune", ( request, params ) -> text( "prune" ) );
            route( HttpMethod.POST, "/parse", "parse", ( request, params ) -> text( body( request, Map.class ).toString() ) );
            route( HttpMethod.POST, "/task", "task", ( request, params ) -> accepted( TaskId.from( "t1" ) ) );
            route( HttpMethod.POST, "/fail", "fail", ( request, params ) -> {
                throw new IllegalArgumentException();
            } );

        }

        private static WebResponse text( final String text )
        {
            return WebResponse.create().status( HttpStatus.OK ).body( text ).build();
        }
    }
}
