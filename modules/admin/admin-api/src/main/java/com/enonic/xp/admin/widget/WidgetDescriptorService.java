package com.enonic.xp.admin.widget;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.page.DescriptorKey;

@PublicApi
public interface WidgetDescriptorService
{
    Descriptors<WidgetDescriptor> getByInterfaces( final String... interfaceName );

    Descriptors<WidgetDescriptor> getAllowedByInterfaces( final String... interfaceName );

    Descriptors<WidgetDescriptor> getByApplication( final ApplicationKey applicationKey );

    WidgetDescriptor getByKey( final DescriptorKey descriptorKey );
}
