package com.enonic.xp.widget;

import com.google.common.annotations.Beta;

@Beta
public interface WidgetDescriptorService
{
    WidgetDescriptors getByInterface( final String interfaceName );

}
