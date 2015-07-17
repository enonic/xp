package com.enonic.xp.portal.bean;

import java.util.function.Supplier;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.resource.ResourceKey;

public interface BeanContext
{
    ApplicationKey getModule();

    ResourceKey getResource();

    Supplier<PortalRequest> getRequest();

    <T> Supplier<T> getService( Class<T> type );
}
