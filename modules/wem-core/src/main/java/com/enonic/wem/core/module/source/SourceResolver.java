package com.enonic.wem.core.module.source;

import com.enonic.wem.api.module.ModuleResourceKey;

public interface SourceResolver
{
    public ModuleSource resolve( ModuleResourceKey key );

    public ModuleSource resolve( ModuleResourceKey base, String uri );
}
