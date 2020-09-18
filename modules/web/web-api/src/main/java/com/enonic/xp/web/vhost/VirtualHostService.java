package com.enonic.xp.web.vhost;

import java.util.List;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface VirtualHostService
{

    boolean isEnabled();

    List<VirtualHost> getVirtualHosts();

}
