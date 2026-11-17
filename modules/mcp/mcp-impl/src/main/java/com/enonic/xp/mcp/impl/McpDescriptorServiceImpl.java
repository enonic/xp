package com.enonic.xp.mcp.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.NullMarked;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.descriptor.DescriptorKeyLocator;
import com.enonic.xp.mcp.McpDescriptor;
import com.enonic.xp.mcp.McpDescriptorService;
import com.enonic.xp.mcp.McpDescriptors;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProcessor;
import com.enonic.xp.resource.ResourceService;

@NullMarked
@Component(immediate = true, service = {McpDescriptorService.class, McpDescriptorServiceImpl.class})
public final class McpDescriptorServiceImpl
    implements McpDescriptorService
{
    private static final String ROOT_PATH = "/mcp";

    private final ResourceService resourceService;

    private final DescriptorKeyLocator descriptorKeyLocator;

    @Activate
    public McpDescriptorServiceImpl( @Reference final ResourceService resourceService )
    {
        this.resourceService = resourceService;
        this.descriptorKeyLocator = new DescriptorKeyLocator( resourceService, ROOT_PATH, false );
    }

    @Override
    public Optional<McpDescriptor> getByKey( final DescriptorKey key )
    {
        return getLoadedByKey( key ).map( LoadedMcpGroup::descriptor );
    }

    public Optional<LoadedMcpGroup> getLoadedByKey( final DescriptorKey key )
    {
        final var processor = new ResourceProcessor.Builder<DescriptorKey, LoadedMcpGroup>().key( key )
            .segment( "mcpDescriptor" )
            .keyTranslator( this::getDescriptorResourceKey )
            .processor( resource -> loadDescriptor( key, resource ) )
            .build();
        return Optional.ofNullable( resourceService.processResource( processor ) );
    }

    @Override
    public McpDescriptors getByApplication( final ApplicationKey applicationKey )
    {
        return getLoadedByApplication( applicationKey ).stream()
            .map( LoadedMcpGroup::descriptor )
            .collect( McpDescriptors.collector() );
    }

    public List<LoadedMcpGroup> getLoadedByApplication( final ApplicationKey applicationKey )
    {
        return descriptorKeyLocator.findKeys( applicationKey )
            .stream()
            .sorted( Comparator.comparing( DescriptorKey::getName ) )
            .map( this::getLoadedByKey )
            .flatMap( Optional::stream )
            .toList();
    }

    @Override
    public ResourceKey getControllerResourceKey( final DescriptorKey key )
    {
        return toResourceKey( key, "js" );
    }

    private ResourceKey getDescriptorResourceKey( final DescriptorKey key )
    {
        final var yamlKey = toResourceKey( key, "yaml" );
        return resourceService.getResource( yamlKey ).exists() ? yamlKey : toResourceKey( key, "yml" );
    }

    private LoadedMcpGroup loadDescriptor( final DescriptorKey key, final Resource resource )
    {
        try
        {
            return YmlMcpDescriptorParser.parse( resource.readString(), key );
        }
        catch ( RuntimeException e )
        {
            throw new IllegalArgumentException( "Invalid MCP descriptor " + resource.getKey() + ": " + e.getMessage(), e );
        }
    }

    private static ResourceKey toResourceKey( final DescriptorKey key, final String extension )
    {
        return ResourceKey.from( key.getApplicationKey(), ROOT_PATH + "/" + key.getName() + "/" + key.getName() + "." + extension );
    }
}
