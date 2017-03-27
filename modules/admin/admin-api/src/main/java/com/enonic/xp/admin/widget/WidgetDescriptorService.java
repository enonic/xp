package com.enonic.xp.admin.widget;

import com.google.common.annotations.Beta;

import com.enonic.xp.descriptor.Descriptors;

@Beta
public interface WidgetDescriptorService
{
    Descriptors<WidgetDescriptor> getByInterfaces( final String... interfaceName );
}
