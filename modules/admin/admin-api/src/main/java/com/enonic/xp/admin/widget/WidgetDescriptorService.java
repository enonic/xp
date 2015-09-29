package com.enonic.xp.admin.widget;

import com.google.common.annotations.Beta;

@Beta
public interface WidgetDescriptorService
{
    WidgetDescriptors getByInterface( final String interfaceName );
}
