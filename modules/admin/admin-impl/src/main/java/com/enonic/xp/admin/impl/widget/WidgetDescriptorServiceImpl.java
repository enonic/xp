package com.enonic.xp.admin.impl.widget;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.admin.widget.WidgetDescriptorService;
import com.enonic.xp.descriptor.DescriptorService;
import com.enonic.xp.descriptor.Descriptors;

@Component(immediate = true)
public final class WidgetDescriptorServiceImpl
    implements WidgetDescriptorService
{
    private DescriptorService descriptorService;

    @Override
    public Descriptors<WidgetDescriptor> getByInterfaces( final String... interfaceNames )
    {
        return this.descriptorService.getAll( WidgetDescriptor.class ).
            filter( w -> containsInterface( w, interfaceNames ) );
    }

    private boolean containsInterface( final WidgetDescriptor descriptor, final String... interfaceNames )
    {
        for ( final String interfaceName : interfaceNames )
        {
            if ( descriptor.getInterfaces().contains( interfaceName ) )
            {
                return true;
            }
        }

        return false;
    }

    @Reference
    public void setDescriptorService( final DescriptorService descriptorService )
    {
        this.descriptorService = descriptorService;
    }
}
