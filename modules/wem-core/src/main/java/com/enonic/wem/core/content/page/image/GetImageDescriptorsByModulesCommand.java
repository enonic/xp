package com.enonic.wem.core.content.page.image;

import java.io.IOException;

import com.enonic.wem.api.content.page.image.ImageDescriptors;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.Modules;
import com.enonic.wem.util.Exceptions;

final class GetImageDescriptorsByModulesCommand
    extends AbstractGetImageDescriptorCommand<GetImageDescriptorsByModulesCommand>
{
    private ModuleKeys moduleKeys;

    public ImageDescriptors execute()
    {
        try
        {
            return doExecute();
        }
        catch ( IOException e )
        {
            throw Exceptions.newRutime( "Error retrieving image descriptors" ).withCause( e );
        }
    }

    private ImageDescriptors doExecute()
        throws IOException
    {
        final Modules modules = this.moduleService.getModules( this.moduleKeys );
        return getImageDescriptorsFromModules( modules );
    }

    public GetImageDescriptorsByModulesCommand modules( final ModuleKeys moduleKeys )
    {
        this.moduleKeys = moduleKeys;
        return this;
    }
}
