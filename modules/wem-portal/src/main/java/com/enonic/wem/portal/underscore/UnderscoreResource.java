package com.enonic.wem.portal.underscore;

import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeyResolver;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.core.module.ModuleKeyResolverService;
import com.enonic.wem.core.module.ModuleResourcePathResolver;
import com.enonic.wem.portal.exception.PortalWebException;
import com.enonic.wem.portal.rendering.RenderResult;

public abstract class UnderscoreResource
{
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
        try
        {
            final ModuleKeyResolver moduleKeyResolver = this.moduleKeyResolverService.forContent( contentPath );
            return moduleKeyResolver.resolve( ModuleName.from( moduleName ) );
        }
        catch ( final ModuleNotFoundException e )
        {
            throw PortalWebException.notFound().message( e.getMessage() ).build();
        }
    }

    protected final Response toResponse( final RenderResult result )
    {
        final Response.ResponseBuilder builder = Response.status( result.getStatus() ).
            type( result.getType() ).
            entity( result.getEntity() );

        for ( final Map.Entry<String, String> header : result.getHeaders().entrySet() )
        {
            builder.header( header.getKey(), header.getValue() );
        }

        return builder.build();
    }

}
