package com.enonic.xp.mcp.impl;

import java.util.HashSet;
import java.util.List;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.mcp.McpDescriptor;

import io.modelcontextprotocol.modern.McpSchema.Tool;

import static java.util.Objects.requireNonNull;

@NullMarked
public record LoadedMcpGroup(McpDescriptor descriptor, List<Tool> tools)
{
    public LoadedMcpGroup
    {
        requireNonNull( descriptor, "descriptor" );
        tools = List.copyOf( tools );
        final var names = new HashSet<String>();
        for ( int i = 0; i < tools.size(); i++ )
        {
            final var tool = tools.get( i );
            try
            {
                McpToolAdapter.validateTool( tool );
            }
            catch ( IllegalArgumentException e )
            {
                throw new IllegalArgumentException( "Invalid MCP descriptor tools[" + i + "]: " + e.getMessage(), e );
            }
            if ( !names.add( tool.name() ) )
            {
                throw new IllegalArgumentException( "Duplicate MCP tool name: " + tool.name() );
            }
        }
    }
}
