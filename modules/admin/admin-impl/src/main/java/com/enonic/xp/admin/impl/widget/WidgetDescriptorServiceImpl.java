package com.enonic.xp.admin.impl.widget;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.widget.GetWidgetDescriptorsParams;
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
    public Descriptors<WidgetDescriptor> getWidgetDescriptors( final GetWidgetDescriptorsParams params )
    {
        return this.descriptorService.getAll( WidgetDescriptor.class ).
            filter( widgetDescriptor -> {
                if ( params.getInterfaceNames() != null && params.getInterfaceNames().stream().noneMatch( widgetDescriptor::hasInterface ) )
                {
                    return false;
                }
                if ( params.getPrincipalKeys() != null && !widgetDescriptor.isAccessAllowed( params.getPrincipalKeys() ) )
                {
                    return false;
                }
                return true;
            } );
    }

    @Reference
    public void setDescriptorService( final DescriptorService descriptorService )
    {
        this.descriptorService = descriptorService;
    }
}
