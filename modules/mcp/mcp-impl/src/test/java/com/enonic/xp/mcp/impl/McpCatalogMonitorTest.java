package com.enonic.xp.mcp.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.event.Event;
import com.enonic.xp.mcp.McpDescriptor;
import com.enonic.xp.security.PrincipalKeys;

import io.modelcontextprotocol.modern.McpSchema.Tool;

import io.modelcontextprotocol.modern.server.feature.ServerChange;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@NullMarked
class McpCatalogMonitorTest
{
    private final McpCatalog catalog = mock( McpCatalog.class );

    private final ScheduledExecutorService scheduler = mock( ScheduledExecutorService.class );

    @Test
    void establishes_baseline_before_scheduling_single_periodic_scan()
    {
        final var monitor = new McpCatalogMonitor( catalog, scheduler );
        try
        {
            final var scan = ArgumentCaptor.forClass( Runnable.class );
            final var order = inOrder( catalog, scheduler );
            order.verify( catalog ).refresh();
            order.verify( scheduler ).scheduleWithFixedDelay( scan.capture(), eq( 1L ), eq( 1L ), eq( TimeUnit.SECONDS ) );
            scan.getValue().run();
            monitor.scan();
            verify( catalog, times( 3 ) ).refresh();
        }
        finally
        {
            monitor.deactivate();
        }
    }

    @Test
    void refreshes_for_native_application_lifecycle_events()
    {
        final var monitor = new McpCatalogMonitor( catalog, scheduler );
        try
        {
            clearInvocations( catalog );
            for ( var type : new String[]{"INSTALLED", "STARTED", "STOPPED", "UNINSTALLED"} )
            {
                monitor.onEvent( Event.create( "application" ).value( "eventType", type ).build() );
            }
            verify( catalog, times( 4 ) ).refresh();
            clearInvocations( catalog );
            monitor.onEvent( Event.create( "application" ).value( "eventType", "UPDATED" ).build() );
            monitor.onEvent( Event.create( "application" ).build() );
            monitor.onEvent( Event.create( "other" ).value( "eventType", "STARTED" ).build() );
            verifyNoInteractions( catalog );
        }
        finally
        {
            monitor.deactivate();
        }
    }

    @Test
    void refresh_failures_allow_subsequent_scans_and_events()
    {
        doThrow( new IllegalArgumentException( "broken descriptor" ) ).doNothing().when( catalog ).refresh();
        final var monitor = new McpCatalogMonitor( catalog, scheduler );
        try
        {
            final var scan = ArgumentCaptor.forClass( Runnable.class );
            verify( scheduler ).scheduleWithFixedDelay( scan.capture(), eq( 1L ), eq( 1L ), eq( TimeUnit.SECONDS ) );
            doThrow( new IllegalArgumentException( "broken descriptor" ) ).when( catalog ).refresh();
            assertDoesNotThrow( scan.getValue()::run );
            assertDoesNotThrow( () -> monitor.onEvent( Event.create( "application" ).value( "eventType", "STARTED" ).build() ) );
            doNothing().when( catalog ).refresh();
            monitor.scan();
            verify( catalog, times( 4 ) ).refresh();
        }
        finally
        {
            monitor.deactivate();
        }
    }

    @Test
    void scan_observes_descriptor_edits_without_application_events()
    {
        final var descriptors = mock( McpDescriptorServiceImpl.class );
        final var applications = mock( ApplicationService.class );

        final var app = mock( Application.class );
        final var key = ApplicationKey.from( "app" );
        when( app.getKey() ).thenReturn( key );
        when( app.isStarted() ).thenReturn( true );
        when( applications.getInstalledApplications() ).thenReturn( Applications.from( app ) );
        final var descriptor = new McpDescriptor( DescriptorKey.from( "app:g" ), PrincipalKeys.empty() );
        when( descriptors.getLoadedByApplication( key ) ).thenReturn(
            List.of( new LoadedMcpGroup( descriptor, List.of( Tool.builder( "before", Map.of() ).build() ) ) ) );
        final var actualCatalog = new McpCatalog( descriptors, applications );
        final var monitor = new McpCatalogMonitor( actualCatalog, scheduler );
        final var changes = new ArrayList<ServerChange>();
        final var subscription = actualCatalog.changes().changes().subscribe( changes::add );
        try
        {
            when( descriptors.getLoadedByApplication( key ) ).thenReturn(
                List.of( new LoadedMcpGroup( descriptor, List.of( Tool.builder( "after", Map.of() ).build() ) ) ) );
            monitor.scan();
            monitor.scan();
            assertThat( changes ).containsExactly( new ServerChange.ToolsListChanged() );
            assertThat( actualCatalog.snapshot().groups() ).singleElement().satisfies(
                group -> assertThat( group.tools() ).extracting( Tool::name ).containsExactly( "after" ) );
        }
        finally
        {
            subscription.dispose();
            monitor.deactivate();
        }
    }

    @Test
    void deactivation_stops_scheduler_and_ignores_late_scans_and_events()
    {
        final var monitor = new McpCatalogMonitor( catalog, scheduler );
        final var scan = ArgumentCaptor.forClass( Runnable.class );
        verify( scheduler ).scheduleWithFixedDelay( scan.capture(), eq( 1L ), eq( 1L ), eq( TimeUnit.SECONDS ) );
        monitor.deactivate();
        verify( scheduler ).shutdownNow();
        clearInvocations( catalog );
        scan.getValue().run();
        monitor.scan();
        monitor.onEvent( Event.create( "application" ).value( "eventType", "STOPPED" ).build() );
        verifyNoInteractions( catalog );
    }
}
