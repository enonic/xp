package com.enonic.xp.mcp.impl;

import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.enonic.xp.core.impl.schema.YmlParserBase;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.mcp.McpDescriptor;
import com.enonic.xp.security.PrincipalKeys;

import io.modelcontextprotocol.modern.McpSchema.Icon;
import io.modelcontextprotocol.modern.McpSchema.Tool;
import io.modelcontextprotocol.modern.McpSchema.ToolAnnotations;

@NullMarked
public final class YmlMcpDescriptorParser
{
    private static final YmlParserBase PARSER = new YmlParserBase();

    static
    {
        PARSER.enableStrictParsing();
        PARSER.addMixIn( Tool.class, ToolMixIn.class );
    }

    private YmlMcpDescriptorParser()
    {
    }

    public static LoadedMcpGroup parse( final String source, final DescriptorKey key )
    {
        final Mapping mapping;
        try
        {
            mapping = PARSER.parse( source, Mapping.class, key.getApplicationKey() );
        }
        catch ( UncheckedIOException e )
        {
            throw new IllegalArgumentException( "Invalid MCP YAML: " + e.getMessage(), e );
        }
        if ( mapping == null )
        {
            throw new IllegalArgumentException( "MCP descriptor must be an object" );
        }
        if ( mapping.tools == null )
        {
            throw new IllegalArgumentException( "MCP descriptor tools must be a list of objects" );
        }
        return new LoadedMcpGroup( new McpDescriptor( key, mapping.allow ), mapping.tools );
    }

    private static final class Mapping
    {
        private PrincipalKeys allow = PrincipalKeys.empty();

        @JsonProperty(required = true)
        @JsonSetter(nulls = Nulls.FAIL, contentNulls = Nulls.FAIL)
        public @Nullable List<Tool> tools;

        @JsonSetter(nulls = Nulls.FAIL)
        public void setKind( final String kind )
        {
            if ( !"MCP".equals( kind ) )
            {
                throw new IllegalArgumentException( "MCP descriptor kind must be MCP" );
            }
        }

        @JsonSetter(nulls = Nulls.FAIL)
        public void setAllow( final PrincipalKeys allow )
        {
            this.allow = allow;
        }
    }

    private abstract static class ToolMixIn
    {
        public static Tool fromJson( @JsonProperty(value = "name", required = true) @JsonSetter(nulls = Nulls.FAIL) final String name,
                                     @JsonProperty("title") final @Nullable String title,
                                     @JsonProperty("description") final @Nullable String description,
                                     @JsonProperty(value = "inputSchema", required = true) @JsonSetter(nulls = Nulls.FAIL)
                                     final Map<String, @Nullable Object> inputSchema,
                                     @JsonProperty("outputSchema") final @Nullable Map<String, @Nullable Object> outputSchema,
                                     @JsonProperty("annotations") final @Nullable ToolAnnotations annotations,
                                     @JsonProperty("_meta") final @Nullable Map<String, @Nullable Object> meta,
                                     @JsonProperty("icons") final @Nullable List<Icon> icons )
        {
            throw new UnsupportedOperationException();
        }
    }
}
