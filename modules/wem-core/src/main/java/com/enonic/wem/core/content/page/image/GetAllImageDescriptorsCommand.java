package com.enonic.wem.core.content.page.image;

import java.io.IOException;

import com.enonic.wem.api.content.page.image.ImageDescriptors;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.Modules;
import com.enonic.wem.util.Exceptions;

final class GetAllImageDescriptorsCommand
    extends AbstractGetImageDescriptorCommand<GetAllImageDescriptorsCommand>
{

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
        final Modules modules = this.moduleService.getAllModules();
        return getImageDescriptorsFromModules( modules );
    }
}
