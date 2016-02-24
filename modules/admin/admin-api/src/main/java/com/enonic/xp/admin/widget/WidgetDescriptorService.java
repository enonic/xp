package com.enonic.xp.admin.widget;

import com.google.common.annotations.Beta;

@Beta
public interface WidgetDescriptorService
{
    WidgetDescriptors getByInterfaces( final String... interfaceName );
}
