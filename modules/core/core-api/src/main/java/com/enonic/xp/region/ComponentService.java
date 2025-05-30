package com.enonic.xp.region;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.descriptor.DescriptorKey;

@PublicApi
public interface ComponentService
{
    Component getByKey( DescriptorKey descriptorKey );
}
