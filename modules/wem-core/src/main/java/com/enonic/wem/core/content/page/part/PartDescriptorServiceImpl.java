package com.enonic.wem.core.content.page.part;

import javax.inject.Inject;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.page.part.CreatePartDescriptor;
import com.enonic.wem.api.command.content.page.part.GetPartDescriptor;
import com.enonic.wem.api.command.content.page.part.GetPartDescriptorsByModules;
import com.enonic.wem.api.content.page.part.PartDescriptor;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.content.page.part.PartDescriptorService;
import com.enonic.wem.api.content.page.part.PartDescriptors;
import com.enonic.wem.api.module.ModuleKeys;

public class PartDescriptorServiceImpl
    implements PartDescriptorService
{
    @Inject
    private Client client;

    public PartDescriptor getByKey( final PartDescriptorKey key )
    {
        final GetPartDescriptor command = new GetPartDescriptor( key );
        return client.execute( command );
    }

    public PartDescriptor create( final CreatePartDescriptor command )
    {
        return client.execute( command );
    }

    public PartDescriptors getByModules( final ModuleKeys moduleKeys )
    {
        final GetPartDescriptorsByModules command = new GetPartDescriptorsByModules( moduleKeys );
        return client.execute( command );
    }
}
