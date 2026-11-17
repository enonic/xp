package com.enonic.xp.mcp;

import java.util.Optional;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.resource.ResourceKey;

/**
 * Provides application-owned MCP group metadata and controller resource keys.
 */
public interface McpDescriptorService
{
    /**
     * Finds MCP group metadata by application and group key.
     *
     * @param key descriptor key
     * @return the descriptor, or an empty optional when it is absent
     */
    Optional<McpDescriptor> getByKey( DescriptorKey key );

    /**
     * Gets the MCP group metadata belonging to an application.
     *
     * @param applicationKey application key
     * @return the application's descriptors, possibly empty
     */
    McpDescriptors getByApplication( ApplicationKey applicationKey );

    /**
     * Gets the controller resource key for an MCP descriptor.
     *
     * @param key descriptor key
     * @return the controller resource key
     */
    ResourceKey getControllerResourceKey( DescriptorKey key );
}
