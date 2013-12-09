package com.enonic.wem.core.content.page;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.module.CreateModuleResource;
import com.enonic.wem.api.content.page.BaseDescriptor;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;

import static com.enonic.wem.api.command.Commands.module;
import static com.enonic.wem.api.resource.Resource.newResource;

public final class CreateDescriptorHelper
{

    public static void storeDescriptorResource( final BaseDescriptor descriptor, final String descriptorXml, final Client client )
    {
        final Resource descriptorResource = newResource().
            stringValue( descriptorXml ).
            name( descriptor.getName().toString() ).
            build();

        final DescriptorKey key = descriptor.getKey();
        final ModuleResourceKey resourceKey = new ModuleResourceKey( key.getModuleKey(), key.getPath() );
        final CreateModuleResource createCommandResource = module().createResource().
            resource( descriptorResource ).
            resourceKey( resourceKey );
        client.execute( createCommandResource );
    }

}
