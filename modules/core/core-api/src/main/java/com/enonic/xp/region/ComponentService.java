package com.enonic.xp.region;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;

@PublicApi
public interface ComponentService
{
    @Deprecated
    Component getByName( final ApplicationKey applicationKey, final ComponentName name );

    Component getByKey( final DescriptorKey descriptorKey );
}
