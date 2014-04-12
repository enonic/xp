package com.enonic.wem.core.content.page.part;

import java.io.IOException;

import com.google.common.base.Throwables;

import com.enonic.wem.api.content.page.part.PartDescriptors;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.Modules;

final class GetPartDescriptorsByModulesCommand
    extends AbstractGetPartDescriptorCommand<GetPartDescriptorsByModulesCommand>
{
    private ModuleKeys moduleKeys;

    public PartDescriptors execute()
    {
        try
        {
            return doExecute();
        }
        catch ( final IOException e )
        {
            throw Throwables.propagate( e );
        }
    }

    private PartDescriptors doExecute()
        throws IOException
    {
        final Modules modules = this.moduleService.getModules( this.moduleKeys );
        return getDescriptorsFromModules( modules );
    }

    public GetPartDescriptorsByModulesCommand moduleKeys( final ModuleKeys moduleKeys )
    {
        this.moduleKeys = moduleKeys;
        return this;
    }
}
