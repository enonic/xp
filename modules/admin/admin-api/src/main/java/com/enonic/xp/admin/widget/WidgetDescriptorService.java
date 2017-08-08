package com.enonic.xp.admin.widget;

import com.google.common.annotations.Beta;

import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.page.DescriptorKey;

@Beta
public interface WidgetDescriptorService
{
    Descriptors<WidgetDescriptor> getWidgetDescriptors( final GetWidgetDescriptorsParams params );

    WidgetDescriptor getByKey( final DescriptorKey descriptorKey );
}
