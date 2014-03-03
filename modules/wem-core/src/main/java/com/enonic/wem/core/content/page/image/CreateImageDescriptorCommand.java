package com.enonic.wem.core.content.page.image;

import com.enonic.wem.api.content.page.image.CreateImageDescriptorParams;
import com.enonic.wem.api.content.page.image.ImageDescriptor;
import com.enonic.wem.api.content.page.image.ImageDescriptorXml;
import com.enonic.wem.api.module.CreateModuleResourceParams;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.core.content.page.DescriptorKeyToModuleResourceKey;
import com.enonic.wem.xml.XmlSerializers;

import static com.enonic.wem.api.content.page.image.ImageDescriptor.newImageDescriptor;
import static com.enonic.wem.api.resource.Resource.newResource;

final class CreateImageDescriptorCommand
{
    private CreateImageDescriptorParams params;

    private ModuleService moduleService;

    public ImageDescriptor execute()
    {
        this.params.validate();

        final ImageDescriptor imageDescriptor = newImageDescriptor().
            config( params.getConfig() ).
            displayName( params.getDisplayName() ).
            name( params.getName() ).
            key( params.getKey() ).
            build();

        final String imageDescriptorXml = serialize( imageDescriptor );

        final Resource descriptorResource = newResource().
            name( imageDescriptor.getName().toString() ).
            stringValue( imageDescriptorXml ).
            build();

        final ModuleResourceKey resourceKey = DescriptorKeyToModuleResourceKey.translate( imageDescriptor.getKey() );
        final CreateModuleResourceParams createResourceSpec = new CreateModuleResourceParams().
            resourceKey( resourceKey ).
            resource( descriptorResource );
        this.moduleService.createResource( createResourceSpec );

        return imageDescriptor;
    }

    private String serialize( final ImageDescriptor imageDescriptor )
    {
        final ImageDescriptorXml imageDescriptorXml = new ImageDescriptorXml();
        imageDescriptorXml.from( imageDescriptor );
        return XmlSerializers.imageDescriptor().serialize( imageDescriptorXml );
    }

    public CreateImageDescriptorCommand params( final CreateImageDescriptorParams params )
    {
        this.params = params;
        return this;
    }

    public CreateImageDescriptorCommand moduleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
        return this;
    }
}
