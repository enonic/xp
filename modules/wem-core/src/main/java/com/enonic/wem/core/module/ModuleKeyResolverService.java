package com.enonic.wem.core.module;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.module.ModuleKeyResolver;

public interface ModuleKeyResolverService
{
    ModuleKeyResolver forContent( final Content content );

    ModuleKeyResolver forContent( final ContentPath contentPath );
}
