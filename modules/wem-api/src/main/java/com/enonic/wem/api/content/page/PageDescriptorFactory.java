package com.enonic.wem.api.content.page;


import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.api.resource.Resource;

import static com.enonic.wem.api.content.page.PageDescriptor.newPageDescriptor;

public class PageDescriptorFactory
{
    public static PageDescriptor create( final Resource resource )
    {
        final ResourcePath resourcePath = ResourcePath.from( "/controllers" ).resolve( resource.getName() );
        final ModuleResourceKey controllerResourceKey = new ModuleResourceKey( ModuleKey.from( "mymod-1.0.0" ), resourcePath );
        final ControllerSetup controllerSetup = new ControllerSetup( controllerResourceKey );

        // TODO deserialize from resource
        final PageDescriptor pageDescriptor = newPageDescriptor().
            displayName( "my page descriptor" ).
            controllerSetup( controllerSetup ).
            build();

        return pageDescriptor;
    }
}
