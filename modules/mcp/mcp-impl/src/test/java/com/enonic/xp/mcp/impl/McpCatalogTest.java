package com.enonic.xp.mcp.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;

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
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@NullMarked
class McpCatalogTest
{
    private final McpDescriptorServiceImpl descriptors = mock( McpDescriptorServiceImpl.class );

    private final ApplicationService applications = mock( ApplicationService.class );

    private final Logger log = mock( Logger.class );

    private final McpCatalog catalog = new McpCatalog( descriptors, applications, log );

    @Test
    void discovers_started_applications_and_groups_in_order_and_preserves_tool_order()
    {
        final var installed = Applications.from( app( "z", true ), app( "stopped", false ), app( "a", true ) );
        when( applications.getInstalledApplications() ).thenReturn( installed );
        groups( "a", group( "a:z", "last", "first" ), group( "a:a", "t" ) );
        groups( "z", group( "z:g", "t" ) );

        catalog.refresh();
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
    void snapshot_reads_the_last_refresh_without_scanning()
    {
        started();
        groups( "app", group( "app:g", "t" ) );
        assertThat( catalog.snapshot().groups() ).isEmpty();
        verifyNoInteractions( applications, descriptors );

        catalog.refresh();
        final var first = catalog.snapshot();
        groups( "app", group( "app:g", "changed" ) );
        assertThat( catalog.snapshot() ).isSameAs( first );
        assertThat( first.groups().getFirst().tools() ).extracting( Tool::name ).containsExactly( "t" );
        verify( applications ).getInstalledApplications();
        verify( descriptors ).getLoadedByApplication( ApplicationKey.from( "app" ) );
        verifyNoMoreInteractions( applications, descriptors );

        catalog.refresh();
        assertThat( catalog.snapshot().groups().getFirst().tools() ).extracting( Tool::name ).containsExactly( "changed" );
    }

    @Test
    void activation_publishes_a_baseline_and_survives_a_failing_scan()
    {
        started();
        groups( "app", group( "app:g", "t" ) );
        catalog.activate();
        assertThat( keys( catalog.snapshot() ) ).containsExactly( "app:g" );

        final var failure = new IllegalStateException( "applications unavailable" );
        when( applications.getInstalledApplications() ).thenThrow( failure );
        final var failing = new McpCatalog( descriptors, applications, log );
        assertDoesNotThrow( failing::activate );
        assertThat( failing.snapshot().groups() ).isEmpty();
        verify( log ).warn( anyString(), eq( failure ) );
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
        catalog.refresh();
        final var first = catalog.snapshot();
        groups( "app", group( "app:a", "t" ), group( "app:z", "t" ) );
        catalog.refresh();
        assertThat( catalog.snapshot() ).isSameAs( first );
        catalog.refresh();
        assertThat( catalog.snapshot() ).isSameAs( first );
        verify( descriptors, times( 3 ) ).getLoadedByApplication( ApplicationKey.from( "app" ) );
    }

    @Test
    void metadata_and_empty_groups_update_snapshot_without_tools_notification()
    {
        started();
        groups( "app", group( "app:g", "t" ) );
        catalog.refresh();
        final var first = catalog.snapshot();
        final var changes = new ArrayList<ServerChange>();
        final var subscription = catalog.changes().changes().subscribe( changes::add );
        try
        {
            final var restricted = new LoadedMcpGroup(
                new McpDescriptor( DescriptorKey.from( "app:g" ), PrincipalKeys.from( "role:system.admin" ) ), first.groups().getFirst().tools() );
            groups( "app", restricted );
            catalog.refresh();
            final var metadata = catalog.snapshot();
            assertThat( metadata ).isNotSameAs( first );
            assertThat( metadata.groups() ).containsExactly( restricted );
            groups( "app", restricted, group( "app:empty" ) );
            catalog.refresh();
            final var added = catalog.snapshot();
            assertThat( added ).isNotSameAs( metadata );
            groups( "app", restricted, group( "app:renamed" ) );
            catalog.refresh();
            assertThat( catalog.snapshot() ).isNotSameAs( added );
            groups( "app", restricted );
            catalog.refresh();
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
            catalog.refresh();
            final var first = catalog.snapshot();
            assertThat( observed ).containsExactly( first );
            assertThat( observed.getFirst() ).isSameAs( first );
            catalog.refresh();
            groups( "app", group( "app:g", "after" ) );
            catalog.refresh();
            final var second = catalog.snapshot();
            assertThat( observed.getLast() ).isSameAs( second );
            when( applications.getInstalledApplications() ).thenReturn( Applications.empty() );
            catalog.refresh();
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
        catalog.refresh();
        final var original = catalog.snapshot();
        final var changes = new ArrayList<ServerChange>();
        final var subscription = catalog.changes().changes().subscribe( changes::add );
        try
        {
            final var cause = new IllegalArgumentException( "broken descriptor" );
            when( descriptors.getLoadedByApplication( ApplicationKey.from( "broken" ) ) ).thenThrow( cause );
            assertDoesNotThrow( catalog::refresh );
            assertThat( keys( catalog.snapshot() ) ).containsExactly( "healthy:g", "removed:g" );
            assertThat( changes ).hasSize( 1 );
            assertThat( warned( "broken" ) ).isSameAs( cause );
            when( removed.isStarted() ).thenReturn( false );
            catalog.refresh();
            assertThat( keys( catalog.snapshot() ) ).containsExactly( "healthy:g" );
            assertThat( changes ).hasSize( 2 );
            catalog.refresh();
            assertThat( changes ).hasSize( 2 );
            verify( log, never() ).info( anyString(), any( Object.class ) );
            groups( "broken", group( "broken:g", "fixed" ) );
            catalog.refresh();
            final var recovered = catalog.snapshot();
            assertThat( recovered ).isNotSameAs( original );
            assertThat( keys( recovered ) ).containsExactly( "broken:g", "healthy:g" );
            assertThat( recovered.groups().getFirst().tools() ).extracting( Tool::name ).containsExactly( "fixed" );
            assertThat( changes ).hasSize( 3 );
            catalog.refresh();
            verify( log ).info( contains( "loaded again" ), eq( ApplicationKey.from( "broken" ) ) );
            verifyNoMoreInteractions( log );
        }
        finally
        {
            subscription.dispose();
        }
    }

    @Test
    void failures_of_stopped_or_uninstalled_applications_are_cleared_once()
    {
        final var stopped = app( "stopped", true );
        final var uninstalled = app( "uninstalled", true );
        when( applications.getInstalledApplications() ).thenReturn( Applications.from( stopped, uninstalled ) );
        when( descriptors.getLoadedByApplication( ApplicationKey.from( "stopped" ) ) ).thenThrow( new IllegalArgumentException( "a" ) );
        when( descriptors.getLoadedByApplication( ApplicationKey.from( "uninstalled" ) ) ).thenThrow( new IllegalArgumentException( "b" ) );
        catalog.refresh();
        assertThat( catalog.snapshot().groups() ).isEmpty();
        warned( "stopped" );
        warned( "uninstalled" );
        when( stopped.isStarted() ).thenReturn( false );
        when( applications.getInstalledApplications() ).thenReturn( Applications.from( stopped ) );
        catalog.refresh();
        catalog.refresh();
        verify( log ).info( contains( "no longer started" ), eq( ApplicationKey.from( "stopped" ) ) );
        verify( log ).info( contains( "no longer started" ), eq( ApplicationKey.from( "uninstalled" ) ) );
        verifyNoMoreInteractions( log );
    }

    @Test
    void failures_are_isolated_per_application_and_logged_once_per_message()
    {
        final var installed = Applications.from( app( "a", true ), app( "b", true ), app( "c", true ) );
        when( applications.getInstalledApplications() ).thenReturn( installed );
        final var first = new IllegalArgumentException( "first" );
        when( descriptors.getLoadedByApplication( ApplicationKey.from( "a" ) ) ).thenThrow( first );
        when( descriptors.getLoadedByApplication( ApplicationKey.from( "b" ) ) ).thenThrow( new IllegalArgumentException( "second" ) );
        groups( "c", group( "c:g", "t" ) );
        final var changes = new ArrayList<ServerChange>();
        final var subscription = catalog.changes().changes().subscribe( changes::add );
        try
        {
            catalog.refresh();
            assertThat( keys( catalog.snapshot() ) ).containsExactly( "c:g" );
            catalog.refresh();
            catalog.refresh();
            verify( descriptors, times( 3 ) ).getLoadedByApplication( ApplicationKey.from( "c" ) );
            assertThat( changes ).containsExactly( new ServerChange.ToolsListChanged() );
            assertThat( warned( "a" ) ).isSameAs( first );
            assertThat( warned( "b" ) ).hasMessage( "second" );
            final var changed = new IllegalArgumentException( "changed" );
            doThrow( changed ).when( descriptors ).getLoadedByApplication( ApplicationKey.from( "a" ) );
            catalog.refresh();
            catalog.refresh();
            final var failures = ArgumentCaptor.forClass( Object.class );
            verify( log, times( 2 ) ).warn( anyString(), eq( ApplicationKey.from( "a" ) ), failures.capture() );
            assertThat( failures.getAllValues() ).containsExactly( first, changed );
            assertThat( changes ).hasSize( 1 );
            verifyNoMoreInteractions( log );
        }
        finally
        {
            subscription.dispose();
        }
    }

    @Test
    void invalid_public_names_exclude_only_their_application()
    {
        final var invalid = app( "a", true );
        final var healthy = app( "healthy", true );
        when( applications.getInstalledApplications() ).thenReturn( Applications.from( invalid, healthy ) );
        groups( "a", group( "a:g", "t".repeat( 123 ) ) );
        groups( "healthy", group( "healthy:g", "t" ) );
        catalog.refresh();
        assertThat( keys( catalog.snapshot() ) ).containsExactly( "healthy:g" );
        assertThat( warned( "a" ) ).hasMessageContainingAll( "a:g", "maximum is 128" );
        final var changes = new ArrayList<ServerChange>();
        final var subscription = catalog.changes().changes().subscribe( changes::add );
        try
        {
            groups( "a", group( "a:g", "t".repeat( 123 ), "sibling" ) );
            catalog.refresh();
            assertThat( changes ).isEmpty();
            when( healthy.isStarted() ).thenReturn( false );
            catalog.refresh();
            assertThat( catalog.snapshot().groups() ).isEmpty();
            assertThat( changes ).containsExactly( new ServerChange.ToolsListChanged() );
            groups( "a", group( "a:g", "fixed" ) );
            catalog.refresh();
            assertThat( catalog.snapshot().groups() ).singleElement().satisfies(
                group -> assertThat( group.tools() ).extracting( Tool::name ).containsExactly( "fixed" ) );
            assertThat( changes ).hasSize( 2 );
            verify( log ).info( contains( "loaded again" ), eq( ApplicationKey.from( "a" ) ) );
            verifyNoMoreInteractions( log );
        }
        finally
        {
            subscription.dispose();
        }
    }

    @Test
    void duplicate_public_names_exclude_only_their_application()
    {
        final var installed = Applications.from( app( "app", true ), app( "other", true ) );
        when( applications.getInstalledApplications() ).thenReturn( installed );
        groups( "app", group( "app:g", "t" ), group( "app:g", "t" ) );
        groups( "other", group( "other:g", "t" ) );
        catalog.refresh();
        assertThat( keys( catalog.snapshot() ) ).containsExactly( "other:g" );
        assertThat( warned( "app" ) ).hasMessage( "Duplicate public MCP tool app__g__t" );
    }

    private Throwable warned( final String application )
    {
        final var failure = ArgumentCaptor.forClass( Object.class );
        verify( log ).warn( contains( "excluded from the catalog" ), eq( ApplicationKey.from( application ) ), failure.capture() );
        return (Throwable) failure.getValue();
    }

    private static List<String> keys( final McpCatalog.Snapshot snapshot )
    {
        return snapshot.groups().stream().map( group -> group.descriptor().getKey().toString() ).toList();
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
