package com.enonic.wem.core.module;


import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleName;

public interface ModuleKeyResolver
{
    ModuleKey resolve( final ModuleName moduleName );
}
