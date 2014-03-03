package com.enonic.wem.core.content.page.layout;

import javax.inject.Inject;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.page.layout.CreateLayoutDescriptorParams;
import com.enonic.wem.api.content.page.layout.GetLayoutDescriptor;
import com.enonic.wem.api.content.page.layout.GetLayoutDescriptorsByModules;
import com.enonic.wem.api.content.page.layout.LayoutDescriptor;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorService;
import com.enonic.wem.api.content.page.layout.LayoutDescriptors;
import com.enonic.wem.api.module.ModuleKeys;

public class LayoutDescriptorServiceImpl
    implements LayoutDescriptorService
{
    @Inject
    private Client client;

    public LayoutDescriptor getByKey( final LayoutDescriptorKey key )
    {
        final GetLayoutDescriptor command = new GetLayoutDescriptor( key );
        return client.execute( command );
    }

    public LayoutDescriptor create( final CreateLayoutDescriptorParams params )
    {
        return client.execute( params );
    }

    public LayoutDescriptors getByModules( final ModuleKeys moduleKeys )
    {
        final GetLayoutDescriptorsByModules command = new GetLayoutDescriptorsByModules( moduleKeys );
        return client.execute( command );
    }
}
