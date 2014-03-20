package com.enonic.wem.core.content.page;

import com.enonic.wem.api.content.page.CreatePageDescriptorParams;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.content.page.PageDescriptorXml;
import com.enonic.wem.api.module.CreateModuleResourceParams;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.xml.XmlSerializers;

import static com.enonic.wem.api.content.page.PageDescriptor.newPageDescriptor;
import static com.enonic.wem.api.resource.Resource.newResource;

class CreatePageDescriptorCommand
{
    private CreatePageDescriptorParams params;

    private ModuleService moduleService;

    public PageDescriptor execute()
    {
        this.params.validate();

        final PageDescriptor pageDescriptor = newPageDescriptor().
            config( params.getConfig() ).
            displayName( params.getDisplayName() ).
            regions( params.getRegions() ).
            key( params.getKey() ).
            build();

        final String pageDescriptorXml = serialize( pageDescriptor );

        final Resource descriptorResource = newResource().
            name( pageDescriptor.getName().toString() ).
            stringValue( pageDescriptorXml ).
            build();

        final ModuleResourceKey resourceKey = DescriptorKeyToModuleResourceKey.translate( pageDescriptor.getKey() );
        final CreateModuleResourceParams createResourceSpec = new CreateModuleResourceParams().
            resourceKey( resourceKey ).
            resource( descriptorResource );
        this.moduleService.createResource( createResourceSpec );

        return pageDescriptor;
    }

    private String serialize( final PageDescriptor pageDescriptor )
    {
        final PageDescriptorXml pageDescriptorXml = new PageDescriptorXml();
        pageDescriptorXml.from( pageDescriptor );
        return XmlSerializers.pageDescriptor().serialize( pageDescriptorXml );
    }

    public CreatePageDescriptorCommand params( final CreatePageDescriptorParams params )
    {
        this.params = params;
        return this;
    }

    public CreatePageDescriptorCommand moduleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
        return this;
    }
}
