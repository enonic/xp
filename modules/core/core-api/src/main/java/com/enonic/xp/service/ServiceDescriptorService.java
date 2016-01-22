package com.enonic.xp.service;

import com.google.common.annotations.Beta;

import com.enonic.xp.page.DescriptorKey;

@Beta
public interface ServiceDescriptorService
{
    ServiceDescriptor getByKey( final DescriptorKey descriptorKey );
}
