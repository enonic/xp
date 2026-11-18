package com.enonic.xp.mcp.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.mcp.McpDescriptor;
import com.enonic.xp.security.PrincipalKeys;

import io.modelcontextprotocol.modern.McpSchema.Icon;
import io.modelcontextprotocol.modern.McpSchema.Tool;
import io.modelcontextprotocol.modern.McpSchema.ToolAnnotations;
import io.modelcontextprotocol.modern.server.feature.ServerChange;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@NullMarked
class McpCatalogTest
{
    private final McpDescriptorServiceImpl descriptors = mock( McpDescriptorServiceImpl.class );

    private final ApplicationService applications = mock( ApplicationService.class );

    private final McpCatalog catalog = new McpCatalog( descriptors, applications );

    @Test
    void discovers_started_applications_and_groups_in_order_and_preserves_tool_order()
    {
        final var installed = Applications.from( app( "z", true ), app( "stopped", false ), app( "a", true ) );
        when( applications.getInstalledApplications() ).thenReturn( installed );
        groups( "a", group( "a:z", "last", "first" ), group( "a:a", "t" ) );
        groups( "z", group( "z:g", "t" ) );

        final var snapshot = catalog.snapshot();

        assertThat( snapshot.groups() ).extracting( group -> group.descriptor().getKey().toString() )
            .containsExactly( "a:a", "a:z", "z:g" );
        assertThat( snapshot.groups().get( 1 ).tools() ).extracting( Tool::name ).containsExactly( "last", "first" );
        final var order = inOrder( descriptors );
        order.verify( descriptors ).getLoadedByApplication( ApplicationKey.from( "a" ) );
        order.verify( descriptors ).getLoadedByApplication( ApplicationKey.from( "z" ) );
        verify( descriptors, never() ).getLoadedByApplication( ApplicationKey.from( "stopped" ) );
    }

    @Test
    void snapshots_copy_groups_and_are_immutable()
    {
        final var source = new ArrayList<>( List.of( group( "app:g", "t" ) ) );
        final var snapshot = new McpCatalog.Snapshot( source );
        source.clear();
        assertThat( snapshot.groups() ).hasSize( 1 );
        assertThatThrownBy( () -> snapshot.groups().clear() ).isInstanceOf( UnsupportedOperationException.class );
        assertThatThrownBy( () -> snapshot.groups().getFirst().tools().clear() ).isInstanceOf( UnsupportedOperationException.class );
    }

    @Test
    void reuses_snapshot_for_equal_content_from_new_descriptor_instances()
    {
        started();
        groups( "app", group( "app:z", "t" ), group( "app:a", "t" ) );
        final var first = catalog.snapshot();
        groups( "app", group( "app:a", "t" ), group( "app:z", "t" ) );
        assertThat( catalog.snapshot() ).isSameAs( first );
        catalog.refresh();
        assertThat( catalog.snapshot() ).isSameAs( first );
        verify( descriptors, times( 4 ) ).getLoadedByApplication( ApplicationKey.from( "app" ) );
    }

    @Test
    void metadata_and_empty_groups_update_snapshot_without_tools_notification()
    {
        started();
        groups( "app", group( "app:g", "t" ) );
        final var first = catalog.snapshot();
        final var changes = new ArrayList<ServerChange>();
        final var subscription = catalog.changes().changes().subscribe( changes::add );
        try
        {
            final var restricted = new LoadedMcpGroup(
                new McpDescriptor( DescriptorKey.from( "app:g" ), PrincipalKeys.from( "role:system.admin" ) ), first.groups().getFirst().tools() );
            groups( "app", restricted );
            final var metadata = catalog.snapshot();
            assertThat( metadata ).isNotSameAs( first );
            assertThat( metadata.groups() ).containsExactly( restricted );
            groups( "app", restricted, group( "app:empty" ) );
            final var added = catalog.snapshot();
            assertThat( added ).isNotSameAs( metadata );
            groups( "app", restricted, group( "app:renamed" ) );
            assertThat( catalog.snapshot() ).isNotSameAs( added );
            groups( "app", restricted );
            assertThat( catalog.snapshot().groups() ).containsExactly( restricted );
            assertThat( changes ).isEmpty();
        }
        finally
        {
            subscription.dispose();
        }
    }

    @Test
    void first_discovery_notifies_shared_feed_and_publishes_snapshot_before_notification()
    {
        started();
        groups( "app", group( "app:g", "before" ) );
        final var changes = new ArrayList<ServerChange>();
        final var observed = new ArrayList<McpCatalog.Snapshot>();
        assertThat( catalog.changes() ).isSameAs( catalog.changes() );
        final var subscription = catalog.changes().changes().subscribe( change -> {
            changes.add( change );
            observed.add( catalog.snapshot() );
        } );
        try
        {
            final var first = catalog.snapshot();
            assertThat( observed ).containsExactly( first );
            assertThat( observed.getFirst() ).isSameAs( first );
            catalog.refresh();
            groups( "app", group( "app:g", "after" ) );
            final var second = catalog.snapshot();
            assertThat( observed.getLast() ).isSameAs( second );
            when( applications.getInstalledApplications() ).thenReturn( Applications.empty() );
            assertThat( catalog.snapshot().groups() ).isEmpty();
            catalog.refresh();
            assertThat( changes ).hasSize( 3 ).containsOnly( new ServerChange.ToolsListChanged() );
        }
        finally
        {
            subscription.dispose();
        }
    }

    @Test
    void compares_key_qualified_ordered_sdk_definitions()
    {
        started();
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
            groups( "app", loaded( "app:renamed", List.of( base ) ) );
            catalog.refresh();
            groups( "app", group( "app:renamed", "t", "u" ) );
            catalog.refresh();
            groups( "app", group( "app:renamed", "u", "t" ) );
            catalog.refresh();
            assertThat( changes ).hasSize( variants.size() * 2 + 3 ).containsOnly( new ServerChange.ToolsListChanged() );
        }
        finally
        {
            subscription.dispose();
        }
    }

    @Test
    void broken_applications_are_excluded_and_other_removals_and_recovery_are_published()
    {
        final var broken = app( "broken", true );
        final var removed = app( "removed", true );
        final var healthy = app( "healthy", true );
        when( applications.getInstalledApplications() ).thenReturn( Applications.from( broken, removed, healthy ) );
        groups( "broken", group( "broken:g", "t" ) );
        groups( "removed", group( "removed:g", "t" ) );
        groups( "healthy", group( "healthy:g", "t" ) );
        final var original = catalog.snapshot();
        final var changes = new ArrayList<ServerChange>();
        final var subscription = catalog.changes().changes().subscribe( changes::add );
        try
        {
            final var cause = new IllegalArgumentException( "broken descriptor" );
            when( descriptors.getLoadedByApplication( ApplicationKey.from( "broken" ) ) ).thenThrow( cause );
            assertThatThrownBy( catalog::refresh ).hasMessageContaining( "application broken" ).hasCause( cause );
            assertThat( changes ).hasSize( 1 );
            when( removed.isStarted() ).thenReturn( false );
            assertThatThrownBy( catalog::snapshot ).hasMessageContaining( "application broken" );
            assertThat( changes ).hasSize( 2 );
            assertThatThrownBy( catalog::snapshot ).hasMessageContaining( "broken descriptor" );
            assertThat( changes ).hasSize( 2 );
            when( applications.getInstalledApplications() ).thenReturn( Applications.from( healthy ) );
            final var surviving = catalog.snapshot();
            assertThat( surviving ).isNotSameAs( original );
            assertThat( surviving.groups() ).extracting( group -> group.descriptor().getKey().toString() )
                .containsExactly( "healthy:g" );
            assertThat( changes ).hasSize( 2 );
            groups( "broken", group( "broken:g", "fixed" ) );
            when( applications.getInstalledApplications() ).thenReturn( Applications.from( healthy, broken ) );
            assertThat( catalog.snapshot().groups() ).extracting( group -> group.descriptor().getKey().toString() )
                .containsExactly( "broken:g", "healthy:g" );
            assertThat( changes ).hasSize( 3 );
        }
        finally
        {
            subscription.dispose();
        }
    }

    @Test
    void aggregates_contextual_failures_and_continues_loading_applications()
    {
        final var installed = Applications.from( app( "a", true ), app( "b", true ), app( "c", true ) );
        when( applications.getInstalledApplications() ).thenReturn( installed );
        when( descriptors.getLoadedByApplication( ApplicationKey.from( "a" ) ) ).thenThrow( new IllegalArgumentException( "first" ) );
        when( descriptors.getLoadedByApplication( ApplicationKey.from( "b" ) ) ).thenThrow( new IllegalArgumentException( "second" ) );
        groups( "c", group( "c:g", "t" ) );
        final var changes = new ArrayList<ServerChange>();
        final var subscription = catalog.changes().changes().subscribe( changes::add );
        try
        {
            assertThatThrownBy( catalog::snapshot ).hasMessageContaining( "application a: first" ).satisfies( failure -> {
                assertThat( failure.getSuppressed() ).singleElement().satisfies(
                                    suppressed -> assertThat( suppressed ).hasMessageContaining( "application b: second" ) );
            } );
            verify( descriptors ).getLoadedByApplication( ApplicationKey.from( "c" ) );
            assertThat( changes ).containsExactly( new ServerChange.ToolsListChanged() );
        }
        finally
        {
            subscription.dispose();
        }
    }

    @Test
    void invalid_public_names_fail_refresh_without_hiding_other_removals()
    {
        final var invalid = app( "a", true );
        final var healthy = app( "healthy", true );
        when( applications.getInstalledApplications() ).thenReturn( Applications.from( invalid, healthy ) );
        groups( "a", group( "a:g", "t".repeat( 123 ) ) );
        groups( "healthy", group( "healthy:g", "t" ) );
        assertThatThrownBy( catalog::refresh ).hasMessageContainingAll( "application a", "maximum is 128" );
        final var changes = new ArrayList<ServerChange>();
        final var subscription = catalog.changes().changes().subscribe( changes::add );
        try
        {
            groups( "a", group( "a:g", "t".repeat( 123 ), "sibling" ) );
            assertThatThrownBy( catalog::refresh ).hasMessageContaining( "maximum is 128" );
            assertThat( changes ).isEmpty();
            when( healthy.isStarted() ).thenReturn( false );
            assertThatThrownBy( catalog::refresh ).hasMessageContaining( "maximum is 128" );
            assertThat( changes ).containsExactly( new ServerChange.ToolsListChanged() );
            groups( "a", group( "a:g", "fixed" ) );
            assertThat( catalog.snapshot().groups() ).singleElement().satisfies(
                group -> assertThat( group.tools() ).extracting( Tool::name ).containsExactly( "fixed" ) );
            assertThat( changes ).hasSize( 2 );
        }
        finally
        {
            subscription.dispose();
        }
    }

    @Test
    void duplicate_public_names_fail_during_refresh()
    {
        started();
        groups( "app", group( "app:g", "t" ), group( "app:g", "t" ) );
        assertThatThrownBy( catalog::refresh ).hasMessageContainingAll( "application app", "Duplicate public MCP tool app__g__t" );
    }

    private void started()
    {
        final var installed = Applications.from( app( "app", true ) );
        when( applications.getInstalledApplications() ).thenReturn( installed );
    }

    private void groups( final String application, final LoadedMcpGroup... groups )
    {
        doReturn( List.of( groups ) ).when( descriptors ).getLoadedByApplication( ApplicationKey.from( application ) );
    }

    private static Application app( final String name, final boolean started )
    {
        final var app = mock( Application.class );
        when( app.getKey() ).thenReturn( ApplicationKey.from( name ) );
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
}
