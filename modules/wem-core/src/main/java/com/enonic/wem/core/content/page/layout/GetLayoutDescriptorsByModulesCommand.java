package com.enonic.wem.core.content.page.layout;

import java.io.IOException;

import com.enonic.wem.api.content.page.layout.LayoutDescriptors;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.Modules;
import com.enonic.wem.util.Exceptions;

final class GetLayoutDescriptorsByModulesCommand
    extends AbstractGetLayoutDescriptorCommand<GetLayoutDescriptorsByModulesCommand>
{
    private ModuleKeys moduleKeys;

    public LayoutDescriptors execute()
    {
        try
        {
            return doExecute();
        }
        catch ( IOException e )
        {
            throw Exceptions.newRutime( "Error retrieving layout descriptors" ).withCause( e );
        }
    }

    private LayoutDescriptors doExecute()
        throws IOException
    {
        final Modules modules = this.moduleService.getModules( this.moduleKeys );
        return getDescriptorsFromModules( modules );
    }

    public GetLayoutDescriptorsByModulesCommand moduleKeys( final ModuleKeys moduleKeys )
    {
        this.moduleKeys = moduleKeys;
        return this;
    }
}
