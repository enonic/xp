package com.enonic.xp.region;

import com.enonic.xp.descriptor.DescriptorKey;


public interface ComponentService
{
    Component getByKey( DescriptorKey descriptorKey );
}
