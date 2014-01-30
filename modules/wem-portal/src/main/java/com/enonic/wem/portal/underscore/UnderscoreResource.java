package com.enonic.wem.portal.underscore;

import javax.inject.Inject;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.module.ModuleKeyResolver;
import com.enonic.wem.core.module.ModuleKeyResolverService;
import com.enonic.wem.core.module.ModuleResourcePathResolver;
import com.enonic.wem.portal.exception.PortalWebException;

public abstract class UnderscoreResource
{
    @Inject
    protected Client client;

    @Inject
    protected ModuleResourcePathResolver modulePathResolver;

    @Inject
    protected ModuleKeyResolverService moduleKeyResolverService;

    protected final ModuleKey resolveModule( final String contentPath, final String moduleName )
    {
        final ContentPath path = ContentPath.from( contentPath );

        try
        {
            return ModuleKey.from( moduleName );
        }
        catch ( final Exception e )
        {
            return resolveModuleFromSite( path, moduleName );
        }
    }

    private ModuleKey resolveModuleFromSite( final ContentPath contentPath, final String moduleName )
    {
        final ModuleKeyResolver moduleKeyResolver = this.moduleKeyResolverService.forContent( contentPath );
        final ModuleKey key = moduleKeyResolver.resolve( ModuleName.from( moduleName ) );
        if ( key != null )
        {
            return key;
        }

        throw PortalWebException.notFound().message( "Module [{0}] not found for path [{1}].", moduleName, contentPath ).build();
    }
}
