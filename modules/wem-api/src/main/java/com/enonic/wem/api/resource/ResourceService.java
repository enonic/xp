package com.enonic.wem.api.resource;

public interface ResourceService
{
    public Resource getResource( ModuleResourceKey key )
        throws ResourceNotFoundException;
}
