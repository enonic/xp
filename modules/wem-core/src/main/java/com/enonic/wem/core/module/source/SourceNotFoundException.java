package com.enonic.wem.core.module.source;

import com.enonic.wem.api.module.ModuleResourceKey;

public final class SourceNotFoundException
    extends RuntimeException
{
    private final ModuleResourceKey resource;

    public SourceNotFoundException( final ModuleResourceKey resource )
    {
        super( resource.toString() + " not found" );
        this.resource = resource;
    }

    public ModuleResourceKey getResource()
    {
        return this.resource;
    }

}
