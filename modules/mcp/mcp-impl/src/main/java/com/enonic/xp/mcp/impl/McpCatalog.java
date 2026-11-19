package com.enonic.xp.mcp.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.NullMarked;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.descriptor.DescriptorKey;

import io.modelcontextprotocol.modern.McpSchema.Tool;
import io.modelcontextprotocol.modern.server.feature.McpChangeFeed;
import io.modelcontextprotocol.modern.server.feature.McpChangePublisher;
import io.modelcontextprotocol.modern.server.feature.ServerChange;

@NullMarked
@Component(service = McpCatalog.class)
public final class McpCatalog
{
    private static final Logger LOG = LoggerFactory.getLogger( McpCatalog.class );

    private final McpDescriptorServiceImpl descriptors;

    private final ApplicationService applications;

    private final Logger log;

    private final McpChangeFeed changeFeed = new McpChangeFeed();

    private Snapshot snapshot = new Snapshot( List.of() );

    private List<ToolDefinition> definitions = List.of();

    private Map<ApplicationKey, String> failures = Map.of();

    @Activate
    public McpCatalog( @Reference final McpDescriptorServiceImpl descriptors, @Reference final ApplicationService applications )
    {
        this( descriptors, applications, LOG );
    }

    McpCatalog( final McpDescriptorServiceImpl descriptors, final ApplicationService applications, final Logger log )
    {
        this.descriptors = descriptors;
        this.applications = applications;
        this.log = log;
    }

    public synchronized Snapshot snapshot()
    {
        final var groups = new ArrayList<LoadedMcpGroup>();
        final var failed = new HashMap<ApplicationKey, String>();
        final var started = applications.getInstalledApplications().stream()
            .filter( Application::isStarted )
            .map( Application::getKey )
            .sorted( Comparator.comparing( ApplicationKey::toString ) )
            .toList();
        for ( var application : started )
        {
            try
            {
                final var loaded = descriptors.getLoadedByApplication( application ).stream()
                    .sorted( Comparator.comparing( group -> group.descriptor().getKey().getName() ) )
                    .toList();
                validateNames( loaded );
                groups.addAll( loaded );
            }
            catch ( RuntimeException e )
            {
                final var message = String.valueOf( e.getMessage() );
                failed.put( application, message );
                if ( !message.equals( failures.get( application ) ) )
                {
                    log.warn( "Cannot load MCP groups for application {}; its tools are excluded from the catalog", application, e );
                }
            }
        }
        for ( var application : failures.keySet() )
        {
            if ( failed.containsKey( application ) )
            {
                continue;
            }
            if ( started.contains( application ) )
            {
                log.info( "MCP groups for application {} loaded again; its tools are back in the catalog", application );
            }
            else
            {
                log.info( "Cleared MCP load failure for application {}; the application is no longer started", application );
            }
        }
        failures = Map.copyOf( failed );
        if ( !sameGroups( snapshot.groups(), groups ) )
        {
            snapshot = new Snapshot( groups );
        }
        final var current = groups.stream()
            .flatMap( group -> group.tools().stream().map( tool -> new ToolDefinition( group.descriptor().getKey(), tool ) ) )
            .toList();
        final var previous = definitions;
        definitions = current;
        if ( !previous.equals( current ) )
        {
            changeFeed.broadcast( new ServerChange.ToolsListChanged() );
        }
        return snapshot;
    }

    public void refresh()
    {
        snapshot();
    }

    public McpChangePublisher changes()
    {
        return changeFeed;
    }

    private static void validateNames( final List<LoadedMcpGroup> groups )
    {
        final var names = new HashSet<String>();
        for ( var group : groups )
        {
            for ( var tool : group.tools() )
            {
                final var name = McpNames.qualify( group.descriptor().getKey(), tool.name() );
                if ( !names.add( name ) )
                {
                    throw new IllegalArgumentException( "Duplicate public MCP tool " + name );
                }
            }
        }
    }

    private static boolean sameGroups( final List<LoadedMcpGroup> previous, final List<LoadedMcpGroup> current )
    {
        if ( previous.size() != current.size() )
        {
            return false;
        }
        for ( int i = 0; i < previous.size(); i++ )
        {
            final var before = previous.get( i );
            final var after = current.get( i );
            if ( !before.descriptor().getKey().equals( after.descriptor().getKey() ) ||
                !before.descriptor().getAllowedPrincipals().equals( after.descriptor().getAllowedPrincipals() ) ||
                !before.tools().equals( after.tools() ) )
            {
                return false;
            }
        }
        return true;
    }

    public record Snapshot(List<LoadedMcpGroup> groups)
    {
        public Snapshot
        {
            groups = List.copyOf( groups );
        }
    }

    private record ToolDefinition(DescriptorKey key, Tool tool)
    {
    }
}
