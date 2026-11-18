package com.enonic.xp.mcp.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.Applications;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.mcp.McpDescriptor;
import com.enonic.xp.security.PrincipalKeys;

import io.modelcontextprotocol.modern.McpException;
import io.modelcontextprotocol.modern.McpSchema.CallToolOutcome;
import io.modelcontextprotocol.modern.McpSchema.CallToolRequest;
import io.modelcontextprotocol.modern.McpSchema.CallToolResult;
import io.modelcontextprotocol.modern.McpSchema.Icon;
import io.modelcontextprotocol.modern.McpSchema.Tool;
import io.modelcontextprotocol.modern.McpSchema.ToolAnnotations;
import io.modelcontextprotocol.modern.server.McpRequestContext;
import io.modelcontextprotocol.modern.server.McpSyncResponse;
import io.modelcontextprotocol.modern.server.feature.McpSyncTool;
import io.modelcontextprotocol.modern.server.feature.ServerChange;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@NullMarked
class McpToolCatalogTest
{
    private final McpDescriptorServiceImpl descriptors = mock( McpDescriptorServiceImpl.class );

    private final ApplicationService applications = mock( ApplicationService.class );

    private final McpToolExecutor executor = mock( McpToolExecutor.class );

    private final McpRequestContext context = mock( McpRequestContext.class );

    private final McpToolCatalog catalog = new McpToolCatalog( descriptors, applications, executor );

    @Test
    void orders_applications_and_groups_and_preserves_declaration_order()
    {
        final var installed = Applications.from( app( "z", true ), app( "stopped", false ), app( "a", true ) );
        when( applications.getInstalledApplications() ).thenReturn( installed );
        groups( "z", group( "z:b", "last" ) );
        groups( "a", group( "a:z", "second" ), group( "a:a", "z", "a" ), group( "a:empty" ) );

        final var page = catalog.list( context, null );

        assertThat( page.tools() ).extracting( Tool::name ).containsExactly( "a__a__z", "a__a__a", "a__z__second", "z__b__last" );
        assertThat( page.nextCursor() ).isNull();
        verify( descriptors ).getLoadedByApplication( ApplicationKey.from( "a" ) );
        verify( descriptors ).getLoadedByApplication( ApplicationKey.from( "z" ) );
        verifyNoInteractions( executor );
    }

    @Test
    void preserves_every_sdk_field_and_local_definition()
    {
        final var schema = new LinkedHashMap<String, @Nullable Object>();
        schema.put
( "type", "object" );
        schema.put( "default", null );
        final var metadata = new LinkedHashMap<String, @Nullable Object>();
        metadata.put( "nullable", null );
        metadata.put( "nested", Map.of( "value", 42 ) );
        final var local = Tool.builder( "query", schema ).title( "Query title" ).description( "Query description" )
            .outputSchema( schema ).annotations( ToolAnnotations.builder().readOnlyHint( true ).build() )
            .icons( List.of( Icon.builder( "https://example.com/icon.svg" ).mimeType( "image/svg+xml" ).build() ) )
            .meta( metadata ).build();
        started( "com.enonic.app.guillotine" );
        groups( "com.enonic.app.guillotine", loaded( "com.enonic.app.guillotine:content", List.of( local ) ) );

        final var publicTool = catalog.list( context, null ).tools().getFirst();

        assertThat( publicTool ).isEqualTo( new Tool( "com.enonic.app.guillotine__content__query", local.title(), local.description(),
            local.inputSchema(), local.outputSchema(), local.annotations(), local.meta(), local.icons() ) );
        assertThat( publicTool.inputSchema() ).isSameAs( local.inputSchema() );
        assertThat( publicTool.outputSchema() ).isSameAs( local.outputSchema() );
        assertThat( publicTool.meta() ).isSameAs( local.meta() );
        assertThat( local.name() ).isEqualTo( "query" );
        assertThat( catalog.find( context, publicTool.name() ).definition() ).isEqualTo( publicTool );
    }

    @Test
    void escapes_delimiters_escape_sequences_and_invalid_characters_without_collisions()
    {
        started( "app" );
        groups( "app", group( "app:a", "b__c", "b_u_uc", "Echo", "echo" ), group( "app:a__b", "c" ),
                group( "app:a_u_ub", "c" ), group( "app:é", "c" ), group( "app:_x00e9", "c" ),
                group( "app:a_", "_b" ) );

        assertThat( catalog.list( context, null ).tools() ).extracting( Tool::name )
            .containsExactlyInAnyOrder( "app__a__b_u_uc", "app__a__b_uu_uuc", "app__a__Echo", "app__a__echo",
                                        "app__a_u_ub__c", "app__a_uu_uub__c", "app___x00e9__c", "app___ux00e9__c",
                                        "app__a_u___ub" )
            .doesNotHaveDuplicates().allMatch( name -> name.matches( "[A-Za-z0-9_.-]{1,128}" ) );
    }

    @Test
    void escapes_application_component()
    {
        started( "my_app" );
        groups( "my_app", group( "my_app:g", "t" ) );
        assertThat( catalog.list( context, null ).tools() ).extracting( Tool::name ).containsExactly( "my_uapp__g__t" );
    }

    @Test
    void accepts_128_characters_and_rejects_oversized_names_with_context()
    {
        started( "a" );
        final var accepted = "t".repeat( 122 );
        groups( "a", group( "a:g", accepted ) );
        assertThat( catalog.list( context, null ).tools().getFirst().name() ).hasSize( 128 );

        final var oversized = "t".repeat( 123 );
        groups( "a", group( "a:g", oversized ) );
        assertThatThrownBy( () -> catalog.list( context, null ) ).isInstanceOf( IllegalArgumentException.class )
            .hasMessageContainingAll( "a:g", oversized, "129", "maximum is 128", "Shorten" );
        assertThatThrownBy( () -> catalog.find( context, "missing" ) ).isInstanceOf( IllegalArgumentException.class );
    }

    @Test
    void counts_escaped_length_for_limit()
    {
        started( "a" );
        groups( "a", group( "a:g", "_".repeat( 62 ) ) );
        assertThatThrownBy( () -> catalog.list( context, null ) ).isInstanceOf( IllegalArgumentException.class )
            .hasMessageContainingAll( "a:g", "130", "maximum is 128" );
    }

    @Test
    void binds_original_key_and_local_name_and_preserves_nullable_arguments()
    {
        started( "app" );
        groups( "app", group( "app:group", "local_name" ) );
        final var arguments = new LinkedHashMap<String, @Nullable Object>();
        arguments.put( "value", null );
        final var key = DescriptorKey.from( "app:group" );
        when( executor.execute( key, "local_name", arguments ) ).thenReturn( Map.of( "content", List.of() ) );

        final var binding = catalog.find( context, "app__group__local_uname" );
        assertThat( binding ).isNotNull();
        assertThat( call( binding, CallToolRequest.builder( binding.definition().name() ).arguments( arguments ).build() ).isError() )
            .isNotEqualTo( true );
        verify( executor ).execute( key, "local_name", arguments );
    }

    @Test
    void normalizes_absent_arguments_through_adapter()
    {
        started( "app" );
        groups( "app", group( "app:g", "t" ) );
        when( executor.execute( DescriptorKey.from( "app:g" ), "t", Map.of() ) ).thenReturn(
 Map.of( "content", List.of() ) );
        final var binding = catalog.find( context, "app__g__t" );
        assertThat( call( binding, CallToolRequest.builder( "app__g__t" ).build() ).isError() ).isNotEqualTo( true );
        verify( executor ).execute( DescriptorKey.from( "app:g" ), "t", Map.of() );
    }

    @Test
    void validates_arguments_through_adapter_before_execution()
    {
        started( "app" );
        groups( "app", loaded( "app:g", List.of( Tool.builder( "t", Map.of( "type", "object", "required", List.of( "value" ) ) )
                                                   .build() ) ) );
        final var binding = catalog.find( context, "app__g__t" );
        assertThat( call( binding, CallToolRequest.builder( "app__g__t" ).build() ).isError() ).isTrue();
        verifyNoInteractions( executor );
    }

    @Test
    void observes_app_lifecycle_and_reloaded_descriptors_on_each_lookup()
    {
        final var app = app( "app", true );
        when( applications.getInstalledApplications() ).thenReturn( Applications.from( app ) );
        groups( "app", group( "app:g", "before" ) );
        assertThat( catalog.find( context, "app__g__before" ) ).isNotNull();
        groups( "app", group( "app:g", "after" ) );
        assertThat( catalog.find( context, "app__g__before" ) ).isNull();
        assertThat( catalog.find( context, "app__g__after" ) ).isNotNull();
        when( app.isStarted() ).thenReturn( false );
        assertThat( catalog.find( context, "app__g__after" ) ).isNull();
        assertThat( catalog.list( context, null ).tools() ).isEmpty();
        when( applications.getInstalledApplications() ).thenReturn( Applications.empty() );
        assertThat( catalog.list( context, null ).tools() ).isEmpty();
    }

    @Test
    void rejects_cursor_and_returns_null_for_unknown_names()
    {
        assertThatThrownBy( () -> catalog.list( context, "unknown" ) ).isInstanceOf( McpException.class )
            .hasMessageContaining( "Unknown tools cursor" );
        verifyNoInteractions( applications, descriptors, executor );
        started( "app" );
        groups( "app", group( "app:g", "t" ) );
        assertThat( catalog.find( context, "t" ) ).isNull();
        assertThat( catalog.find( context, "app__g__T" ) ).isNull();
        assertThat( catalog.find( context, "app__g__missing" ) ).isNull();
        assertThat( catalog.find( context, "app__g__t" ) ).isNotNull();
    }

    @Test
    void broken_groups_fail_listing_and_lookup_with_actionable_cause()
    {
        started( "app" );
        final var failure = new IllegalArgumentException( "Invalid MCP descriptor app:/mcp/broken/broken.yaml: invalid inputSchema" );
        when( descriptors.getLoadedByApplication( ApplicationKey.from( "app" ) ) ).thenThrow( failure );
        assertThatThrownBy( () -> catalog.list( context, null ) ).isInstanceOf( IllegalArgumentException.class )
            .hasMessageContainingAll( "application app", "/mcp/broken/broken.yaml", "invalid inputSchema" ).hasCause( failure );
        assertThatThrownBy( () -> catalog.find( context, "app__g__t" ) ).isInstanceOf( IllegalArgumentException.class ).hasCause( failure );
        verifyNoInteractions( executor );
    }

    @Test
    void ignores_new_binding_instances_after_initial_discovery()
    {
        started( "app" );
        groups( "app", group( "app:g", "t" ) );
        catalog.refresh();
        final var changes = new ArrayList<ServerChange>();
        final var subscription = catalog.changes().changes().subscribe( changes::add );
        try
        {
            catalog.refresh();
            groups( "app", group( "app:g", "t" ) );
            catalog.refresh();
            assertThat( catalog.list( context, null ).tools() ).hasSize( 1 );
            assertThat( catalog.find( context, "app__g__t" ) ).isNotNull();
            assertThat( changes ).isEmpty();
        }
        finally
        {
            subscription.dispose();
        }
    }

    @Test
    void first_discovery_notifies_an_already_connected_listener()
    {
        started( "app" );
        groups( "app", group( "app:g", "t" ) );
        final var changes = new ArrayList<ServerChange>();
        final var subscription = catalog.changes().changes().subscribe( changes::add );
        try
        {
            catalog.refresh();
            catalog.refresh();
            assertThat( changes ).containsExactly( new ServerChange.ToolsListChanged() );
        }
        finally
        {
            subscription.dispose();
        }
    }

    @Test
    void observes_refresh_list_and_find_changes_and_publishes_state_before_notification()
    {
        started( "app" );
        groups( "app", group( "app:g", "before" ) );
        catalog.refresh();
        final var observed = new ArrayList<List<String>>();
        final var subscription = catalog.changes().changes().subscribe( change -> {
            assertThat( change ).isEqualTo( new ServerChange.ToolsListChanged() );
            observed.add( catalog.list( context, null ).tools().stream().map( Tool::name ).toList() );
        } );
        try
        {
            groups( "app", group( "app:g", "after" ) );
            catalog.refresh();
            groups( "app", group( "app:g", "last", "first" ) );
            catalog.list( context, null );
            groups( "app", group( "app:g", "first", "last" ) );
            catalog.find( context, "app__g__first" );
            when( applications.getInstalledApplications() ).thenReturn( Applications.empty() );
            catalog.refresh();
            catalog.refresh();
            assertThat( observed ).containsExactly( List.of( "app__g__after" ), List.of( "app__g__last", "app__g__first" ),
                                                   List.of( "app__g__first", "app__g__last" ), List.of() );
        }
        finally
        {
            subscription.dispose();
        }
    }

    @Test
    void compares_every_tool_definition_field()
    {
        started( "app" );
        final var base = Tool.builder( "t", Map.of() ).build();
        final var variants = List.of(
            Tool.builder( "t", Map.of() ).title( "Title" ).build(),
            Tool.builder( "t", Map.of() ).description( "Description" ).build(),
            Tool.builder( "t", Map.of( "type", "object" ) ).build(),
            Tool.builder( "t", Map.of() ).outputSchema( Map.of( "type", "object" ) ).build(),
            Tool.builder( "t", Map.of() ).annotations( ToolAnnotations.builder().readOnlyHint( true ).build() ).build(),
            Tool.builder( "t", Map.of() ).meta( Map.of( "value", 42 ) ).build(),
            Tool.builder( "t", Map.of() ).icons( List.of( Icon.builder( "https://example.com/icon.svg" ).build() ) ).build() );
        groups( "app", loaded( "app:g", List.of( base ) ) );
        catalog.refresh();
        final var changes = new ArrayList<ServerChange>();
        final var subscription = catalog.changes().changes().subscribe( changes::add );
        try
        {
            for ( var variant : variants )
            {
                groups( "app", loaded( "app:g", List.of( variant ) ) );
                catalog.refresh();
                groups( "app", loaded( "app:g", List.of( base ) ) );
                catalog.refresh();
            }
            assertThat( changes ).hasSize( variants.size() * 2 ).containsOnly( new ServerChange.ToolsListChanged() );
        }
        finally
        {
            subscription.dispose();
        }
    }

    @Test
    void broken_application_does_not_hide_other_removals_and_recovery()
    {
        final var broken = app( "broken", true );
        final var removed = app( "removed", true );
        when( applications.getInstalledApplications() ).thenReturn( Applications.from( broken, removed ) );
        groups( "broken", group( "broken:g", "t" ) );
        groups( "removed", group( "removed:g", "t" ) );
        final var binding = catalog.find( context, "removed__g__t" );
        final var changes = new ArrayList<ServerChange>();
        final var subscription = catalog.changes().changes().subscribe( changes::add );
        try
        {
            when( descriptors.getLoadedByApplication( ApplicationKey.from( "broken" ) ) )
                .thenThrow( new IllegalArgumentException( "broken descriptor" ) );
            assertThatThrownBy( catalog::refresh ).hasMessageContaining( "broken descriptor" );
            when( removed.isStarted() ).thenReturn( false );
            assertThatThrownBy( catalog::refresh ).hasMessageContaining( "broken descriptor" );
            assertThatThrownBy( () -> call( binding, CallToolRequest.builder( "removed__g__t" ).build() ) )
                .hasMessageContaining( "broken descriptor" );
            assertThat( changes ).hasSize( 2 );
            when( applications.getInstalledApplications() ).thenReturn( Applications.empty() );
            catalog.refresh();
            assertThat( catalog.list( context, null ).tools() ).isEmpty();
            groups( "broken", group( "broken:g", "fixed" ) );
            when( applications.getInstalledApplications() ).thenReturn( Applications.from( broken ) );
            catalog.refresh();
            assertThat( changes ).hasSize( 3 );
            assertThat( catalog.find( context, "broken__g__fixed" ) ).isNotNull();
            verifyNoInteractions( executor );
        }
        finally
        {
            subscription.dispose();
        }
    }

    @Test
    void retained_bindings_reject_removed_and_changed_definitions()
    {
        started( "app" );
        groups( "app", group( "app:g", "t" ) );
        final var binding = catalog.find( context, "app__g__t" );
        groups( "app", loaded( "app:g", List.of( Tool.builder( "t", Map.of() ).description( "changed" ).build() ) ) );
        assertThatThrownBy( () -> call( binding, CallToolRequest.builder( "app__g__t" ).build() ) )
            .isInstanceOf( McpException.class ).hasMessageContaining( "no longer available" );
        when( applications.getInstalledApplications() ).thenReturn( Applications.empty() );
        assertThatThrownBy( () -> call( binding, CallToolRequest.builder( "app__g__t" ).build() ) )
            .isInstanceOf( McpException.class ).hasMessageContaining( "no longer available" );
        verifyNoInteractions( executor );
    }

    @Test
    void script_execution_does_not_hold_refresh_lock()
    {
        started( "app" );
        groups( "app", group( "app:g", "t" ) );
        final var binding = catalog.find( context, "app__g__t" );
        try ( var worker = Executors.newSingleThreadExecutor() )
        {
            when( executor.execute( DescriptorKey.from( "app:g" ), "t", Map.of() ) ).thenAnswer( invocation -> {
                assertDoesNotThrow( () -> worker.submit( catalog::refresh ).get( 5, TimeUnit.SECONDS ) );
                return Map.of( "content", List.of() );
            } );
            assertThat( call( binding, CallToolRequest.builder( "app__g__t" ).build() ).isError() ).isNotEqualTo( true );
        }
    }

    private void started( final String key )
    {
        final var installed = Applications.from( app( key, true ) );
        when( applications.getInstalledApplications() ).thenReturn( installed );
    }

    private void groups( final String app, final LoadedMcpGroup... groups )
    {
        doReturn( List.of( groups ) ).when( descriptors ).getLoadedByApplication( ApplicationKey.from( app ) );
    }

    private static Application app( final String key, final boolean started )
    {
        final var app = mock( Application.class );
        when( app.getKey() ).thenReturn( ApplicationKey.from( key ) );
        when( app.isStarted() ).thenReturn( started );
        return app;
    }

    private static LoadedMcpGroup group( final String key, final String... names )
    {
        return loaded( key, List.of( names ).stream().map( name -> Tool.builder( name, Map.of() ).build() ).toList() );
    }

    private static LoadedMcpGroup loaded( final String key, final List<Tool> tools )
    {
        return new LoadedMcpGroup( new McpDescriptor( DescriptorKey.from( key ), PrincipalKeys.empty() ), tools );
    }

    private CallToolResult call( final McpSyncTool binding, final CallToolRequest request )
    {
        final var response = binding.handler().call( context, request );
        assertThat( response ).isInstanceOf( McpSyncResponse.Result.class );
        return (CallToolResult) ( (McpSyncResponse.Result<CallToolOutcome>) response ).result();
    }
}
