package com.enonic.wem.portal.base;

import javax.inject.Inject;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeyResolver;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.core.module.ModuleKeyResolverService;
import com.enonic.wem.core.module.ModuleResourcePathResolver;

public abstract class ModuleBaseHandler
    extends BaseHandler
{
    @Inject
    protected ModuleResourcePathResolver modulePathResolver;

    @Inject
    protected ModuleService moduleService;

    @Inject
    protected ModuleKeyResolverService moduleKeyResolverService;

    protected final ModuleKey resolveModule( final String contentPath, final String module )
    {
        final ContentPath path = ContentPath.from( contentPath );

        try
        {
            return ModuleKey.from( module );
        }
        catch ( final Exception e )
        {
            return resolveModuleFromSite( path, module );
        }
    }

    private ModuleKey resolveModuleFromSite( final ContentPath contentPath, final String moduleName )
    {
        final ModuleKeyResolver moduleKeyResolver = this.moduleKeyResolverService.forContent( contentPath );
        return moduleKeyResolver.resolve( ModuleName.from( moduleName ) );
    }
}
