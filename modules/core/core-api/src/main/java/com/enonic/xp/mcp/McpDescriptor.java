package com.enonic.xp.mcp;

import java.util.Objects;

import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.security.PrincipalKeys;

/**
 * Application-owned metadata for an MCP tool group.
 */
public final class McpDescriptor
{
    private final DescriptorKey key;

    private final PrincipalKeys allowedPrincipals;

    /**
     * Creates the metadata for an MCP tool group.
     *
     * @param key application and local group key
     * @param allowedPrincipals declared allowed principals, possibly empty
     * @throws NullPointerException if an argument is null
     */
    public McpDescriptor( final DescriptorKey key, final PrincipalKeys allowedPrincipals )
    {
        this.key = Objects.requireNonNull( key, "key" );
        this.allowedPrincipals = Objects.requireNonNull( allowedPrincipals, "allowedPrincipals" );
    }

    /**
     * Gets the descriptor key.
     *
     * @return application and local group key
     */
    public DescriptorKey getKey()
    {
        return key;
    }

    /**
     * Gets the declared allowed principals.
     *
     * @return allowed principals, possibly empty
     */
    public PrincipalKeys getAllowedPrincipals()
    {
        return allowedPrincipals;
    }
}
