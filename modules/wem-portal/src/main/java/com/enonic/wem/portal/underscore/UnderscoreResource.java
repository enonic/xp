package com.enonic.wem.portal.underscore;

import javax.inject.Inject;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.module.ModuleKeyResolver;
import com.enonic.wem.core.module.ModuleKeyResolverService;
import com.enonic.wem.core.module.ModuleResourcePathResolver;

public abstract class UnderscoreResource
{
    @Inject
    protected Client client;

    @Inject
    protected ModuleResourcePathResolver modulePathResolver;

    @Inject
    protected ModuleKeyResolverService moduleKeyResolver;

    protected final ModuleKey resolveModule( final String contentPath, final String moduleName )
    {
        try
        {
            // module key with version
            return ModuleKey.from( moduleName );
        }
        catch ( final Exception e )
        {
            // just module name, needs resolving to module key
            final ContentPath path = ContentPath.from( contentPath );
            final ModuleKeyResolver moduleResolver = this.moduleKeyResolver.forContent( path );
            return moduleResolver.resolve( ModuleName.from( moduleName ) );
        }
    }
}
