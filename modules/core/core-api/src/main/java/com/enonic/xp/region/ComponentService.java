package com.enonic.xp.region;

import com.google.common.annotations.Beta;

import com.enonic.xp.page.DescriptorKey;

@Beta
public interface ComponentService
{
    Component getByKey( final DescriptorKey descriptorKey );
}
