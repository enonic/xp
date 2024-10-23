package com.enonic.xp.portal;

import java.util.List;

import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.web.universalapi.UniversalApiHandlerWrapper;

public interface UniversalApiHandlerRegistry
{
    UniversalApiHandlerWrapper getApiHandler( DescriptorKey key );

    List<ApiDescriptor> getAllApiDescriptors();
}
