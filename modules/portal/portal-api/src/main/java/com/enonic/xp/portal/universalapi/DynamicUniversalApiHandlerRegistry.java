package com.enonic.xp.portal.universalapi;

import java.util.List;

import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.page.DescriptorKey;

public interface DynamicUniversalApiHandlerRegistry
{
    DynamicUniversalApiHandler getApiHandler( DescriptorKey key );

    List<ApiDescriptor> getAllApiDescriptors();
}
