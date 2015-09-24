package com.enonic.xp.admin.impl.widget;

import com.google.common.annotations.Beta;

@Beta
public interface WidgetDescriptorService
{
    WidgetDescriptors getByInterface( final String interfaceName );

}
