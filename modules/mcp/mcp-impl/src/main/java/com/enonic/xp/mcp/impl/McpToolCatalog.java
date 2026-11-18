package com.enonic.xp.mcp.impl;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.descriptor.DescriptorKey;

import io.modelcontextprotocol.modern.McpException;
import io.modelcontextprotocol.modern.McpSchema.Tool;
import io.modelcontextprotocol.modern.server.McpRequestContext;
import io.modelcontextprotocol.modern.server.feature.McpChangeFeed;
import io.modelcontextprotocol.modern.server.feature.McpChangePublisher;
import io.modelcontextprotocol.modern.server.feature.McpSyncTool;
import io.modelcontextprotocol.modern.server.feature.McpSyncToolRepository;
import io.modelcontextprotocol.modern.server.feature.ServerChange;
import io.modelcontextprotocol.modern.server.feature.ToolsPage;

@NullMarked
@Component(service = {McpSyncToolRepository.class, McpToolCatalog.class})
public final class McpToolCatalog
    implements McpSyncToolRepository
{
    private final McpDescriptorServiceImpl descriptorService;

    private final ApplicationService applicationService;

    private final McpToolExecutor executor;

    private final McpChangeFeed changeFeed = new McpChangeFeed();

    private List<Tool> definitions = List.of();

    @Activate
    public McpToolCatalog( @Reference final McpDescriptorServiceImpl descriptorService,
                           @Reference final ApplicationService applicationService, @Reference final McpToolExecutor executor )
    {
        this.descriptorService = descriptorService;
        this.applicationService = applicationService;
        this.executor = executor;
    }

    public McpChangePublisher changes()
    {
        return changeFeed;
    }

    public void refresh()
    {
        loadTools();
    }

    @Override
    public ToolsPage list( final McpRequestContext context, final @Nullable String cursor )
    {
        if ( cursor != null )
        {
            throw McpException.invalidParams( "Unknown tools cursor: " + cursor );
        }
        return ToolsPage.of( loadTools().values().stream().map( McpSyncTool::definition ).toList() );
    }

    @Override
    public @Nullable McpSyncTool find( final McpRequestContext context, final String name )
    {
        return loadTools().get( name );
    }

    private synchronized Map<String, McpSyncTool> loadTools()
    {
        final var tools = new LinkedHashMap<String, McpSyncTool>();
        RuntimeException failure = null;
        final var applications = applicationService.getInstalledApplications().stream()
            .filter( Application::isStarted )
            .map( Application::getKey )
            .sorted( Comparator.comparing( ApplicationKey::toString ) )
            .toList();
        for ( var application : applications )
        {
            try
            {
                tools.putAll( loadApplicationTools( application ) );
            }
            catch ( RuntimeException e )
            {
                if ( failure == null )
                {
                    failure = e;
                }
                else
                {
                    failure.addSuppressed( e );
                }
            }
        }
        final var current = tools.values().stream().map( McpSyncTool::definition ).toList();
        final var previous = definitions;
        definitions = current;
        if ( !previous.equals( current ) )
        {
            changeFeed.broadcast( new ServerChange.ToolsListChanged() );
        }
        if ( failure != null )
        {
            throw failure;
        }
        return tools;
    }

    private Map<String, McpSyncTool> loadApplicationTools( final ApplicationKey application )
    {
        final var tools = new LinkedHashMap<String, McpSyncTool>();
        for ( var group : loadGroups( application ) )
        {
            final var key = group.descriptor().getKey();
            for ( var tool : group.tools() )
            {
                final var publicTool = namespace( key, tool );
                final var binding = McpToolAdapter.bind( publicTool, ( name, arguments ) -> {
                    final var current = loadTools().get( name );
                    if ( current == null || !current.definition().equals( publicTool ) )
                    {
                        throw McpException.invalidParams( "MCP tool is no longer available: " + name );
                    }
                    return executor.execute( key, tool.name(), arguments );
                } );
                if ( tools.putIfAbsent( publicTool.name(), binding ) != null )
                {
                    throw new IllegalArgumentException( "Duplicate public MCP tool " + publicTool.name() + " in " + key );
                }
            }
        }
        return tools;
    }

    private List<LoadedMcpGroup> loadGroups( final ApplicationKey application )
    {
        try
        {
            return descriptorService.getLoadedByApplication( application ).stream()
                .sorted( Comparator.comparing( group -> group.descriptor().getKey().getName() ) )
                .toList();
        }
        catch ( RuntimeException e )
        {
            throw new IllegalArgumentException( "Cannot load MCP groups for application " + application + ": " + e.getMessage(), e );
        }
    }

    private static Tool namespace( final DescriptorKey key, final Tool tool )
    {
        final var name = encode( key.getApplicationKey().toString() ) + "__" + encode( key.getName() ) + "__" + encode( tool.name() );
        if ( name.length() > 128 )
        {
            throw new IllegalArgumentException( "Public MCP tool name for " + key + " tool '" + tool.name() + "' has " + name.length() +
                                                    " characters; maximum is 128. Shorten the application, group or tool name." );
        }
        return new Tool( name, tool.title(), tool.description(), tool.inputSchema(), tool.outputSchema(), tool.annotations(),
                         tool.meta(), tool.icons() );
    }

    private static String encode( final String component )
    {
        final var encoded = new StringBuilder();
        for ( int i = 0; i < component.length(); i++ )
        {
            final char c = component.charAt( i );
            if ( c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '.' || c == '-' )
            {
                encoded.append( c );
            }
            else if ( c == '_' )
            {
                encoded.append( "_u" );
            }
            else
            {
                encoded.append( "_x" );
                for ( int shift = 12; shift >= 0; shift -= 4 )
                {
                    encoded.append( Character.forDigit( ( c >> shift ) & 0xf, 16 ) );
                }
            }
        }
        return encoded.toString();
    }
}
