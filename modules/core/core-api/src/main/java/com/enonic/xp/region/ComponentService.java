package com.enonic.xp.region;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;

@Beta
public interface ComponentService
{
    @Deprecated
    Component getByName( final ApplicationKey applicationKey, final ComponentName name );

    Component getByKey( final DescriptorKey descriptorKey );
}
