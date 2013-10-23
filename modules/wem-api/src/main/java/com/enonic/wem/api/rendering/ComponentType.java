package com.enonic.wem.api.rendering;


import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.module.GetModuleResource;
import com.enonic.wem.api.content.page.PageDescriptor;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.resource.Resource;

public abstract class ComponentType<T extends Component>
{
    public abstract RenderingResult execute( T component, Context context, Client client );

    protected Resource getControllerResource( final PageDescriptor descriptor, final Client client )
    {
        final GetModuleResource command = Commands.module().getResource().resourceKey( descriptor.getControllerSetup().getSource() );
        return client.execute( command );
    }

    protected RootDataSet mergeConfig( final RootDataSet pageConfig, final RootDataSet pageConfigFromTemplate )
    {
        return new RootDataSet();
    }

}
