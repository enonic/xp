package com.enonic.wem.api.resource;

import com.enonic.wem.api.module.ModuleResourceKey;

public interface ResourceService
{
    public boolean hasResource( ModuleResourceKey key );

    public Resource getResource( ModuleResourceKey key )
        throws ResourceNotFoundException;

    public boolean hasResource( ResourceReference ref );

    public Resource getResource( ResourceReference ref )
        throws ResourceNotFoundException;
}
