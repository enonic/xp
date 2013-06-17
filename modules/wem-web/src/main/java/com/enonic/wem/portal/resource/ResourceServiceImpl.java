package com.enonic.wem.portal.resource;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.resource.GetResource;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.portal.AbstractPortalService;

public class ResourceServiceImpl
    extends AbstractPortalService
    implements ResourceService
{
    @Override
    public Resource getResource( final ResourceRequest resourceRequest )
    {
        final GetResource getResource = Commands.resource().get().path( resourceRequest.getRelativePathAsString() ).module( "myModuel" );

        final Resource result = client.execute( getResource );

        return result;
    }
}
