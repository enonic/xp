package com.enonic.xp.mcp.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.security.PrincipalKey;

import io.modelcontextprotocol.modern.McpSchema.Tool;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@NullMarked
class YmlMcpDescriptorParserTest
{
    private static final DescriptorKey KEY = DescriptorKey.from( "myapp:search" );

    @Test
    void preserves_tools_schemas_metadata_and_nested_nulls()
        throws IOException
    {
        final String source;
        try ( var stream = requireNonNull( getClass().getResourceAsStream( "tools.yaml" ) ) )
        {
            source = new String( stream.readAllBytes(), StandardCharsets.UTF_8 );
        }
        final var descriptor = YmlMcpDescriptorParser.parse( source, KEY );

        assertThat( descriptor.descriptor().getKey() ).isEqualTo( KEY );
        assertThat( descriptor.descriptor().getAllowedPrincipals() ).containsExactly( PrincipalKey.from( "role:cms.admin" ),
                                                                        PrincipalKey.from( "role:system.everyone" ) );
        assertThat( descriptor.tools() ).extracting( Tool::name ).containsExactly( "search", "details" );
        final var tool = descriptor.tools().getFirst();
        assertThat( tool.title() ).isEqualTo( "Search content" );
        assertThat( tool.description() ).isEqualTo( "Find matching content" );
        assertThat( tool.inputSchema() ).isEqualTo(
            Map.of( "type", "object", "properties", Map.of( "query", nullableSchema() ), "required", Arrays.asList( "query" ),
                    "additionalProperties", false ) );
        assertThat( tool.outputSchema() ).isEqualTo(
            Map.of( "type", "object", "properties", Map.of( "result", nullValue( "const" ) ) ) );
        assertThat( tool.annotations().readOnlyHint() ).isTrue();
        assertThat( tool.icons() ).hasSize( 1 );
        assertThat( tool.icons().getFirst().src() ).isEqualTo( "https://example.com/search.svg" );
        assertThat( tool.icons().getFirst().mimeType() ).isEqualTo( "image/svg+xml" );
        final var metadata = new LinkedHashMap<String, @Nullable Object>();
        metadata.put( "nullable", null );
        metadata.put( "values", Arrays.asList( "text", null, true, 42 ) );
        assertThat( tool.meta() ).isEqualTo( metadata );
        final var details = descriptor.tools().getLast();
        assertThat( details.inputSchema() ).isEmpty();
        assertThat( details.meta() ).isNull();
        assertThat( details.icons() ).isNull();
    }

    @Test
    void accepts_optional_kind_and_defaults_allow_to_empty()
    {
        final var descriptor = YmlMcpDescriptorParser.parse( "tools: []", KEY );
        assertThat( descriptor.descriptor().getAllowedPrincipals() ).isEmpty();
        assertThat( descriptor.tools() ).isEmpty();
        assertDoesNotThrow( () -> YmlMcpDescriptorParser.parse( "kind: MCP\nallow: []\ntools: []", KEY ) );
    }

    @Test
    void rejects_invalid_roots_and_unknown_fields()
    {
        assertThatThrownBy( () -> YmlMcpDescriptorParser.parse( "", KEY ) )
            .isInstanceOf( IllegalArgumentException.class ).hasMessageContaining( "Invalid MCP YAML" );
        for ( var source : Arrays.asList( "null", "[]", "MCP" ) )
        {
            assertThatThrownBy( () -> YmlMcpDescriptorParser.parse( source, KEY ) )
                .isInstanceOf( IllegalArgumentException.class ).hasMessageContaining( "MCP" );
        }
        assertThatThrownBy( () -> YmlMcpDescriptorParser.parse( "tools: []\ntoolz: []", KEY ) )
            .hasMessageContaining( "toolz" );
        for ( var kind : Arrays.asList( "API", "null", "42", "[]" ) )
        {
            assertThatThrownBy( () -> YmlMcpDescriptorParser.parse( "kind: " + kind + "\ntools: []", KEY ) )
                .hasMessageContaining( "kind" );
        }
    }

    @Test
    void preserves_all_principal_key_types()
    {
        final var descriptor = YmlMcpDescriptorParser.parse( """
            allow: ['role:cms.admin', 'user:system:su', 'group:system:editors']
            tools: []
            """, KEY );
        assertThat( descriptor.descriptor().getAllowedPrincipals() ).containsExactly( PrincipalKey.from( "role:cms.admin" ),
                                                                        PrincipalKey.from( "user:system:su" ),
                                                                        PrincipalKey.from( "group:system:editors" ) );
    }

    @Test
    void rejects_invalid_allow()
    {
        for ( var allow : Arrays.asList( "null", "role:cms.admin", "{}", "[null]", "[42]", "[invalid]" ) )
        {
            assertThatThrownBy( () -> YmlMcpDescriptorParser.parse( "allow: " + allow + "\ntools: []", KEY ) )
                .isInstanceOf( IllegalArgumentException.class ).hasMessageContaining( "allow" );
        }
    }

    @Test
    void requires_tool_objects_and_validates_tool_shapes()
    {
        for ( var source : Arrays.asList( "allow: []", "tools: null", "tools: {}", "tools: text", "tools: [null]",
                                         "tools: [text]" ) )
        {
            assertThatThrownBy( () -> YmlMcpDescriptorParser.parse( source, KEY ) )
                .isInstanceOf( IllegalArgumentException.class ).hasMessageContaining( "tools" );
        }
        for ( var tool : Arrays.asList( "{}", "{name: search}", "{inputSchema: {}}",
                                       "{name: null, inputSchema: {}}", "{name: search, inputSchema: null}",
                                       "{name: 42, inputSchema: {}}", "{name: '', inputSchema: {}}", "{name: 'bad name', inputSchema: {}}",
                                       "{name: search, inputSchema: []}", "{name: search, inputSchema: {}, outputSchema: []}",
                                       "{name: search, inputSchema: {}, title: 42}",
                                       "{name: search, inputSchema: {}, annotations: []}" ) )
        {
            assertThatThrownBy( () -> YmlMcpDescriptorParser.parse( "tools: [" + tool + "]", KEY ) )
                .isInstanceOf( IllegalArgumentException.class ).hasMessageContaining( "tools" );
        }
    }

    @Test
    void validates_tool_schemas_and_nested_annotations_through_sdk()
    {
        for ( var tool : Arrays.asList( "{name: search, inputSchema: {type: unknown}}",
                                       "{name: search, inputSchema: {}, outputSchema: {required: value}}",
                                       "{name: search, inputSchema: {}, annotations: {readOnlyHint: 'true'}}" ) )
        {
            assertThatThrownBy( () -> YmlMcpDescriptorParser.parse( "tools: [" + tool + "]", KEY ) )
                .isInstanceOf( IllegalArgumentException.class ).hasMessageContaining( "tools" );
        }
    }

    @Test
    void rejects_duplicate_keys_names_and_trailing_documents()
    {
        for ( var source : Arrays.asList( "tools: []\ntools: []", "tools: [{name: search, name: other, inputSchema: {}}]",
                                         "tools: [{name: search, inputSchema: {type: object, type: string}}]",
                                         "tools: []\n---\ntools: []", "tools: []\n---\nnull", "tools: [" ) )
        {
            assertThatThrownBy( () -> YmlMcpDescriptorParser.parse( source, KEY ) ).isInstanceOf( IllegalArgumentException.class );
        }
        assertThatThrownBy( () -> YmlMcpDescriptorParser.parse( """
            tools:
              - {name: search, inputSchema: {}}
              - {name: search, inputSchema: {}}
            """, KEY ) ).hasMessageContaining( "Duplicate MCP tool name: search" );
    }

    @Test
    void preserves_schema_without_executing_it()
    {
        final var descriptor = YmlMcpDescriptorParser.parse( """
            tools:
              - name: search
                inputSchema:
                  $ref: https://example.invalid/schema
                  customKeyword: [null, 7]
            """, KEY );
        assertThat( descriptor.tools().getFirst().inputSchema() ).isEqualTo(
            Map.of( "$ref", "https://example.invalid/schema", "customKeyword", Arrays.asList( null, 7 ) ) );
    }

    @Test
    void loaded_group_copies_list_and_preserves_sdk_tool_identity()
    {
        final var parsed = YmlMcpDescriptorParser.parse( "tools: [{name: search, inputSchema: {}}]", KEY );
        final var tools = new ArrayList<>( parsed.tools() );
        final var loaded = new LoadedMcpGroup( parsed.descriptor(), tools );
        tools.clear();

        assertThat( loaded.descriptor() ).isSameAs( parsed.descriptor() );
        assertThat( loaded.tools() ).hasSize( 1 );
        assertThat( loaded.tools().getFirst() ).isSameAs( parsed.tools().getFirst() );
        assertThatThrownBy( () -> loaded.tools().clear() ).isInstanceOf( UnsupportedOperationException.class );
        assertThatThrownBy( () -> new LoadedMcpGroup( parsed.descriptor(),
                                                    Arrays.asList( parsed.tools().getFirst(), parsed.tools().getFirst() ) ) )
            .hasMessageContaining( "Duplicate MCP tool name: search" );
    }

    private static Map<String, @Nullable Object> nullableSchema()
    {
        final var schema = new LinkedHashMap<String, @Nullable Object>();
        schema.put( "type", Arrays.asList( "string", "null" ) );
        schema.put( "default", null );
        return schema;
    }

    private static Map<String, @Nullable Object> nullValue( final String key )
    {
        final var value = new LinkedHashMap<String, @Nullable Object>();
        value.put( key, null );
        return value;
    }
}
