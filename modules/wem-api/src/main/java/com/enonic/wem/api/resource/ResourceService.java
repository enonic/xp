package com.enonic.wem.api.resource;

import com.enonic.wem.api.module.ModuleResourceKey;

public interface ResourceService
{
    public Resource getResource( ModuleResourceKey key )
        throws ResourceNotFoundException;

    public Resource getResource( ResourceReference ref )
        throws ResourceNotFoundException;
}
