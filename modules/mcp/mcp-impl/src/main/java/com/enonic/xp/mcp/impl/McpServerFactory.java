package com.enonic.xp.mcp.impl;

import org.jspecify.annotations.NullMarked;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.server.VersionInfo;

import io.modelcontextprotocol.json.jackson2.JacksonMcpJsonMapper;
import io.modelcontextprotocol.json.schema.jackson2.DefaultJsonSchemaValidator;
import io.modelcontextprotocol.modern.McpSchema.CacheScope;
import io.modelcontextprotocol.modern.McpSchema.Implementation;
import io.modelcontextprotocol.modern.server.McpServer;
import io.modelcontextprotocol.modern.server.feature.McpChangePublisher;
import io.modelcontextprotocol.modern.server.feature.McpSyncToolRepository;
import io.modelcontextprotocol.modern.server.feature.ToolsFeature;
import io.modelcontextprotocol.modern.server.transport.HttpServletMcpTransport;

@NullMarked
@Component(service = McpServerFactory.class)
public final class McpServerFactory
{
    private final McpSyncToolRepository tools;

    private final McpChangePublisher changes;

    @Activate
    public McpServerFactory( @Reference final McpSyncToolRepository tools, @Reference final McpCatalog catalog )
    {
        this( tools, catalog.changes() );
    }

    McpServerFactory( final McpSyncToolRepository tools, final McpChangePublisher changes )
    {
        this.tools = tools;
        this.changes = changes;
    }

    public HttpServletMcpTransport createTransport( final String endpoint )
    {
        final var objectMapper = new ObjectMapper();
        final var jsonMapper = new JacksonMcpJsonMapper( objectMapper );
        final var server = McpServer.builder()
            .serverInfo( Implementation.builder( "Enonic XP", VersionInfo.get().getVersion() ).build() )
            .jsonMapper( jsonMapper )
            .feature( ToolsFeature.ofSync( tools, jsonMapper, new DefaultJsonSchemaValidator( objectMapper ),
                                          0L, CacheScope.PRIVATE ) )
            .subscriptions( changes )
            .build();
        return HttpServletMcpTransport.builder( server ).jsonMapper( jsonMapper ).endpoint( endpoint ).build();
    }
}
