package com.enonic.xp.web.vhost;

import java.util.List;


public interface VirtualHostService
{
    boolean isEnabled();

    List<VirtualHost> getVirtualHosts();
}
