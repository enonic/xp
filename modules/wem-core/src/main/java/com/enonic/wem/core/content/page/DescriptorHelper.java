package com.enonic.wem.core.content.page;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.module.CreateModuleResource;
import com.enonic.wem.api.content.page.Descriptor;
import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.api.resource.Resource;

import static com.enonic.wem.api.command.Commands.module;
import static com.enonic.wem.api.resource.Resource.newResource;

public final class DescriptorHelper
{
    private static final ResourcePath COMPONENT_FOLDER = ResourcePath.from( "component" );

    private DescriptorHelper()
    {
    }

    public static void storeDescriptorResource( final Descriptor descriptor, final String descriptorXml, final Client client )
    {
        final Resource descriptorResource = newResource().
            stringValue( descriptorXml ).
            name( descriptor.getName().toString() ).
            build();

        final ModuleResourceKey resourceKey = moduleResourceKeyForDescriptor( descriptor.getKey() );
        final CreateModuleResource createCommandResource = module().createResource().
            resource( descriptorResource ).
            resourceKey( resourceKey );
        client.execute( createCommandResource );
    }

    public static ModuleResourceKey moduleResourceKeyForDescriptor( final DescriptorKey descriptorKey )
    {
        final String descriptorType;
        switch ( descriptorKey.getDescriptorType() )
        {
            case IMAGE:
                descriptorType = "image";
                break;
            case LAYOUT:
                descriptorType = "layout";
                break;
            case PAGE:
                descriptorType = "page";
                break;
            case PART:
                descriptorType = "part";
                break;
            default:
                throw new IllegalArgumentException( "Unsupported DescriptorType: " + descriptorKey.getDescriptorType() );
        }
        final ResourcePath path = COMPONENT_FOLDER.resolve( descriptorKey.getName().toString() ).resolve( descriptorType + ".xml" );
        return new ModuleResourceKey( descriptorKey.getModuleKey(), path );
    }
}
