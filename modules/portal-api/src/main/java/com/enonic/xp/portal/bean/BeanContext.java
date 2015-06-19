package com.enonic.xp.portal.bean;

import java.util.function.Supplier;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.resource.ResourceKey;

public interface BeanContext
{
    ModuleKey getModule();

    ResourceKey getResource();

    Supplier<PortalRequest> getRequest();

    <T> Supplier<T> getService( Class<T> type );
}
