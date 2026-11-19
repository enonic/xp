package com.enonic.xp.mcp.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.resource.ResourceError;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProblemException;

import io.modelcontextprotocol.json.jackson2.JacksonMcpJsonMapper;
import io.modelcontextprotocol.modern.McpException;
import io.modelcontextprotocol.modern.McpSchema.CallToolOutcome;
import io.modelcontextprotocol.modern.McpSchema.CallToolRequest;
import io.modelcontextprotocol.modern.McpSchema.CallToolResult;
import io.modelcontextprotocol.modern.McpSchema.EmbeddedResource;
import io.modelcontextprotocol.modern.McpSchema.Icon;
import io.modelcontextprotocol.modern.McpSchema.ImageContent;
import io.modelcontextprotocol.modern.McpSchema.ResultType;
import io.modelcontextprotocol.modern.McpSchema.TextContent;
import io.modelcontextprotocol.modern.McpSchema.TextResourceContents;
import io.modelcontextprotocol.modern.McpSchema.Tool;
import io.modelcontextprotocol.modern.McpSchema.ToolAnnotations;
import io.modelcontextprotocol.modern.server.McpRequestContext;
import io.modelcontextprotocol.modern.server.McpSyncResponse;
import io.modelcontextprotocol.modern.server.feature.McpSyncTool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@NullMarked
class McpToolAdapterTest
{
    private static final Pattern ERROR_ID = Pattern.compile( "MCP tool execution failed \\(error id ([0-9a-f]{8})\\)" );

    private static final Map<String, Object> ECHO_SCHEMA = Map.of( "type", "object", "properties",
                                                                Map.of( "message", Map.of( "type", "string" ) ),
                                                                "required", List.of( "message" ), "additionalProperties", false );

    @Test
    void validates_same_tool_with_schemas_annotations_icons_and_metadata()
    {
        final var schema = new LinkedHashMap<String, @Nullable Object>();
        schema.put( "type", "object" );
        schema.put( "properties", Map.of( "value", Map.of( "type", List.of( "string", "null" ) ) ) );
        schema.put( "default", null );
        final var metadata = new LinkedHashMap<String, @Nullable Object>();
        metadata.put( "nullable", null );
        metadata.put( "nested", Arrays.asList( "text", null, true, 42 ) );
        final var source = Tool.builder( "echo", schema )
            .title( "Echo title" )
            .description( "Echo a message" )
            .outputSchema( schema )
            .annotations( ToolAnnotations.builder().readOnlyHint( true ).destructiveHint( false ).build() )
            .icons( List.of( Icon.builder( "https://example.com/echo.svg" ).mimeType( "image/svg+xml" )
                                .sizes( List.of( "48x48" ) ).theme( "light" ).build() ) )
            .meta( metadata )
            .build();

        final var tool = McpToolAdapter.validateTool( source );

        assertThat( tool ).isSameAs( source );

        assertThat( tool.name() ).isEqualTo( "echo" );
        assertThat( tool.title() ).isEqualTo( "Echo title" );
        assertThat( tool.description() ).isEqualTo( "Echo a message" );
        assertThat( tool.inputSchema() ).isEqualTo( schema );
        assertThat( tool.outputSchema() ).isEqualTo( schema );
        assertThat( tool.meta() ).isEqualTo( metadata );
        assertThat( tool.annotations().readOnlyHint() ).isTrue();
        assertThat( tool.annotations().destructiveHint() ).isFalse();
        assertThat( tool.icons() ).singleElement().satisfies( icon -> {
            assertThat( icon.src() ).isEqualTo( "https://example.com/echo.svg" );
            assertThat( icon.mimeType() ).isEqualTo( "image/svg+xml" );
            assertThat( icon.sizes() ).containsExactly( "48x48" );
            assertThat( icon.theme() ).isEqualTo( "light" );
        } );
        assertThat( McpToolAdapter.bind( tool, handler() ).definition() ).isSameAs( source );
    }

    @Test
    void preserves_case_sensitive_valid_names()
    {
        for ( var name : List.of( "echo", "Echo", "ECHO", "aZ09_.-", "a".repeat( 128 ) ) )
        {
            final var tool = Tool.builder( name, Map.of() ).build();
            assertThat( McpToolAdapter.validateTool( tool ) ).isSameAs( tool );
            final var binding = McpToolAdapter.bind( tool, ( toolName, arguments ) -> {
                assertThat( toolName ).isEqualTo( name );
                return Map.of( "content", List.of() );
            } );
            assertThat( binding.definition() ).isSameAs( tool );
            assertThat( call( binding, CallToolRequest.builder( name ).build() ).isError() ).isNotEqualTo( true );
        }
    }

    @Test
    void rejects_invalid_names_during_validation_and_binding()
    {
        for ( var name : List.of( "", "a".repeat( 129 ), "echo tool", "echo/tool", "echo:tool", "écho", "echo\n" ) )
        {
            final var tool = new Tool( name, null, null, Map.of(), null, null, null, null );
            assertThatThrownBy( () -> McpToolAdapter.validateTool( tool ) )
                .isInstanceOf( IllegalArgumentException.class ).hasMessageContaining( "name" );
            final var handler = handler();
            assertThatThrownBy( () -> McpToolAdapter.bind( tool, handler ) )
                .isInstanceOf( IllegalArgumentException.class ).hasMessageContaining( "name" );
            verifyNoInteractions( handler );
        }
    }

    @Test
    void parser_rejects_invalid_names_and_missing_schemas()
    {
        for ( var name : List.of( "", "a".repeat( 129 ), "echo tool", "echo/tool", "echo:tool", "écho", "echo\n" ) )
        {
            final var source = definition( Map.of() );
            source.put( "name", name );
            assertThatThrownBy( () -> parseTool( source ) )
                .isInstanceOf( IllegalArgumentException.class ).hasMessageContaining( "name" );
        }
        final var missingSchema = new LinkedHashMap<String, @Nullable Object>();
        missingSchema.put( "name", "echo" );
        assertThatThrownBy( () -> parseTool( missingSchema ) )
            .isInstanceOf( IllegalArgumentException.class ).hasMessageContaining( "inputSchema" );
        missingSchema.put( "inputSchema", null );
        assertThatThrownBy( () -> parseTool( missingSchema ) )
            .isInstanceOf( IllegalArgumentException.class ).hasMessageContaining( "inputSchema" );
    }

    @Test
    void validates_input_and_output_schema_keywords()
    {
        for ( var schema : List.<Map<String, Object>>of( Map.of( "type", "unknown" ), Map.of( "required", "message" ),
                                                       Map.of( "properties", List.of( "message" ) ), Map.of( "minLength", -1 ) ) )
        {
            assertThatThrownBy( () -> McpToolAdapter.validateTool( Tool.builder( "echo", schema ).build() ) )
                .isInstanceOf( IllegalArgumentException.class ).hasMessageContaining( "inputSchema" );
            final var tool = Tool.builder( "echo", Map.of() ).outputSchema( schema ).build();
            assertThatThrownBy( () -> McpToolAdapter.validateTool( tool ) )
                .isInstanceOf( IllegalArgumentException.class ).hasMessageContaining( "outputSchema" );
        }
    }

    @Test
    void parser_rejects_invalid_optional_shapes_and_nested_values()
    {
        for ( var entry : Map.<String, Object>of( "title", List.of(), "description", Map.of(), "outputSchema", List.of(),
                                                "annotations", List.of(), "icons", Map.of(), "_meta", List.of() ).entrySet() )
        {
            final var source = definition( Map.of() );
            source.put( entry.getKey(), entry.getValue() );
            assertThatThrownBy( () -> parseTool( source ) )
                .isInstanceOf( IllegalArgumentException.class );
        }
        for ( var annotations : List.of( Map.of( "readOnlyHint", "true" ), Map.of( "destructiveHint", 1 ),
                                         Map.of( "title", false ) ) )
        {
            final var source = definition( Map.of() );
            source.put( "annotations", annotations );
            assertThatThrownBy( () -> parseTool( source ) )
                .isInstanceOf( IllegalArgumentException.class );
        }
        for ( var icon : List.of( Map.of( "src", 42 ), Map.of( "src", "icon", "sizes", "48x48" ),
                                 Map.of( "src", "icon", "mimeType", true ), Map.of( "src", "icon", "sizes", List.of( 42 ) ) ) )
        {
            final var source = definition( Map.of() );
            source.put( "icons", List.of( icon ) );
            assertThatThrownBy( () -> parseTool( source ) )
                .isInstanceOf( IllegalArgumentException.class );
        }
    }

    @Test
    void parser_rejects_scalar_coercion_in_tool_fields()
    {
        for ( var value : List.of( 42, 2.5, true ) )
        {
            final var source = definition( Map.of() );
            source.put( "title", value );
            assertThatThrownBy( () -> parseTool( source ) )
                .isInstanceOf( IllegalArgumentException.class );
        }
    }

    @Test
    void converts_sdk_polymorphic_content_and_preserves_null_metadata()
    {
        final var metadata = new LinkedHashMap<String, @Nullable Object>();
        metadata.put( "nullable", null );
        final Map<String, @Nullable Object> source = Map.of(
            "content", List.of( Map.of( "type", "text", "text", "hello", "_meta", metadata ),
                                Map.of( "type", "image", "data", "aGVsbG8=", "mimeType", "image/png" ),
                                Map.of( "type", "resource", "resource",
                                        Map.of( "uri", "file:///echo.txt", "mimeType", "text/plain", "text", "hello" ) ) ),
            "structuredContent", metadata, "_meta", metadata );

        final var result = McpToolAdapter.toResult( source );

        assertThat( result.resultType() ).isEqualTo( ResultType.COMPLETE );
        assertThat( result.structuredContent() ).isEqualTo( metadata );
        assertThat( result.meta() ).isEqualTo( metadata );
        assertThat( result.content() ).hasSize( 3 );
        assertThat( result.content().get( 0 ) ).isInstanceOfSatisfying( TextContent.class, text -> {
            assertThat( text.text() ).isEqualTo( "hello" );
            assertThat( text.meta() ).isEqualTo( metadata );
        } );
        assertThat( result.content().get( 1 ) ).isInstanceOfSatisfying( ImageContent.class, image -> {
            assertThat( image.data() ).isEqualTo( "aGVsbG8=" );
            assertThat( image.mimeType() ).isEqualTo( "image/png" );
        } );
        assertThat( result.content().get( 2 ) ).isInstanceOfSatisfying( EmbeddedResource.class, resource ->
            assertThat( resource.resource() ).isInstanceOfSatisfying( TextResourceContents.class, text -> {
                assertThat( text.uri() ).isEqualTo( "file:///echo.txt" );
                assertThat( text.text() ).isEqualTo( "hello" );
            } ) );
    }

    @Test
    void requires_content_list_and_reserves_result_type_for_xp()
    {
        final var nullContent = new LinkedHashMap<String, @Nullable Object>();
        nullContent.put( "content", null );
        for ( var result : List.<Map<String, @Nullable Object>>of( Map.of(), nullContent, Map.of( "content", "text" ),
                                                                 Map.of( "content", Map.of() ) ) )
        {
            assertThatThrownBy( () -> McpToolAdapter.toResult( result ) ).isInstanceOf( IllegalArgumentException.class )
                .hasMessageContaining( "content" );
        }
        for ( var type : Arrays.asList( ResultType.COMPLETE, ResultType.INPUT_REQUIRED, "partial", null ) )
        {
            final var result = new LinkedHashMap<String, @Nullable Object>();
            result.put( "content", List.of() );
            result.put( "resultType", type );
            assertThatThrownBy( () -> McpToolAdapter.toResult( result ) ).isInstanceOf( IllegalArgumentException.class )
                .hasMessageContaining( "resultType" );
        }
    }

    @Test
    void rejects_unknown_and_malformed_nested_content_using_sdk()
    {
        for ( var content : List.of( Map.of( "type", "unknown", "text", "hello" ),
                                    Map.of( "type", "text", "text", Map.of() ),
                                    Map.of( "type", "image", "data", List.of(), "mimeType", "image/png" ),
                                    Map.of( "type", "resource", "resource", "text" ),
                                    Map.of( "type", "text", "text", "hello", "annotations", Map.of( "audience", List.of( "invalid" ) ) ),
                                    Map.of( "type", "text", "text", "hello", "annotations", Map.of( "priority", "0.5" ) ) ) )
        {
            assertThatThrownBy( () -> McpToolAdapter.toResult( Map.of( "content", List.of( content ) ) ) )
                .isInstanceOf( IllegalArgumentException.class );
        }
        for ( var scalar : List.of( true, 42, 2.5 ) )
        {
            assertThatThrownBy( () -> McpToolAdapter.toResult( Map.of( "content", List.of( Map.of( "type", "text", "text", scalar ) ) ) ) )
                .isInstanceOf( IllegalArgumentException.class );
        }
        assertThatThrownBy( () -> McpToolAdapter.toResult( Map.of( "content", List.of(), "isError", "true" ) ) )
            .isInstanceOf( IllegalArgumentException.class );
    }

    @Test
    void invokes_echo_fixture_through_sdk_handler_and_complete_response()
    {
        final var binding = McpToolAdapter.bind( echoTool(), ( name, arguments ) -> {
            assertThat( name ).isEqualTo( "echo" );
            return Map.of( "content", List.of( Map.of( "type", "text", "text", arguments.get( "message" ) ) ),
                           "structuredContent", arguments );
        } );

        final var result = call( binding, CallToolRequest.builder( "echo" ).arguments( Map.of( "message", "hello" ) ).build() );

        assertThat( binding.definition().name() ).isEqualTo( "echo" );
        assertThat( result.isError() ).isNotEqualTo( true );
        assertThat( result.resultType() ).isEqualTo( ResultType.COMPLETE );
        assertThat( result.structuredContent() ).isEqualTo( Map.of( "message", "hello" ) );
        assertThat( result.content() ).singleElement().isInstanceOfSatisfying( TextContent.class,
                                                                                 text -> assertThat( text.text() ).isEqualTo( "hello" ) );
    }

    @Test
    void normalizes_absent_arguments_to_empty_map()
    {
        final var handler = handler();
        when( handler.apply( eq( "echo" ), any() ) ).thenReturn( Map.of( "content", List.of() ) );
        final var binding = McpToolAdapter.bind( Tool.builder( "echo", Map.of( "type", "object" ) ).build(), handler );

        assertDoesNotThrow( () -> call( binding, CallToolRequest.builder( "echo" ).build() ) );

        verify( handler ).apply( "echo", Map.of() );
    }

    @Test
    void preserves_null_arguments_and_output_without_output_schema()
    {
        final var arguments = new LinkedHashMap<String, @Nullable Object>();
        arguments.put( "value", null );
        final var schema = Map.of( "type", "object", "properties", Map.of( "value", Map.of( "type", "null" ) ) );
        final var binding = McpToolAdapter.bind( Tool.builder( "echo", schema ).build(), ( name, values ) -> {
            assertThat( values ).containsKey( "value" );
            assertThat( values.get( "value" ) ).isNull();
            return Map.of( "content", List.of(), "structuredContent", values );
        } );

        final var result = call( binding, CallToolRequest.builder( "echo" ).arguments( arguments ).build() );

        assertThat( result.isError() ).isNotEqualTo( true );
        assertThat( result.structuredContent() ).isEqualTo( arguments );
    }

    @Test
    void rejects_invalid_arguments_without_invoking_handler()
    {
        final var handler = handler();
        final var binding = McpToolAdapter.bind( echoTool(), handler );
        for ( var request : List.of( CallToolRequest.builder( "echo" ).build(),
                                    CallToolRequest.builder( "echo" ).arguments( Map.of( "message", 42 ) ).build(),
                                    CallToolRequest.builder( "echo" ).arguments( Map.of( "message", "hello", "extra", true ) ).build() ) )
        {
            assertError( call( binding, request ), "arguments" );
        }
        verifyNoInteractions( handler );
    }

    @Test
    void validates_successful_controller_output()
    {
        for ( var result : List.<Map<String, @Nullable Object>>of(
            Map.of( "content", List.of(), "structuredContent", Map.of( "message", 42 ) ),
            Map.of( "content", List.of(), "structuredContent", Map.of() ),
            Map.of( "content", List.of(), "structuredContent", "{\"message\":\"hello\"}" ) ) )
        {
            final var binding = McpToolAdapter.bind( echoTool(), ( name, arguments ) -> result );
            assertError( call( binding, echoRequest() ), "MCP tool" );
        }
    }

    @Test
    void converts_malformed_controller_results_to_standard_tool_errors()
    {
        for ( var result : List.<Map<String, @Nullable Object>>of( Map.of(),
            Map.of( "content", List.of( Map.of( "type", "unknown" ) ) ),
            Map.of( "content", List.of(), "resultType", ResultType.INPUT_REQUIRED ) ) )
        {
            final var binding = McpToolAdapter.bind( echoTool(), ( name, arguments ) -> result );
            assertError( call( binding, echoRequest() ), "" );
        }
    }

    @Test
    void preserves_content_only_errors_and_errors_with_valid_partial_output()
    {
        for ( var result : List.<Map<String, @Nullable Object>>of(
            Map.of( "content", List.of( Map.of( "type", "text", "text", "failed" ) ), "isError", true ),
            Map.of( "content", List.of( Map.of( "type", "text", "text", "failed" ) ), "isError", true,
                    "structuredContent", Map.of( "message", "partial result" ) ) ) )
        {
            final var binding = McpToolAdapter.bind( echoTool(), ( name, arguments ) -> result );
            final var converted = call( binding, echoRequest() );
            assertError( converted, "failed" );
            assertThat( converted.structuredContent() ).isEqualTo( result.get( "structuredContent" ) );
        }
    }

    @Test
    void rejects_errors_with_invalid_partial_structured_output()
    {
        for ( var structuredContent : List.of( Map.of( "partial", true ), Map.of( "message", 42 ) ) )
        {
            final var binding = McpToolAdapter.bind( echoTool(), ( name, arguments ) ->
                Map.of( "content", List.of( Map.of( "type", "text", "text", "controller failure" ) ), "isError", true,
                        "structuredContent", structuredContent ) );

            final var result = call( binding, echoRequest() );

            assertError( result, "Invalid MCP tool output" );
            assertThat( result.structuredContent() ).isNull();
        }
    }

    @Test
    void accepts_explicit_null_structured_output_when_schema_permits()
    {
        final var tool = Tool.builder( "echo", ECHO_SCHEMA ).outputSchema( Map.of( "type", "null" ) ).build();
        final var output = new LinkedHashMap<String, @Nullable Object>();
        output.put( "content", List.of() );
        output.put( "structuredContent", null );
        final var binding = McpToolAdapter.bind( tool, ( name, arguments ) -> output );

        final var result = call( binding, echoRequest() );

        assertThat( result.isError() ).isNotEqualTo( true );
        assertThat( result.resultType() ).isEqualTo( ResultType.COMPLETE );
        assertThat( result.structuredContent() ).isSameAs( NullNode.getInstance() );
        assertThat( result.content() ).isEmpty();
    }

    @Test
    void rejects_missing_successful_structured_output_even_when_schema_permits_null()
    {
        final var nullableOutput = Tool.builder( "echo", ECHO_SCHEMA ).outputSchema( Map.of( "type", "null" ) ).build();
        for ( var tool : List.of( echoTool(), nullableOutput ) )
        {
            final var binding = McpToolAdapter.bind( tool, ( name, arguments ) -> Map.of( "content", List.of() ) );

            assertError( call( binding, echoRequest() ), "requires structuredContent for outputSchema" );
        }
    }

    @Test
    void serializes_explicit_null_structured_output_and_nested_null_metadata()
        throws IOException
    {
        final var nested = new LinkedHashMap<String, @Nullable Object>();
        nested.put( "nullable", null );
        final var metadata = Map.of( "nested", nested );
        final var output = new LinkedHashMap<String, @Nullable Object>();
        output.put( "content", List.of( Map.of( "type", "text", "text", "hello", "_meta", metadata ) ) );
        output.put( "structuredContent", null );
        output.put( "_meta", metadata );
        final var result = McpToolAdapter.toResult( output );
        final var objectMapper = new ObjectMapper();
        final var mapper = new JacksonMcpJsonMapper( objectMapper );

        final var json = objectMapper.readTree( mapper.writeValueAsString( result ) );

        assertThat( result.structuredContent() ).isSameAs( NullNode.getInstance() );
        assertThat( result.meta() ).isEqualTo( metadata );
        assertThat( json.has( "structuredContent" ) ).isTrue();
        assertThat( json.path( "structuredContent" ).isNull() ).isTrue();
        assertThat( json.path( "resultType" ).asText() ).isEqualTo( ResultType.COMPLETE );
        final var resultMetadata = json.path( "_meta" ).path( "nested" );
        assertThat( resultMetadata.has( "nullable" ) ).isTrue();
        assertThat( resultMetadata.path( "nullable" ).isNull() ).isTrue();
        final var contentMetadata = json.path( "content" ).path( 0 ).path( "_meta" ).path( "nested" );
        assertThat( contentMetadata.has( "nullable" ) ).isTrue();
        assertThat( contentMetadata.path( "nullable" ).isNull() ).isTrue();
    }

    @Test
    void round_trips_sdk_request_and_complete_result_through_json_mapper()
        throws IOException
    {
        final var objectMapper = new ObjectMapper();
        final var mapper = new JacksonMcpJsonMapper( objectMapper );
        final var request = echoRequest();
        final var requestJson = mapper.writeValueAsString( request );
        final var decodedRequest = mapper.readValue( requestJson, CallToolRequest.class );
        assertThat( decodedRequest ).isEqualTo( request );
        assertThat( objectMapper.readTree( requestJson ).path( "name" ).asText() ).isEqualTo( "echo" );
        assertThat( decodedRequest.arguments() ).containsEntry( "message", "hello" );
        final var binding = McpToolAdapter.bind( echoTool(), ( name, arguments ) ->
            Map.of( "content", List.of( Map.of( "type", "text", "text", arguments.get( "message" ) ) ),
                    "structuredContent", arguments ) );
        final var result = call( binding, decodedRequest );

        final var resultJson = mapper.writeValueAsString( result );
        final var wireResult = objectMapper.readTree( resultJson );
        final var decodedResult = mapper.readValue( resultJson, CallToolResult.class );

        assertThat( wireResult.path( "resultType" ).asText() ).isEqualTo( ResultType.COMPLETE );
        assertThat( wireResult.path( "content" ).path( 0 ).path( "type" ).asText() ).isEqualTo( "text" );
        assertThat( decodedResult ).isEqualTo( result );
        assertThat( decodedResult.resultType() ).isEqualTo( ResultType.COMPLETE );
        assertThat( decodedResult.structuredContent() ).isEqualTo( Map.of( "message", "hello" ) );
        assertThat( decodedResult.content() ).singleElement().isInstanceOfSatisfying( TextContent.class,
            text -> assertThat( text.text() ).isEqualTo( "hello" ) );
    }

    @Test
    void reports_thrown_failures_as_generic_tool_errors_with_logged_error_id()
    {
        for ( var failure : List.<Throwable>of( new IllegalStateException( "private detail" ),
                                                new IllegalArgumentException( "private detail" ),
                                                ResourceProblemException.create().message( "Error: private detail" ).build(),
                                                new ResourceError( ResourceKey.from( "myapp:/mcp/echo/echo.js" ), "private detail" ) ) )
        {
            final var log = mock( Logger.class );
            final var binding = McpToolAdapter.bind( echoTool(), ( name, arguments ) -> {
                if ( failure instanceof Error error )
                {
                    throw error;
                }
                throw (RuntimeException) failure;
            }, log );

            final var result = call( binding, echoRequest() );

            assertError( result, "MCP tool execution failed" );
            final var text = ( (TextContent) result.content().getFirst() ).text();
            assertThat( text ).doesNotContain( "private detail" );
            final var matcher = ERROR_ID.matcher( text );
            assertThat( matcher.matches() ).as( text ).isTrue();
            verify( log ).warn( "MCP tool {} execution failed (error id {})", "echo", matcher.group( 1 ), failure );
        }
    }

    @Test
    void propagates_protocol_exceptions()
    {
        final var failure = McpException.invalidParams( "MCP tool is no longer available: echo" );
        final var binding = McpToolAdapter.bind( echoTool(), ( name, arguments ) -> {
            throw failure;
        } );

        assertThatThrownBy( () -> call( binding, echoRequest() ) ).isSameAs( failure );
    }

    private static Map<String, @Nullable Object> definition( final Map<String, ?> inputSchema )
    {
        final var source = new LinkedHashMap<String, @Nullable Object>();
        source.put( "name", "echo" );
        source.put( "inputSchema", inputSchema );
        return source;
    }

    private static Tool parseTool( final Map<String, ?> source )
        throws IOException
    {
        final var yaml = new ObjectMapper().writeValueAsString( Map.of( "tools", List.of( source ) ) );
        return YmlMcpDescriptorParser.parse( yaml, DescriptorKey.from( "myapp:echo" ) ).tools().getFirst();
    }

    private static Tool echoTool()
    {
        return Tool.builder( "echo", ECHO_SCHEMA ).outputSchema( ECHO_SCHEMA ).build();
    }

    private static CallToolRequest echoRequest()
    {
        return CallToolRequest.builder( "echo" ).arguments( Map.of( "message", "hello" ) ).build();
    }

    private static CallToolResult call( final McpSyncTool binding, final CallToolRequest request )
    {
        final var response = binding.handler().call( mock( McpRequestContext.class ), request );
        assertThat( response ).isInstanceOf( McpSyncResponse.Result.class );
        final var outcome = ( (McpSyncResponse.Result<CallToolOutcome>) response ).result();
        assertThat( outcome ).isInstanceOf( CallToolResult.class );
        return (CallToolResult) outcome;
    }

    private static void assertError( final CallToolResult result, final String message )
    {
        assertThat( result.isError() ).isTrue();
        assertThat( result.resultType() ).isEqualTo( ResultType.COMPLETE );
        assertThat( result.content() ).singleElement().isInstanceOfSatisfying( TextContent.class,
            text -> assertThat( text.text() ).isNotBlank().contains( message ) );
    }

    @SuppressWarnings("unchecked")
    private static BiFunction<String, Map<String, @Nullable Object>, Map<String, @Nullable Object>> handler()
    {
        return mock( BiFunction.class );
    }
}
