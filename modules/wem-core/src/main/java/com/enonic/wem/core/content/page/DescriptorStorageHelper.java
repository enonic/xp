package com.enonic.wem.core.content.page;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.content.page.Descriptor;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;

import static com.enonic.wem.api.command.Commands.module;
import static com.enonic.wem.api.resource.Resource.newResource;

public final class DescriptorStorageHelper
{
    private final Client client;

    public DescriptorStorageHelper( final Client client )
    {
        this.client = client;
    }

    public void store( final Descriptor descriptor, final String descriptorXml )
    {
        final Resource descriptorResource = newResource().
            name( descriptor.getName().toString() ).
            stringValue( descriptorXml ).
            build();

        final ModuleResourceKey resourceKey = DescriptorKeyToModuleResourceKey.translate( descriptor.getKey() );

        client.execute( module().createResource().
            resourceKey( resourceKey ).
            resource( descriptorResource ) );
    }

}
