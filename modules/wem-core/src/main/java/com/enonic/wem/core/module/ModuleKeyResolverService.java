package com.enonic.wem.core.module;


import com.google.inject.ImplementedBy;

import com.enonic.wem.api.content.ContentPath;

@ImplementedBy(ModuleKeyResolverServiceImpl.class)
public interface ModuleKeyResolverService
{
    ModuleKeyResolver getModuleKeyResolverForContent( final ContentPath contentPath );
}
