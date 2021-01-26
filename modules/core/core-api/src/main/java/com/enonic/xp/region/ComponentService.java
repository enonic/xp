package com.enonic.xp.region;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;

@PublicApi
public interface ComponentService
{
    @Deprecated
    Component getByName( ApplicationKey applicationKey, ComponentName name );

    Component getByKey( DescriptorKey descriptorKey );
}
