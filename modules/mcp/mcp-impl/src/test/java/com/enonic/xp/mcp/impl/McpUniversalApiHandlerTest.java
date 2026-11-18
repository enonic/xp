package com.enonic.xp.mcp.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

import org.eclipse.jetty.ee11.servlet.ServletContextHandler;
import org.eclipse.jetty.ee11.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.api.ApiDescriptorService;
import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.Applications;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.mcp.McpDescriptor;
import com.enonic.xp.mcp.McpInvocation;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.impl.api.DynamicUniversalApiHandlerRegistry;
import com.enonic.xp.portal.impl.handler.SlashApiHandler;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.portal.sse.SseManager;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.server.VersionInfo;
import com.enonic.xp.site.SiteService;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.impl.serializer.WebSerializerServiceImpl;
import com.enonic.xp.webapp.WebappService;

import io.modelcontextprotocol.json.jackson2.JacksonMcpJsonMapper;
import io.modelcontextprotocol.modern.McpSchema.ProgressParams;
import io.modelcontextprotocol.modern.McpSchema.Tool;
import io.modelcontextprotocol.modern.client.McpClient;
import io.modelcontextprotocol.modern.client.McpSyncClient;
import io.modelcontextprotocol.modern.client.McpSyncSubscriptionListener;
import io.modelcontextprotocol.modern.client.SubscriptionEvent;
import io.modelcontextprotocol.modern.client.transport.HttpClientMcpTransport;
import io.modelcontextprotocol.modern.server.McpRequestContext;
import io.modelcontextprotocol.modern.server.feature.McpSyncTool;
import io.modelcontextprotocol.modern.server.feature.McpSyncToolRepository;
import io.modelcontextprotocol.modern.server.feature.ToolsPage;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static io.modelcontextprotocol.modern.McpSchema.LATEST_PROTOCOL_VERSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@NullMarked
class McpUniversalApiHandlerTest
{
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Duration TIMEOUT = Duration.ofSeconds( 10 );

    private static final String VERSION_META = "io.modelcontextprotocol/protocolVersion";

    private static final Map<String, Object> ECHO_SCHEMA = Map.of( "type", "object", "properties",
        Map.of( "message", Map.of( "type", "string" ) ), "required", List.of( "message" ), "additionalProperties", false );

    @Test
    void discovers_empty_production_repository_with_modern_sdk_client()
        throws Exception
    {
        try ( var endpoint = new Endpoint( new McpUniversalApiHandler( emptyCatalog() ), false ) )
        {
            final var client = endpoint.sdkClient();
            final var discovery = client.discover();
            assertThat( discovery.supportedVersions() ).contains( LATEST_PROTOCOL_VERSION );
            assertThat( discovery.capabilities().tools() ).isNotNull();
            assertThat( discovery.capabilities().tools().listChanged() ).isTrue();
            assertThat( discovery.capabilities().resources() ).isNull();
            assertThat( discovery.capabilities().prompts() ).isNull();
            assertThat( client.listTools().list().tools() ).isEmpty();

            final var raw = json( endpoint.rpc( "server/discover", Map.of() ), 200 ).path( "result" );
            assertThat( raw.path( "capabilities" ).has( "subscriptions" ) ).isFalse();
            assertThat( raw.path( "capabilities" ).path( "tools" ).path( "listChanged" ).asBoolean() ).isTrue();
            assertThat( raw.path( "capabilities" ).has( "resources" ) ).isFalse();
            assertThat( raw.path( "capabilities" ).has( "prompts" ) ).isFalse();
            assertThat( json( endpoint.rpc( "subscriptions/listen", Map.of() ), 400 )
                            .path( "error" ).path( "code" ).asInt() ).isEqualTo( -32602 );
            assertThat( json( endpoint.rpc( "tools/list", Map.of() ), 200 ).path( "result" ).path( "tools" ).isEmpty() ).isTrue();
        }
    }

    @Test
    void calls_injected_echo_fixture_with_sdk_and_replayed_unicode_body()
        throws Exception
    {
        try ( var endpoint = new Endpoint( new McpUniversalApiHandler( new EchoRepository() ), false ) )
        {
            final var client = endpoint.sdkClient();
            assertThat( client.listTools().list().tools() ).singleElement().satisfies(
                tool -> assertThat( tool.name() ).isEqualTo( "echo" ) );
            final var result = client.tool( "echo" ).arguments( Map.of( "message", "Héllo 世界 🌍" ) ).call();
            assertThat( result.isError() ).isNotEqualTo( true );
            assertThat( result.structuredContent() ).isEqualTo( Map.of( "message", "Héllo 世界 🌍" ) );
            assertThat( client.tool( "echo" ).arguments( Map.of( "message", 42 ) ).call().isError() ).isTrue();
            final var raw = json( endpoint.rpc( "tools/call", Map.of( "name", "echo", "arguments",
                Map.of( "message", "Héllo 世界 🌍" ) ) ), 200 ).path( "result" );
            assertThat( raw.path( "structuredContent" ).path( "message" ).asText() ).isEqualTo( "Héllo 世界 🌍" );
            assertThat( raw.path( "content" ).get( 0 ).path( "text" ).asText() ).isEqualTo( "Héllo 世界 🌍" );
        }
    }

    @Test
    void calls_namespaced_catalog_tool_through_script_executor_with_caller_context()
        throws Exception
    {
        final var appKey = ApplicationKey.from( "com.example.tools" );
        final var key = DescriptorKey.from( appKey, "example" );
        final var controller = ResourceKey.from( appKey, "/mcp/example/example.js" );
        final var publicName = "com.example.tools__example__echo";
        final Map<String, Object> arguments = Map.of( "message", "Héllo 世界 🌍" );
        final var authInfo = AuthenticationInfo.create()
            .user( User.create().key( PrincipalKey.from( "user:system:mcp-caller" ) ).login( "mcp-caller" ).build() )
            .principals( RoleKeys.AUTHENTICATED ).build();
        final var application = mock( Application.class );
        when( application.getKey() ).thenReturn( appKey );
        when( application.isStarted() ).thenReturn( true );
        final var applications = mock( ApplicationService.class );
        when( applications.getInstalledApplications() ).thenReturn( Applications.from( application ) );
        final var descriptors = mock( McpDescriptorServiceImpl.class );
        final var tool = Tool.builder( "echo", ECHO_SCHEMA ).outputSchema( ECHO_SCHEMA ).build();
        when( descriptors.getLoadedByApplication( appKey ) ).thenReturn(
            List.of( new LoadedMcpGroup( new McpDescriptor( key, PrincipalKeys.empty() ), List.of( tool ) ) ) );
        when( descriptors.getControllerResourceKey( key ) ).thenReturn( controller );
        final var scripts = mock( PortalScriptService.class );
        final var exports = mock( ScriptExports.class );
        final var value = mock( ScriptValue.class );
        when( scripts.hasScript( controller ) ).thenReturn( true );
        when( scripts.execute( controller ) ).thenReturn( exports );
        when( exports.executeBound( any() ) ).thenCallRealMethod();
        when( exports.hasMethod( "execute" ) ).thenReturn( true );
        when( exports.executeMethod( eq( "execute" ), eq( "echo" ), anyMap(), any( McpInvocation.class ) ) ).thenAnswer( invocation -> {
            final Map<String, @Nullable Object> actualArguments = invocation.getArgument( 2 );
            assertThat( actualArguments ).isEqualTo( arguments );
            final var request = PortalRequestAccessor.get();
            assertThat( request ).isNotNull();
            assertThat( request.getApplicationKey() ).isEqualTo( appKey );
            assertThat( request.getRawPath() ).isEqualTo( McpUniversalApiHandler.ENDPOINT );
            assertThat( request.getRawRequest() ).isNotNull();
            assertThat( ContextAccessor.current().getAuthInfo() ).isSameAs( authInfo );
            return value;
        } );
        when( value.isObject() ).thenReturn( true );
        when( value.getMap() ).thenReturn( Map.of( "content", List.of( Map.of( "type", "text", "text", arguments.get( "message" ) ) ),
            "structuredContent", arguments, "_meta", Map.of( "source", "script" ) ) );
        final var executor = new McpToolExecutorImpl( descriptors, scripts );
        final var sharedCatalog = new McpCatalog( descriptors, applications );
        final var catalog = new McpToolCatalog( sharedCatalog, executor );

        try ( var endpoint = new Endpoint( new McpUniversalApiHandler( catalog ), false, authInfo ) )
        {
            final var listed = json( endpoint.rpc( "tools/list", Map.of() ), 200 ).path( "result" ).path( "tools" );
            assertThat( listed ).hasSize( 1 );
            assertThat( listed.get( 0 ).path( "name" ).asText() ).isEqualTo( publicName );
            final var params = Map.of( "name", publicName, "arguments", arguments );
            final var result = json( endpoint.rpc( "tools/call", params ), 200 ).path( "result" );
            assertThat( result.path( "isError" ).asBoolean() ).isFalse();
            assertThat( result.path( "structuredContent" ) ).isEqualTo( MAPPER.valueToTree( arguments ) );
            assertThat( result.path( "content" ).get( 0 ).path( "text" ).asText() ).isEqualTo( arguments.get( "message" ) );
            assertThat( result.path( "_meta" ).path( "source" ).asText() ).isEqualTo( "script" );
            verify( exports ).executeMethod( eq( "execute" ), eq( "echo" ), eq( arguments ), any( McpInvocation.class ) );

            final var invalidArguments = json( endpoint.rpc( "tools/call",
                Map.of( "name", publicName, "arguments", Map.of( "message", 42 ) ) ), 200 ).path( "result" );
            assertThat( invalidArguments.path( "isError" ).asBoolean() ).isTrue();
            assertThat( invalidArguments.path( "content" ).get( 0 ).path( "text" ).asText() ).containsIgnoringCase( "argument" );
            verify( scripts ).execute( controller );
            verify( exports ).executeMethod( eq( "execute" ), eq( "echo" ), eq( arguments ), any( McpInvocation.class ) );

            when( value.getMap() ).thenReturn( Map.of( "content", "invalid" ) );
            final var invalidResult = json( endpoint.rpc( "tools/call", params ), 200 ).path( "result" );
            assertThat( invalidResult.path( "isError" ).asBoolean() ).isTrue();
            assertThat( invalidResult.path( "content" ).get( 0 ).path( "text" ).asText() ).contains( "content must be a list" );

            when( value.getMap() ).thenReturn( Map.of( "content", List.of(), "structuredContent", Map.of( "message", 42 ) ) );
            final var invalidOutput = json( endpoint.rpc( "tools/call", params ), 200 ).path( "result" );
            assertThat( invalidOutput.path( "isError" ).asBoolean() ).isTrue();
            assertThat( invalidOutput.path( "content" ).get( 0 ).path( "text" ).asText() ).containsIgnoringCase( "output" );

            final var unknown = json( endpoint.rpc( "tools/call", Map.of( "name", "echo", "arguments", arguments ) ), 400 );
            assertThat( unknown.path( "error" ).path( "code" ).asInt() ).isEqualTo( -32602 );
            assertThat( unknown.path( "error" ).path( "message" ).asText() ).contains( "echo" );
            verify( scripts, times( 3 ) ).execute( controller );
            verify( exports, times( 3 ) ).executeMethod( eq( "execute" ), eq( "echo" ), eq( arguments ), any( McpInvocation.class ) );
        }
    }

    @Test
    void streams_script_progress_to_sdk_before_final_result_without_notifying_catalog_subscribers()
        throws Exception
    {
        final var release = new CountDownLatch( 1 );
        final var helpers = new LinkedBlockingQueue<McpInvocation>();
        final var fixture = new ScriptFixture( ( arguments, helper ) -> {
            helpers.add( helper );
            assertThat( helper.isProgressSupported() ).isTrue();
            helper.progress( 0, "Loading " + arguments.get( "message" ) );
            await( release );
            helper.progress( 1, 2, "Loaded first chunk" );
            helper.progress( 2, 2, "Complete" );
        } );
        try ( var endpoint = new Endpoint( fixture.handler(), true ); var workers = Executors.newVirtualThreadPerTaskExecutor() )
        {
            final var client = endpoint.sdkClient();
            final var listener = new RecordingListener();
            final var progress = new LinkedBlockingQueue<ProgressParams>();
            assertThat( client.listTools().list().tools() ).hasSize( 1 );
            try ( var subscription = client.listen().toolsChanged().open( listener ) )
            {
                listener.subscribed();
                final var result = workers.submit( () -> client.tool( CatalogFixture.PUBLIC_NAME )
                    .arguments( Map.of( "message", "Héllo 世界 🌍" ) ).progress( progress::add ).call() );
                try
                {
                    final var first = progress.poll( TIMEOUT.toMillis(), MILLISECONDS );
                    assertThat( first ).isNotNull();
                    assertThat( first.progress() ).isZero();
                    assertThat( first.message() ).isEqualTo( "Loading Héllo 世界 🌍" );
                    assertThat( first.total() ).isNull();
                    assertThat( first.progressToken() ).isNotNull();
                    assertThat( result.isDone() ).isFalse();
                    release.countDown();
                    assertThat( result.get( TIMEOUT.toMillis(), MILLISECONDS ).structuredContent() )
                        .isEqualTo( Map.of( "message", "Héllo 世界 🌍" ) );
                    final var second = progress.poll( TIMEOUT.toMillis(), MILLISECONDS );
                    final var third = progress.poll( TIMEOUT.toMillis(), MILLISECONDS );
                    assertThat( second ).isNotNull();
                    assertThat( third ).isNotNull();
                    assertThat( second.progress() ).isEqualTo( 1 );
                    assertThat( third.progress() ).isEqualTo( 2 );
                    assertThat( second.total() ).isEqualTo( 2 );
                    assertThat( third.total() ).isEqualTo( 2 );
                    assertThat( second.message() ).isEqualTo( "Loaded first chunk" );
                    assertThat( third.message() ).isEqualTo( "Complete" );
                    assertThat( second.progressToken() ).isEqualTo( first.progressToken() );
                    assertThat( third.progressToken() ).isEqualTo( first.progressToken() );
                    assertInactive( helpers.remove() );
                    assertThat( progress.poll( 250, MILLISECONDS ) ).isNull();
                    listener.quiet();
                }
                finally
                {
                    release.countDown();
                }
            }
        }
    }

    @Test
    void emits_exact_sse_frames_with_string_and_integer_progress_tokens()
        throws Exception
    {
        for ( final var token : List.of( "chunk-token", 12655 ) )
        {
            final var release = new CountDownLatch( 1 );
            final var helpers = new LinkedBlockingQueue<McpInvocation>();
            final var fixture = new ScriptFixture( ( arguments, helper ) -> {
                helpers.add( helper );
                helper.progress( 0 );
                await( release );
                helper.progress( 0, "duplicate" );
                helper.progress( 1, "Read chunk" );
                helper.progress( 0.5 );
                helper.progress( 2, 3 );
                helper.progress( 3, 3, "Done" );
            } );
            try ( var endpoint = new Endpoint( fixture.handler(), false );
                  var stream = endpoint.stream( token, "chunks", null ) )
            {
                try
                {
                    assertProgressFrame( stream.frame(), token, 0, null, null );
                    release.countDown();
                    assertProgressFrame( stream.frame(), token, 1, null, "Read chunk" );
                    assertProgressFrame( stream.frame(), token, 2, 3, null );
                    assertProgressFrame( stream.frame(), token, 3, 3, "Done" );
                    assertResultFrame( stream.frame(), "chunks" );
                    assertInactive( helpers.remove() );
                    assertThat( stream.frame() ).isEmpty();
                    verify( fixture.exports ).executeMethod( eq( "execute" ), eq( "echo" ),
                        eq( Map.of( "message", "chunks" ) ), any( McpInvocation.class ) );
                }
                finally
                {
                    release.countDown();
                }
            }
        }
    }

    @Test
    void concurrent_progress_streams_keep_tokens_arguments_and_caller_context_isolated()
        throws Exception
    {
        final var entered = new CountDownLatch( 2 );
        final var release = new CountDownLatch( 1 );
        final var fixture = new ScriptFixture( ( arguments, helper ) -> {
            final var message = arguments.get( "message" );
            final var auth = ContextAccessor.current().getAuthInfo();
            final var request = PortalRequestAccessor.get();
            assertThat( auth.getUser().getLogin() ).isEqualTo( message );
            assertThat( request ).isNotNull();
            assertThat( request.getRawRequest().getHeader( "X-Mcp-Caller" ) ).isEqualTo( message );
            helper.progress( 0, "Loading " + message );
            entered.countDown();
            await( release );
            assertThat( ContextAccessor.current().getAuthInfo() ).isSameAs( auth );
            assertThat( PortalRequestAccessor.get() ).isSameAs( request );
            assertThat( request.getRawRequest().getHeader( "X-Mcp-Caller" ) ).isEqualTo( message );
            assertThat( auth.getUser().getLogin() ).isEqualTo( message );
            helper.progress( 1, 1, "Finished " + message );
        } );
        try ( var endpoint = new Endpoint( fixture.handler(), false );
              var first = endpoint.stream( "alice-token", "alice", "alice" );
              var second = endpoint.stream( 42, "bob", "bob" ) )
        {
            try
            {
                await( entered );
                assertProgressFrame( first.frame(), "alice-token", 0, null, "Loading alice" );
                assertProgressFrame( second.frame(), 42, 0, null, "Loading bob" );
                release.countDown();
                assertProgressFrame( first.frame(), "alice-token", 1, 1, "Finished alice" );
                assertProgressFrame( second.frame(), 42, 1, 1, "Finished bob" );
                assertResultFrame( first.frame(), "alice" );
                assertResultFrame( second.frame(), "bob" );
                assertThat( first.frame() ).isEmpty();
                assertThat( second.frame() ).isEmpty();
            }
            finally
            {
                release.countDown();
            }
        }
    }

    @Test
    void no_token_returns_json_with_noop_helper_and_invalid_arguments_never_execute_or_stream()
        throws Exception
    {
        final var fixture = new ScriptFixture( ( arguments, helper ) -> assertInactive( helper ) );
        try ( var endpoint = new Endpoint( fixture.handler(), false ) )
        {
            final var result = json( endpoint.rpc( "tools/call", Map.of( "name", CatalogFixture.PUBLIC_NAME,
                "arguments", Map.of( "message", "synchronous" ) ) ), 200 ).path( "result" );
            assertThat( result.path( "structuredContent" ).path( "message" ).asText() ).isEqualTo( "synchronous" );
            final var invalid = json( endpoint.rpc( "tools/call", Map.of( "name", CatalogFixture.PUBLIC_NAME,
                "arguments", Map.of( "message", 42 ), "_meta", Map.of( "progressToken", "invalid" ) ) ), 200 );
            assertThat( invalid.path( "result" ).path( "isError" ).asBoolean() ).isTrue();
            verify( fixture.scripts ).execute( fixture.controller );
            verify( fixture.exports ).executeMethod( eq( "execute" ), eq( "echo" ),
                eq( Map.of( "message", "synchronous" ) ), any( McpInvocation.class ) );
        }
    }

    @Test
    void invalid_progress_tokens_return_protocol_errors_before_controller_execution()
        throws Exception
    {
        final var fixture = new ScriptFixture( ( arguments, helper ) -> {
            throw new AssertionError( "Invalid token reached controller" );
        } );
        try ( var endpoint = new Endpoint( fixture.handler(), false ) )
        {
            for ( final var token : List.of( 1.5, true, List.of( "token" ), Map.of( "token", "value" ) ) )
            {
                final var response = json( endpoint.rpc( "tools/call", Map.of( "name", CatalogFixture.PUBLIC_NAME,
                    "arguments", Map.of( "message", "invalid-token" ), "_meta", Map.of( "progressToken", token ) ) ), 400 );
                assertThat( response.path( "error" ).path( "code" ).asInt() ).isEqualTo( -32602 );
                assertThat( response.path( "error" ).path( "message" ).asText() ).contains( "progressToken" );
            }
            verifyNoInteractions( fixture.scripts );
        }
    }

    @Test
    void disconnected_progress_stream_allows_bounded_script_teardown_and_subsequent_calls()
        throws Exception
    {
        final var release = new CountDownLatch( 1 );
        final var finished = new CountDownLatch( 1 );
        final var fixture = new ScriptFixture( ( arguments, helper ) -> {
            if ( helper.isProgressSupported() )
            {
                try
                {
                    helper.progress( 0, "Connected" );
                    await( release );
                    helper.progress( 1, "Disconnected" );
                }
                finally
                {
                    finished.countDown();
                }
            }
        } );
        try ( var endpoint = new Endpoint( fixture.handler(), false ) )
        {
            try ( var stream = endpoint.stream( "disconnect", "closed", null ) )
            {
                assertProgressFrame( stream.frame(), "disconnect", 0, null, "Connected" );
            }
            finally
            {
                release.countDown();
            }
            await( finished );
            assertThat( json( endpoint.rpc( "tools/call", Map.of( "name", CatalogFixture.PUBLIC_NAME,
                "arguments", Map.of( "message", "healthy" ) ) ), 200 ).path( "result" ).path( "structuredContent" )
                .path( "message" ).asText() ).isEqualTo( "healthy" );
        }
    }

    @Test
    void progress_stream_finishes_with_tool_error_for_invalid_controller_output()
        throws Exception
    {
        for ( final var output : List.<Map<String, @Nullable Object>>of( Map.of( "content", "invalid" ),
            Map.of( "content", List.of(), "structuredContent", Map.of( "message", 42 ) ) ) )
        {
            final var helpers = new LinkedBlockingQueue<McpInvocation>();
            final var fixture = new ScriptFixture( ( arguments, helper ) -> {
                helpers.add( helper );
                helper.progress( 0, "Started" );
            }, arguments -> output );
            try ( var endpoint = new Endpoint( fixture.handler(), false );
                  var stream = endpoint.stream( "invalid-output", "error", null ) )
            {
                assertProgressFrame( stream.frame(), "invalid-output", 0, null, "Started" );
                final var result = frameJson( stream.frame() ).path( "result" );
                assertThat( result.path( "isError" ).asBoolean() ).isTrue();
                assertThat( result.path( "content" ).get( 0 ).path( "text" ).asText() ).contains( "MCP tool" );
                assertInactive( helpers.remove() );
                assertThat( stream.frame() ).isEmpty();
            }
        }
    }

    @Test
    void progress_stream_finishes_with_protocol_error_for_controller_exception()
        throws Exception
    {
        final var helpers = new LinkedBlockingQueue<McpInvocation>();
        final var fixture = new ScriptFixture( ( arguments, helper ) -> {
            helpers.add( helper );
            helper.progress( 0, "Started" );
            throw new IllegalStateException( "Private controller details" );
        } );
        try ( var endpoint = new Endpoint( fixture.handler(), false );
              var stream = endpoint.stream( "controller-error", "error", null ) )
        {
            assertProgressFrame( stream.frame(), "controller-error", 0, null, "Started" );
            final var response = frameJson( stream.frame() );
            assertThat( response.path( "error" ).path( "code" ).asInt() ).isEqualTo( -32603 );
            assertThat( response.toString() ).doesNotContain( "Private controller details" );
            assertInactive( helpers.remove() );
            assertThat( stream.frame() ).isEmpty();
        }
    }

    @Test
    void routes_registered_web_api_through_real_registry_and_slash_handler()
        throws Exception
    {
        try ( var endpoint = new Endpoint( new McpUniversalApiHandler( emptyCatalog() ), true ) )
        {
            assertThat( endpoint.sdkClient().listTools().list().tools() ).isEmpty();
            assertThat( json( endpoint.rpc( "server/discover", Map.of() ), 200 ).has( "result" ) ).isTrue();
        }
    }

    @Test
    void rejects_unknown_tools_and_cursors()
        throws Exception
    {
        try ( var endpoint = new Endpoint( new McpUniversalApiHandler( emptyCatalog() ), false ) )
        {
            final var unknown = json( endpoint.rpc( "tools/call", Map.of( "name", "echo", "arguments", Map.of() ) ), 400 );
            assertThat( unknown.path( "error" ).path( "message" ).asText() ).containsIgnoringCase( "echo" );
            final var cursor = json( endpoint.rpc( "tools/list", Map.of( "cursor", "unknown" ) ), 400 );
            assertThat( cursor.path( "error" ).path( "code" ).asInt() ).isEqualTo( -32602 );
            assertThat( cursor.path( "error" ).path( "message" ).asText() ).contains( "cursor" );
        }
    }

    @Test
    void rejects_malformed_json_and_batches()
        throws Exception
    {
        try ( var endpoint = new Endpoint( new McpUniversalApiHandler( emptyCatalog() ), false ) )
        {
            assertThat( json( endpoint.post( "{", "application/json", Map.of() ), 400 )
                            .path( "error" ).path( "code" ).asInt() ).isEqualTo( -32700 );
            final var batch = "[" + request( "tools/list", Map.of() ) + "]";
            assertThat( json( endpoint.post( batch, "application/json", headers( "tools/list" ) ), 400 )
                            .path( "error" ).path( "code" ).asInt() ).isEqualTo( -32600 );
        }
    }

    @Test
    void rejects_wrong_media_type_legacy_verbs_and_subpaths()
        throws Exception
    {
        try ( var endpoint = new Endpoint( new McpUniversalApiHandler( emptyCatalog() ), false ) )
        {
            assertThat( endpoint.post( "{}", "text/plain", Map.of() ).statusCode() ).isEqualTo( 415 );
            for ( final var method : List.of( "GET", "DELETE" ) )
            {
                assertThat( endpoint.send( HttpRequest.newBuilder( endpoint.uri ).method( method, HttpRequest.BodyPublishers.noBody() ) )
                                .statusCode() ).isEqualTo( 405 );
            }
            assertThat( endpoint.send( HttpRequest.newBuilder( URI.create( endpoint.uri + "/unknown" ) )
                .POST( HttpRequest.BodyPublishers.ofString( "{}" ) ) ).statusCode() ).isEqualTo( 404 );
        }
    }

    @Test
    void validates_missing_mismatched_and_duplicate_mirror_headers()
        throws Exception
    {
        try ( var endpoint = new Endpoint( new McpUniversalApiHandler( emptyCatalog() ), false ) )
        {
            final var body = request( "tools/call", Map.of( "name", "echo", "arguments", Map.of() ) );
            final var valid = headers( "tools/call" );
            valid.put( "Mcp-Name", List.of( "echo" ) );
            for ( final var name : List.of( "Mcp-Method", "MCP-Protocol-Version", "Mcp-Name" ) )
            {
                final var missing = new LinkedHashMap<>( valid );
                missing.remove( name );
                assertHeaderError( endpoint.post( body, "application/json", missing ), name );
                final var mismatch = new LinkedHashMap<>( valid );
                mismatch.put( name, List.of( "wrong" ) );
                assertHeaderError( endpoint.post( body, "application/json", mismatch ), name );
                final var duplicate = new LinkedHashMap<>( valid );
                duplicate.put( name, List.of( valid.get( name ).getFirst(), valid.get( name ).getFirst() ) );
                assertHeaderError( endpoint.post( body, "application/json", duplicate ), name );
            }
        }
    }

    @Test
    void reports_unsupported_protocol_and_legacy_initialize()
        throws Exception
    {
        try ( var endpoint = new Endpoint( new McpUniversalApiHandler( emptyCatalog() ), false ) )
        {
            final var params = Map.of( "_meta", Map.of( VERSION_META, "1900-01-01" ) );
            final var response = json( endpoint.post( requestWithoutMeta( "tools/list", params ), "application/json",
                Map.of( "Mcp-Method", List.of( "tools/list" ), "MCP-Protocol-Version", List.of( "1900-01-01" ) ) ), 400 );
            assertThat( response.path( "error" ).path( "message" ).asText() ).containsIgnoringCase( "version" );
            final var initialize = endpoint.post( requestWithoutMeta( "initialize", Map.of( "protocolVersion", "2025-11-25",
                "capabilities", Map.of(), "clientInfo", Map.of( "name", "test", "version", "1" ) ) ),
                "application/json", Map.of() );
            final var error = json( initialize, 400 ).path( "error" );
            assertThat( error.path( "message" ).asText() ).contains( "initialize", LATEST_PROTOCOL_VERSION );
        }
    }

    @Test
    void accepts_notifications_and_rejects_requests_after_deactivation()
        throws Exception
    {
        final var handler = new McpUniversalApiHandler( emptyCatalog() );
        try ( var endpoint = new Endpoint( handler, false ) )
        {
            final var response = endpoint.post( "{\"jsonrpc\":\"2.0\",\"method\":\"notifications/initialized\"}",
                "application/json", Map.of() );
            assertThat( response.statusCode() ).isEqualTo( 202 );
            assertThat( response.body() ).isEmpty();
            handler.deactivate();
            assertThat( endpoint.rpc( "tools/list", Map.of() ).statusCode() ).isEqualTo( 503 );
        }
    }

    @Test
    void preserves_sdk_default_origin_rejection()
        throws Exception
    {
        try ( var endpoint = new Endpoint( new McpUniversalApiHandler( emptyCatalog() ), false ) )
        {
            final var headers = headers( "tools/list" );
            headers.put( "Origin", List.of( "https://untrusted.example" ) );
            assertThat( endpoint.post( request( "tools/list", Map.of() ), "application/json", headers ).statusCode() ).isEqualTo( 403 );
        }
    }

    @Test
    void streams_installed_updated_and_removed_tools_through_routed_deferred_replay()
        throws Exception
    {
        final var fixture = new CatalogFixture();
        final var factory = new McpServerFactory( fixture.catalog, fixture.sharedCatalog );
        try ( var endpoint = new Endpoint( new McpUniversalApiHandler( factory ), true ) )
        {
            final var client = endpoint.sdkClient();
            assertThat( client.listTools().list().tools() ).isEmpty();
            final var listener = new RecordingListener();
            try ( var listening = client.listen().toolsChanged().open( listener ) )
            {
                listener.subscribed();
                fixture.install( "Initial echo" );
                listener.changed();
                assertThat( client.listTools().list().tools() ).singleElement().satisfies( tool -> {
                    assertThat( tool.name() ).isEqualTo( CatalogFixture.PUBLIC_NAME );
                    assertThat( tool.description() ).isEqualTo( "Initial echo" );
                } );
                assertThat( client.tool( CatalogFixture.PUBLIC_NAME ).arguments( Map.of( "message", "Héllo 世界 🌍" ) )
                                .call().isError() ).isNotEqualTo( true );
                verify( fixture.executor ).execute( fixture.key, "echo", Map.of( "message", "Héllo 世界 🌍" ) );

                fixture.install( "Updated echo" );
                listener.changed();
                assertThat( client.listTools().list().tools() ).singleElement().satisfies(
                    tool -> assertThat( tool.description() ).isEqualTo( "Updated echo" ) );
                fixture.sharedCatalog.refresh();
                listener.quiet();

                fixture.remove();
                listener.changed();
                assertThat( client.listTools().list().tools() ).isEmpty();
                final var stale = json( endpoint.rpc( "tools/call", Map.of( "name", CatalogFixture.PUBLIC_NAME,
                    "arguments", Map.of( "message", "stale" ) ) ), 400 );
                assertThat( stale.path( "error" ).path( "code" ).asInt() ).isEqualTo( -32602 );
                verify( fixture.executor, times( 1 ) ).execute( any(), any(), anyMap() );

                for ( int i = 0; i < 3; i++ )
                {
                    fixture.install( "Echo " + i );
                    listener.changed();
                    assertThat( client.listTools().list().tools() ).hasSize( 1 );
                    fixture.remove();
                    listener.changed();
                    assertThat( client.listTools().list().tools() ).isEmpty();
                }
                listener.quiet();
            }
        }
    }

    @Test
    void rejects_tool_removed_between_http_lookup_and_execution()
        throws Exception
    {
        final var fixture = new CatalogFixture();
        fixture.install( "Stale binding" );
        try ( var endpoint = new Endpoint( new McpUniversalApiHandler( fixture.catalog, fixture.sharedCatalog.changes() ), false ) )
        {
            final var client = endpoint.sdkClient();
            assertThat( client.listTools().list().tools() ).hasSize( 1 );
            final var listener = new RecordingListener();
            try ( var listening = client.listen().toolsChanged().open( listener ) )
            {
                listener.subscribed();
                when( fixture.applications.getInstalledApplications() ).thenReturn(
                    Applications.from( fixture.application ), Applications.empty() );
                final var response = json( endpoint.rpc( "tools/call", Map.of( "name", CatalogFixture.PUBLIC_NAME,
                    "arguments", Map.of( "message", "stale" ) ) ), 400 );
                assertThat( response.path( "error" ).path( "code" ).asInt() ).isEqualTo( -32602 );
                assertThat( response.path( "error" ).path( "message" ).asText() ).contains( "no longer available" );
                listener.changed();
                assertThat( client.listTools().list().tools() ).isEmpty();
                verifyNoInteractions( fixture.executor );
            }
        }
    }

    @Test
    void broadcasts_to_multiple_clients_and_allows_disconnect_and_reconnect()
        throws Exception
    {
        final var fixture = new CatalogFixture();
        fixture.sharedCatalog.refresh();
        try ( var endpoint = new Endpoint( new McpUniversalApiHandler( fixture.catalog, fixture.sharedCatalog.changes() ), false ) )
        {
            final var first = new RecordingListener();
            final var second = new RecordingListener();
            final var firstClient = endpoint.sdkClient();
            final var secondClient = endpoint.sdkClient();
            try ( var firstStream = firstClient.listen().toolsChanged().open( first );
                  var secondStream = secondClient.listen().toolsChanged().open( second ) )
            {
                first.subscribed();
                second.subscribed();
                fixture.install( "Both listeners" );
                first.changed();
                second.changed();
                firstStream.close();
                fixture.remove();
                second.changed();
                first.quiet();
                final var reconnected = new RecordingListener();
                try ( var reconnectedStream = firstClient.listen().toolsChanged().open( reconnected ) )
                {
                    reconnected.subscribed();
                    fixture.install( "Reconnected" );
                    second.changed();
                    reconnected.changed();
                    assertThat( firstClient.listTools().list().tools() ).hasSize( 1 );
                    assertThat( secondClient.listTools().list().tools() ).hasSize( 1 );
                    first.quiet();
                }
            }
        }
    }

    @Test
    void acknowledges_only_supported_tool_filters()
        throws Exception
    {
        final var fixture = new CatalogFixture();
        fixture.sharedCatalog.refresh();
        try ( var endpoint = new Endpoint( new McpUniversalApiHandler( fixture.catalog, fixture.sharedCatalog.changes() ), false ) )
        {
            final var listener = new RecordingListener();
            try ( var listening = endpoint.sdkClient().listen().toolsChanged().resourcesChanged().promptsChanged()
                .resourceUpdates( "test://resource" ).open( listener ) )
            {
                final var subscribed = listener.subscribed();
                assertThat( subscribed.honoured().wantsToolsListChanged() ).isTrue();
                assertThat( subscribed.honoured().wantsResourcesListChanged() ).isFalse();
                assertThat( subscribed.honoured().wantsPromptsListChanged() ).isFalse();
                assertThat( subscribed.honoured().resourceSubscriptionsOrEmpty() ).isEmpty();
                assertThat( subscribed.missing().wantsResourcesListChanged() ).isTrue();
                assertThat( subscribed.missing().wantsPromptsListChanged() ).isTrue();
                assertThat( subscribed.missing().resourceSubscriptionsOrEmpty() ).containsExactly( "test://resource" );
                fixture.install( "Tools only" );
                listener.changed();
                listener.quiet();
            }
        }
    }

    @Test
    void graceful_shutdown_completes_all_subscription_streams()
        throws Exception
    {
        final var fixture = new CatalogFixture();
        fixture.sharedCatalog.refresh();
        final var handler = new McpUniversalApiHandler( fixture.catalog, fixture.sharedCatalog.changes() );
        try ( var endpoint = new Endpoint( handler, false ) )
        {
            final var first = new RecordingListener();
            final var second = new RecordingListener();
            try ( var firstStream = endpoint.sdkClient().listen().toolsChanged().open( first );
                  var secondStream = endpoint.sdkClient().listen().toolsChanged().open( second ) )
            {
                first.subscribed();
                second.subscribed();
                handler.deactivate();
                first.completed();
                second.completed();
                fixture.install( "After shutdown" );
                first.quiet();
                second.quiet();
                assertThat( endpoint.rpc( "tools/list", Map.of() ).statusCode() ).isEqualTo( 503 );
            }
        }
    }

    private static void await( final CountDownLatch latch )
        throws InterruptedException
    {
        assertThat( latch.await( TIMEOUT.toMillis(), MILLISECONDS ) ).isTrue();
    }

    private static void assertInactive( final McpInvocation helper )
    {
        assertThat( helper.isProgressSupported() ).isFalse();
        assertDoesNotThrow( () -> {
            helper.progress( 100 );
            helper.progress( 101, "late" );
            helper.progress( 102, 103 );
            helper.progress( 103, 103, "late" );
        } );
    }

    private static JsonNode frameJson( final String frame )
        throws IOException
    {
        assertThat( frame ).startsWith( "event: message\ndata: " ).endsWith( "\n\n" );
        return MAPPER.readTree( frame.substring( "event: message\ndata: ".length(), frame.length() - 2 ) );
    }

    private static void assertProgressFrame( final String frame, final Object token, final double progress,
                                            final @Nullable Number total, final @Nullable String message )
        throws IOException
    {
        final var params = new LinkedHashMap<String, Object>();
        params.put( "progressToken", token );
        params.put( "progress", progress );
        if ( total != null )
        {
            params.put( "total", total.doubleValue() );
        }
        if ( message != null )
        {
            params.put( "message", message );
        }
        final var expected = MAPPER.createObjectNode();
        expected.put( "jsonrpc", "2.0" );
        expected.put( "method", "notifications/progress" );
        expected.set( "params", MAPPER.valueToTree( params ) );
        assertThat( frameJson( frame ) ).isEqualTo( expected );
        assertThat( frame ).isEqualTo( "event: message\ndata: " + MAPPER.writeValueAsString( expected ) + "\n\n" );
    }

    private static void assertResultFrame( final String frame, final String message )
        throws IOException
    {
        final var expected = MAPPER.createObjectNode();
        expected.put( "jsonrpc", "2.0" );
        expected.put( "id", 1 );
        final var result = expected.putObject( "result" );
        final var text = result.putArray( "content" ).addObject();
        text.put( "type", "text" );
        text.put( "text", MAPPER.writeValueAsString( Map.of( "message", message ) ) );
        result.putObject( "structuredContent" ).put( "message", message );
        result.put( "resultType", "complete" );
        final var serverInfo = result.putObject( "_meta" ).putObject( "io.modelcontextprotocol/serverInfo" );
        serverInfo.put( "name", "Enonic XP" );
        serverInfo.put( "version", VersionInfo.get().getVersion() );
        assertThat( frameJson( frame ) ).isEqualTo( expected );
        assertThat( frame ).isEqualTo( "event: message\ndata: " + MAPPER.writeValueAsString( expected ) + "\n\n" );
    }

    private static AuthenticationInfo caller( final String login )
    {
        return AuthenticationInfo.create()
            .user( User.create().key( PrincipalKey.from( "user:system:" + login ) ).login( login ).build() )
            .principals( RoleKeys.AUTHENTICATED ).build();
    }

    private static void assertHeaderError( final HttpResponse<String> response, final String name )
        throws IOException
    {
        final var error = json( response, 400 ).path( "error" );
        assertThat( error.path( "code" ).asInt() ).isEqualTo( -32020 );
        assertThat( error.path( "message" ).asText() ).containsIgnoringCase( name );
    }

    private static McpToolCatalog emptyCatalog()
    {
        final var applications = mock( ApplicationService.class );
        when( applications.getInstalledApplications() ).thenReturn( Applications.empty() );
        final var sharedCatalog = new McpCatalog( mock( McpDescriptorServiceImpl.class ), applications );
        return new McpToolCatalog( sharedCatalog, mock( McpToolExecutor.class ) );
    }

    private static JsonNode json( final HttpResponse<String> response, final int status )
        throws IOException
    {
        assertThat( response.statusCode() ).as( response.body() ).isEqualTo( status );
        assertThat( response.headers().firstValue( "Content-Type" ).orElse( "" ) ).startsWith( "application/json" );
        assertThat( response.body() ).doesNotContain( "event:", "data:" );
        return MAPPER.readTree( response.body() );
    }

    private static Map<String, List<String>> headers( final String method )
    {
        final var result = new LinkedHashMap<String, List<String>>();
        result.put( "Mcp-Method", List.of( method ) );
        result.put( "MCP-Protocol-Version", List.of( LATEST_PROTOCOL_VERSION ) );
        return result;
    }

    private static String request( final String method, final Map<String, ?> params )
        throws IOException
    {
        final var versioned = new LinkedHashMap<String, Object>( params );
        final var meta = new LinkedHashMap<String, Object>();
        if ( params.get( "_meta" ) instanceof Map<?, ?> supplied )
        {
            supplied.forEach( ( key, value ) -> meta.put( (String) key, value ) );
        }
        meta.put( VERSION_META, LATEST_PROTOCOL_VERSION );
        meta.put( "io.modelcontextprotocol/clientCapabilities", Map.of() );
        versioned.put( "_meta", meta );
        return requestWithoutMeta( method, versioned );
    }

    private static String requestWithoutMeta( final String method, final Map<String, ?> params )
        throws IOException
    {
        return MAPPER.writeValueAsString( Map.of( "jsonrpc", "2.0", "id", 1, "method", method, "params", params ) );
    }

    private static final class RecordingListener
        implements McpSyncSubscriptionListener
    {
        private final BlockingQueue<Object> signals = new LinkedBlockingQueue<>();

        @Override
        public void onEvent( final SubscriptionEvent event )
        {
            signals.add( event );
        }

        @Override
        public void onError( final Throwable error )
        {
            signals.add( error );
        }

        @Override
        public void onComplete()
        {
            signals.add( "complete" );
        }

        private SubscriptionEvent.Subscribed subscribed()
            throws InterruptedException
        {
            final var signal = signals.poll( TIMEOUT.toMillis(), MILLISECONDS );
            assertThat( signal ).isInstanceOf( SubscriptionEvent.Subscribed.class );
            final var subscribed = (SubscriptionEvent.Subscribed) signal;
            assertThat( subscribed.honoured().wantsToolsListChanged() ).isTrue();
            return subscribed;
        }

        private void changed()
            throws InterruptedException
        {
            assertThat( signals.poll( TIMEOUT.toMillis(), MILLISECONDS ) ).isInstanceOf( SubscriptionEvent.ToolsListChanged.class );
        }

        private void completed()
            throws InterruptedException
        {
            assertThat( signals.poll( TIMEOUT.toMillis(), MILLISECONDS ) ).isEqualTo( "complete" );
        }

        private void quiet()
            throws InterruptedException
        {
            assertThat( signals.poll( 250, MILLISECONDS ) ).isNull();
        }
    }

    @FunctionalInterface
    private interface ScriptProgress
    {
        void execute( Map<String, @Nullable Object> arguments, McpInvocation helper )
            throws Exception;
    }

    private static final class ScriptFixture
    {
        private final PortalScriptService scripts = mock( PortalScriptService.class );

        private final ScriptExports exports = mock( ScriptExports.class );

        private final DescriptorKey key = DescriptorKey.from( "com.example.tools:example" );

        private final ResourceKey controller = ResourceKey.from( key.getApplicationKey(), "/mcp/example/example.js" );

        private final McpCatalog sharedCatalog;

        private final McpToolCatalog catalog;

        private ScriptFixture( final ScriptProgress action )
        {
            this( action, arguments -> Map.of( "content", List.of(), "structuredContent", arguments ) );
        }

        private ScriptFixture( final ScriptProgress action,
                               final Function<Map<String, @Nullable Object>, Map<String, @Nullable Object>> resultMapper )
        {
            final var applications = mock( ApplicationService.class );
            final var application = mock( Application.class );
            when( application.getKey() ).thenReturn( key.getApplicationKey() );
            when( application.isStarted() ).thenReturn( true );
            when( applications.getInstalledApplications() ).thenReturn( Applications.from( application ) );
            final var descriptors = mock( McpDescriptorServiceImpl.class );
            when( descriptors.getLoadedByApplication( key.getApplicationKey() ) ).thenReturn( List.of(
                new LoadedMcpGroup( new McpDescriptor( key, PrincipalKeys.empty() ),
                    List.of( Tool.builder( "echo", ECHO_SCHEMA ).outputSchema( ECHO_SCHEMA ).build() ) ) ) );
            when( descriptors.getControllerResourceKey( key ) ).thenReturn( controller );
            when( scripts.hasScript( controller ) ).thenReturn( true );
            when( scripts.execute( controller ) ).thenReturn( exports );
            when( exports.executeBound( any() ) ).thenCallRealMethod();
            when( exports.hasMethod( "execute" ) ).thenReturn( true );
            when( exports.executeMethod( eq( "execute" ), eq( "echo" ), anyMap(), any( McpInvocation.class ) ) )
                .thenAnswer( invocation -> {
                    final Map<String, @Nullable Object> arguments = invocation.getArgument( 2 );
                    final McpInvocation helper = invocation.getArgument( 3 );
                    assertThat( helper ).isSameAs( McpInvocationScope.current() );
                    final var request = PortalRequestAccessor.get();
                    assertThat( request ).isNotNull();
                    assertThat( request.getApplicationKey() ).isEqualTo( key.getApplicationKey() );
                    assertThat( request.getRawPath() ).isEqualTo( McpUniversalApiHandler.ENDPOINT );
                    assertThat( request.getRawRequest() ).isNotNull();
                    action.execute( arguments, helper );
                    final var result = mock( ScriptValue.class );
                    when( result.isObject() ).thenReturn( true );
                    when( result.getMap() ).thenReturn( resultMapper.apply( arguments ) );
                    return result;
                } );
            sharedCatalog = new McpCatalog( descriptors, applications );
            catalog = new McpToolCatalog( sharedCatalog, new McpToolExecutorImpl( descriptors, scripts ) );
        }

        private McpUniversalApiHandler handler()
        {
            return new McpUniversalApiHandler( new McpServerFactory( catalog, sharedCatalog ) );
        }
    }

    private static final class SseStream
        implements AutoCloseable
    {
        private final InputStream input;

        private final ExecutorService reader = Executors.newVirtualThreadPerTaskExecutor();

        private SseStream( final HttpResponse<InputStream> response )
        {
            input = response.body();
            assertThat( response.statusCode() ).isEqualTo( 200 );
            assertThat( response.headers().firstValue( "Content-Type" ).orElse( "" ) ).startsWith( "text/event-stream" );
        }

        private String frame()
            throws Exception
        {
            return reader.submit( () -> {
                final var bytes = new ByteArrayOutputStream();
                int previous = -1;
                int current;
                while ( ( current = input.read() ) != -1 )
                {
                    bytes.write( current );
                    if ( previous == '\n' && current == '\n' )
                    {
                        break;
                    }
                    previous = current;
                }
                return bytes.toString( UTF_8 );
            } ).get( TIMEOUT.toMillis(), MILLISECONDS );
        }

        @Override
        public void close()
            throws IOException, InterruptedException
        {
            try
            {
                input.close();
            }
            finally
            {
                reader.shutdownNow();
                assertThat( reader.awaitTermination( TIMEOUT.toMillis(), MILLISECONDS ) ).isTrue();
            }
        }
    }

    private static final class CatalogFixture
    {
        private static final String PUBLIC_NAME = "com.example.tools__example__echo";

        private final ApplicationService applications = mock( ApplicationService.class );

        private final McpDescriptorServiceImpl descriptors = mock( McpDescriptorServiceImpl.class );

        private final McpToolExecutor executor = mock( McpToolExecutor.class );

        private final Application application = mock( Application.class );

        private final DescriptorKey key = DescriptorKey.from( "com.example.tools:example" );

        private final McpCatalog sharedCatalog = new McpCatalog( descriptors, applications );

        private final McpToolCatalog catalog = new McpToolCatalog( sharedCatalog, executor );

        private CatalogFixture()
        {
            when( applications.getInstalledApplications() ).thenReturn( Applications.empty() );
            when( application.getKey() ).thenReturn( key.getApplicationKey() );
            when( application.isStarted() ).thenReturn( true );
            when( executor.execute( eq( key ), eq( "echo" ), anyMap() ) ).thenAnswer( invocation -> {
                final Map<String, @Nullable Object> arguments = invocation.getArgument( 2 );
                return Map.of( "content", List.of(), "structuredContent", arguments );
            } );
        }

        private void install( final String description )
        {
            when( applications.getInstalledApplications() ).thenReturn( Applications.from( application ) );
            when( descriptors.getLoadedByApplication( key.getApplicationKey() ) ).thenReturn( List.of(
                new LoadedMcpGroup( new McpDescriptor( key, PrincipalKeys.empty() ),
                    List.of( Tool.builder( "echo", ECHO_SCHEMA ).description( description ).outputSchema( ECHO_SCHEMA ).build() ) ) ) );
            sharedCatalog.refresh();
        }

        private void remove()
        {
            when( applications.getInstalledApplications() ).thenReturn( Applications.empty() );
            sharedCatalog.refresh();
        }
    }

    private static final class EchoRepository
        implements McpSyncToolRepository
    {
        private final McpSyncTool echo = McpToolAdapter.bind( Tool.builder( "echo", ECHO_SCHEMA ).outputSchema( ECHO_SCHEMA ).build(),
            ( name, arguments ) -> Map.of( "content", List.of( Map.of( "type", "text", "text", arguments.get( "message" ) ) ),
                "structuredContent", arguments ) );

        @Override
        public ToolsPage list( final McpRequestContext context, final @Nullable String cursor )
        {
            return ToolsPage.of( List.of( echo.definition() ) );
        }

        @Override
        public @Nullable McpSyncTool find( final McpRequestContext context, final String name )
        {
            return "echo".equals( name ) ? echo : null;
        }
    }

    private static final class Endpoint
        implements AutoCloseable
    {
        private final McpUniversalApiHandler handler;

        private final Server server = new Server();

        private final HttpClient http = HttpClient.newBuilder().version( HttpClient.Version.HTTP_1_1 ).connectTimeout( TIMEOUT ).build();

        private final URI uri;

        private Endpoint( final McpUniversalApiHandler handler, final boolean routed )
            throws Exception
        {
            this( handler, routed, AuthenticationInfo.unAuthenticated() );
        }

        private Endpoint( final McpUniversalApiHandler handler, final boolean routed, final AuthenticationInfo authInfo )
            throws Exception
        {
            this.handler = handler;
            final Function<WebRequest, WebResponse> dispatch = routed ? routing( handler ) : handler::handle;
            final var connector = new ServerConnector( server );
            connector.setHost( "localhost" );
            connector.setPort( 0 );
            server.addConnector( connector );
            final var servletContext = new ServletContextHandler();
            servletContext.setContextPath( "/" );
            final var holder = new ServletHolder( new HttpServlet()
            {
                @Override
                protected void service( final HttpServletRequest request, final HttpServletResponse response )
                    throws IOException
                {
                    request.setAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE, DispatchConstants.WEB_CONNECTOR );
                    final var serializer = new WebSerializerServiceImpl();
                    final var webRequest = new PortalRequest( serializer.request( request ) );
                    webRequest.setApplicationKey( ApplicationKey.from( "caller.app" ) );
                    webRequest.setBody( new String( request.getInputStream().readAllBytes(), UTF_8 ) );
                    assertThat( request.getInputStream().read() ).isEqualTo( -1 );
                    final var login = request.getHeader( "X-Mcp-Caller" );
                    final var requestAuth = login == null ? authInfo : caller( login );
                    final var context = ContextBuilder.create().authInfo( requestAuth ).build();
                    context.callWith( () -> {
                        final var webResponse = PortalRequestAccessor.callWith( webRequest, () -> dispatch.apply( webRequest ) );
                        final var applicationKey = webRequest.getApplicationKey();
                        assertThat( PortalRequestAccessor.get() ).isNull();
                        serializer.response( webRequest, webResponse, response );
                        assertThat( PortalRequestAccessor.get() ).isNull();
                        assertThat( webRequest.getApplicationKey() ).isEqualTo( applicationKey );
                        assertThat( ContextAccessor.current().getAuthInfo() ).isSameAs( requestAuth );
                        return null;
                    } );
                }
            } );
            holder.setAsyncSupported( true );
            servletContext.addServlet( holder, "/*" );
            server.setHandler( servletContext );
            try
            {
                server.start();
                uri = URI.create( "http://localhost:" + connector.getLocalPort() + "/api/system:mcp" );
            }
            catch ( Exception e )
            {
                close();
                throw e;
            }
        }

        private McpSyncClient sdkClient()
        {
            final var mapper = new JacksonMcpJsonMapper( MAPPER );
            final var builder = mock( HttpClient.Builder.class, RETURNS_SELF );
            when( builder.build() ).thenReturn( http );
            final var transport = HttpClientMcpTransport.builder( uri ).clientBuilder( builder ).jsonMapper( mapper ).build();
            return McpClient.sync( transport ).jsonMapper( mapper ).roundTimeout( TIMEOUT ).callTimeout( TIMEOUT ).build();
        }

        private SseStream stream( final Object token, final String message, final @Nullable String login )
            throws Exception
        {
            final var body = request( "tools/call", Map.of( "name", CatalogFixture.PUBLIC_NAME,
                "arguments", Map.of( "message", message ), "_meta", Map.of( "progressToken", token ) ) );
            final var builder = HttpRequest.newBuilder( uri ).timeout( TIMEOUT ).header( "Content-Type", "application/json" )
                .header( "Accept", "application/json, text/event-stream" ).header( "Mcp-Name", CatalogFixture.PUBLIC_NAME )
                .POST( HttpRequest.BodyPublishers.ofString( body, UTF_8 ) );
            headers( "tools/call" ).forEach( ( name, values ) -> values.forEach( value -> builder.header( name, value ) ) );
            if ( login != null )
            {
                builder.header( "X-Mcp-Caller", login );
            }
            return new SseStream( http.sendAsync( builder.build(), HttpResponse.BodyHandlers.ofInputStream() )
                .get( TIMEOUT.toMillis(), MILLISECONDS ) );
        }

        private HttpResponse<String> rpc( final String method, final Map<String, ?> params )
            throws IOException, InterruptedException
        {
            final var headers = headers( method );
            if ( params.get( "name" ) instanceof String name )
            {
                headers.put( "Mcp-Name", List.of( name ) );
            }
            return post( request( method, params ), "application/json", headers );
        }

        private HttpResponse<String> post( final String body, final String contentType, final Map<String, List<String>> headers )
            throws IOException, InterruptedException
        {
            final var builder = HttpRequest.newBuilder( uri ).header( "Content-Type", contentType )
                .POST( HttpRequest.BodyPublishers.ofString( body, UTF_8 ) );
            headers.forEach( ( name, values ) -> values.forEach( value -> builder.header( name, value ) ) );
            return send( builder );
        }

        private HttpResponse<String> send( final HttpRequest.Builder builder )
            throws IOException, InterruptedException
        {
            return http.send( builder.timeout( TIMEOUT ).build(), HttpResponse.BodyHandlers.ofString( UTF_8 ) );
        }

        @Override
        public void close()
            throws Exception
        {
            try
            {
                handler.deactivate();
            }
            finally
            {
                try
                {
                    http.close();
                }
                finally
                {
                    server.stop();
                }
            }
        }

        private static Function<WebRequest, WebResponse> routing( final McpUniversalApiHandler handler )
        {
            final var registry = new DynamicUniversalApiHandlerRegistry();
            registry.addApiHandler( handler, Map.of( "key", "system:mcp", "title", "MCP API",
                "allowedPrincipals", "role:system.everyone", "mount", "web" ) );
            final var descriptor = registry.getApiHandler( DescriptorKey.from( "system:mcp" ) ).getApiDescriptor();
            assertThat( descriptor.getKey().toString() ).isEqualTo( "system:mcp" );
            assertThat( descriptor.getMount() ).containsExactly( "web" );
            final var scripts = mock( ControllerScriptFactory.class );
            final var descriptors = mock( ApiDescriptorService.class );
            final var slash = new SlashApiHandler( scripts, descriptors, mock( SiteService.class ), mock( WebappService.class ),
                mock( AdminToolDescriptorService.class ), registry, mock( SseManager.class ), mock( PortalScriptService.class ) );
            return request -> {
                final var response = slash.handle( request );
                verifyNoInteractions( scripts, descriptors );
                return response;
            };
        }
    }
}
