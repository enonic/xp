package com.enonic.wem.portal.internal.base;

import org.restlet.resource.ResourceException;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleService;

public abstract class ModuleBaseResource
    extends WorkspaceBaseResource
{
    protected ModuleService moduleService;

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
        return ModuleKey.from( moduleName );
    }
}
