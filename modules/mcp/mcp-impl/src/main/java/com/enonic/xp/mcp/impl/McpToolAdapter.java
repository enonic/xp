package com.enonic.xp.mcp.impl;

import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.type.LogicalType;

import com.enonic.xp.resource.ResourceError;

import io.modelcontextprotocol.json.jackson2.JacksonMcpJsonMapper;
import io.modelcontextprotocol.json.schema.jackson2.DefaultJsonSchemaValidator;
import io.modelcontextprotocol.modern.McpException;
import io.modelcontextprotocol.modern.McpSchema.CallToolResult;
import io.modelcontextprotocol.modern.McpSchema.TextContent;
import io.modelcontextprotocol.modern.McpSchema.Tool;
import io.modelcontextprotocol.modern.server.McpSyncResponse;
import io.modelcontextprotocol.modern.server.feature.McpSyncTool;

@NullMarked
public final class McpToolAdapter
{
    private static final Logger LOG = LoggerFactory.getLogger( McpToolAdapter.class );

    private static final Pattern TOOL_NAME = Pattern.compile( "[A-Za-z0-9_.-]{1,128}" );

    private static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

    private static final JacksonMcpJsonMapper JSON_MAPPER = new JacksonMcpJsonMapper( OBJECT_MAPPER );

    private static final DefaultJsonSchemaValidator VALIDATOR = new DefaultJsonSchemaValidator( OBJECT_MAPPER );

    private McpToolAdapter()
    {
    }

    public static Tool validateTool( final Tool tool )
    {
        if ( !TOOL_NAME.matcher( tool.name() ).matches() )
        {
            throw new IllegalArgumentException( "MCP tool name must match [A-Za-z0-9_.-]{1,128}" );
        }
        VALIDATOR.assertConforms( "MCP tool " + tool.name() + " inputSchema", tool.inputSchema() );
        VALIDATOR.assertConforms( "MCP tool " + tool.name() + " outputSchema", tool.outputSchema() );
        return tool;
    }

    public static CallToolResult toResult( final Map<String, @Nullable Object> result )
    {
        if ( !( result.get( "content" ) instanceof List<?> ) )
        {
            throw new IllegalArgumentException( "MCP tool result content must be a list" );
        }
        if ( result.containsKey( "resultType" ) )
        {
            throw new IllegalArgumentException( "MCP tool resultType is managed by XP" );
        }
        final var converted = JSON_MAPPER.convertValue( result, CallToolResult.class );
        if ( result.containsKey( "structuredContent" ) && result.get( "structuredContent" ) == null )
        {
            return new CallToolResult( converted.content(), NullNode.getInstance(), converted.isError(), converted.resultType(),
                                       converted.meta() );
        }
        return converted;
    }

    public static McpSyncTool bind( final Tool tool,
                                     final BiFunction<String, Map<String, @Nullable Object>, Map<String, @Nullable Object>> handler )
    {
        return bind( tool, handler, LOG );
    }

    static McpSyncTool bind( final Tool tool,
                             final BiFunction<String, Map<String, @Nullable Object>, Map<String, @Nullable Object>> handler,
                             final Logger log )
    {
        Objects.requireNonNull( handler, "handler" );
        validateTool( tool );
        return McpSyncTool.of( tool, ( context, request ) -> {
            final Map<String, @Nullable Object> arguments = request.arguments() == null ? Map.of() : request.arguments();
            final var validation = VALIDATOR.validate( tool.inputSchema(), arguments );
            if ( !validation.valid() )
            {
                return McpSyncResponse.result( error( "Invalid MCP tool arguments: " + validation.errorMessage() ) );
            }
            return McpInvocationScope.respond( context, () -> execute( tool, handler, arguments, log ) );
        } );
    }

    private static CallToolResult execute( final Tool tool,
                                           final BiFunction<String, Map<String, @Nullable Object>, Map<String, @Nullable Object>> handler,
                                           final Map<String, @Nullable Object> arguments, final Logger log )
    {
        final Map<String, @Nullable Object> result;
        try
        {
            result = handler.apply( tool.name(), arguments );
        }
        catch ( McpException e )
        {
            throw e;
        }
        catch ( RuntimeException | ResourceError e )
        {
            final var errorId = HexFormat.of().toHexDigits( ThreadLocalRandom.current().nextInt() );
            log.warn( "MCP tool {} execution failed (error id {})", tool.name(), errorId, e );
            return error( "MCP tool execution failed (error id " + errorId + ")" );
        }
        return validateResult( tool, result );
    }

    private static CallToolResult validateResult( final Tool tool, final Map<String, @Nullable Object> result )
    {
        try
        {
            final var converted = toResult( result );
            if ( tool.outputSchema() != null &&
                ( !Boolean.TRUE.equals( converted.isError() ) || result.containsKey( "structuredContent" ) ) )
            {
                if ( !result.containsKey( "structuredContent" ) )
                {
                    return error( "MCP tool result requires structuredContent for outputSchema" );
                }
                final var outputValidation = VALIDATOR.validate( tool.outputSchema(),
                                                                 OBJECT_MAPPER.valueToTree( converted.structuredContent() ) );
                if ( !outputValidation.valid() )
                {
                    return error( "Invalid MCP tool output: " + outputValidation.errorMessage() );
                }
            }
            return converted;
        }
        catch ( IllegalArgumentException e )
        {
            return error( Objects.toString( e.getMessage(), "Invalid MCP tool result" ) );
        }
    }

    private static CallToolResult error( final String message )
    {
        return CallToolResult.builder().addContent( TextContent.builder( message ).build() ).isError( true ).build();
    }

    private static ObjectMapper createObjectMapper()
    {
        final var mapper = JsonMapper.builder()
            .disable( MapperFeature.ALLOW_COERCION_OF_SCALARS )
            .disable( DeserializationFeature.ACCEPT_FLOAT_AS_INT )
            .build();
        mapper.coercionConfigFor( LogicalType.Textual )
            .setCoercion( CoercionInputShape.Integer, CoercionAction.Fail )
            .setCoercion( CoercionInputShape.Float, CoercionAction.Fail )
            .setCoercion( CoercionInputShape.Boolean, CoercionAction.Fail );
        return mapper;
    }
}
