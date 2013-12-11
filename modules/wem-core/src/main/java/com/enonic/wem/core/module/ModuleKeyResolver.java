package com.enonic.wem.core.module;


import com.google.common.base.Optional;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleName;

public interface ModuleKeyResolver
{
    Optional<ModuleKey> resolve( final ModuleName moduleName );
}
