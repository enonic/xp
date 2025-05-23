package com.enonic.xp.admin.widget;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.descriptor.DescriptorKey;

@PublicApi
public interface WidgetDescriptorService
{
    Descriptors<WidgetDescriptor> getByInterfaces( String... interfaceName );

    Descriptors<WidgetDescriptor> getByApplication( ApplicationKey applicationKey );

    WidgetDescriptor getByKey( DescriptorKey descriptorKey );
}
