package com.enonic.xp.mcp.impl;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.descriptor.DescriptorKey;

import io.modelcontextprotocol.modern.McpException;
import io.modelcontextprotocol.modern.McpSchema.Tool;
import io.modelcontextprotocol.modern.server.McpRequestContext;
import io.modelcontextprotocol.modern.server.feature.McpSyncTool;
import io.modelcontextprotocol.modern.server.feature.McpSyncToolRepository;
import io.modelcontextprotocol.modern.server.feature.ToolsPage;

@NullMarked
@Component(service = McpSyncToolRepository.class)
public final class McpToolCatalog
    implements McpSyncToolRepository
{
    private final McpCatalog catalog;

    private final McpToolExecutor executor;

    private McpCatalog.Snapshot boundSnapshot = new McpCatalog.Snapshot( List.of() );

    private Map<String, McpSyncTool> bindings = Map.of();

    @Activate
    public McpToolCatalog( @Reference final McpCatalog catalog, @Reference final McpToolExecutor executor )
    {
        this.catalog = catalog;
        this.executor = executor;
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

    private Map<String, McpSyncTool> loadTools()
    {
        return bind( catalog.snapshot() );
    }

    private synchronized Map<String, McpSyncTool> bind( final McpCatalog.Snapshot snapshot )
    {
        if ( snapshot == boundSnapshot )
        {
            return bindings;
        }
        final var tools = new LinkedHashMap<String, McpSyncTool>();
        for ( var group : snapshot.groups() )
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
        bindings = Collections.unmodifiableMap( tools );
        boundSnapshot = snapshot;
        return bindings;
    }

    private static Tool namespace( final DescriptorKey key, final Tool tool )
    {
        final var name = McpNames.qualify( key, tool.name() );
        return new Tool( name, tool.title(), tool.description(), tool.inputSchema(), tool.outputSchema(), tool.annotations(),
                         tool.meta(), tool.icons() );
    }

}
