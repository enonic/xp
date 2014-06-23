package com.enonic.wem.portal.base;

import javax.inject.Inject;

import org.restlet.resource.ResourceException;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeyResolver;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.core.module.ModuleKeyResolverService;
import com.enonic.wem.core.module.ModuleResourcePathResolver;

public abstract class ModuleBaseResource
    extends BaseResource
{
    @Inject
    protected ModuleResourcePathResolver modulePathResolver;

    @Inject
    protected ModuleKeyResolverService moduleKeyResolverService;

    protected String contentPath;

    @Override
    protected void doInit()
        throws ResourceException
    {
        super.doInit();
        this.contentPath = getAttribute( "path" );
    }

    protected final ModuleKey resolveModule()
    {
        final String moduleName = getAttribute( "module" );
        final ContentPath path = ContentPath.from( this.contentPath );

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
        try
        {
            final ModuleKeyResolver moduleKeyResolver = this.moduleKeyResolverService.forContent( contentPath );
            return moduleKeyResolver.resolve( ModuleName.from( moduleName ) );
        }
        catch ( final ModuleNotFoundException e )
        {
            throw notFound( "Module [%s] not found", moduleName );
        }
    }
}
