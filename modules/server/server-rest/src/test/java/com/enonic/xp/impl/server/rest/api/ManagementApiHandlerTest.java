package com.enonic.xp.impl.server.rest.api;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.request;
import static com.enonic.xp.impl.server.rest.api.ManagementApiTestSupport.withVirtualHostContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    private static String body( final WebResponse response )
    {
        return String.valueOf( response.getBody() );
    }

    private static final class TestHandler
        extends ManagementApiHandler
    {
        TestHandler()
        {
            super( "server:test" );
            route( HttpMethod.GET, "/", "list", ( request, params ) -> text( "list" ) );
            route( HttpMethod.GET, "/{name}", "get", ( request, params ) -> text( "get:" + params.get( "name" ) ) );
            route( HttpMethod.POST, "/{name}/load", "load", ( request, params ) -> text( "load:" + params.get( "name" ) ) );
            route( HttpMethod.POST, "/prune", "prune", ( request, params ) -> text( "prune" ) );
            route( HttpMethod.POST, "/vacuum", "prune", ( request, params ) -> text( "prune" ) );
            route( HttpMethod.POST, "/parse", "parse", ( request, params ) -> text( body( request, Map.class ).toString() ) );
            route( HttpMethod.POST, "/task", "task", ( request, params ) -> accepted( com.enonic.xp.task.TaskId.from( "t1" ) ) );
        }

        private static WebResponse text( final String text )
        {
            return WebResponse.create().status( HttpStatus.OK ).body( text ).build();
        }
    }
}
