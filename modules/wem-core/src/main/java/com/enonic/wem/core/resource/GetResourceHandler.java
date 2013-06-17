package com.enonic.wem.core.resource;

import javax.inject.Inject;

import com.enonic.wem.api.command.resource.GetResource;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.resource.dao.ResourceDao;

public class GetResourceHandler
    extends CommandHandler<GetResource>
{
    private ResourceDao resourceDao;

    public GetResourceHandler()
    {
        super( GetResource.class );
    }

    @Override
    public void handle( final CommandContext context, final GetResource command )
        throws Exception
    {
        final Resource resource = resourceDao.getResource( command.getPath(), command.getModule() );

        command.setResult( resource );
    }


    @Inject
    public void setResourceDao( final ResourceDao resourceDao )
    {
        this.resourceDao = resourceDao;
    }
}
