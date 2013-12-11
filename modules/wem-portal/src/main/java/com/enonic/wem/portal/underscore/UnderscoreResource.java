package com.enonic.wem.portal.underscore;

import javax.inject.Inject;

import com.google.common.base.Optional;

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
    protected ModuleKeyResolverService moduleKeyResolver;

    protected final ModuleKey resolveModule( final String contentPath, final String moduleName )
    {
        try
        {
            return ModuleKey.from( moduleName );
        }
        catch ( final Exception e )
        {
            return resolveModuleFromSite( contentPath, moduleName );
        }
    }

    private ModuleKey resolveModuleFromSite( final String contentPath, final String moduleName )
    {
        final ContentPath path = ContentPath.from( contentPath );
        final ModuleKeyResolver moduleResolver = this.moduleKeyResolver.forContent( path );
        final Optional<ModuleKey> key = moduleResolver.resolve( ModuleName.from( moduleName ) );

        if ( key.isPresent() )
        {
            return key.get();
        }

        throw PortalWebException.notFound().message( "Module [{0}] not found for path [{1}].", moduleName, contentPath ).build();
    }
}
