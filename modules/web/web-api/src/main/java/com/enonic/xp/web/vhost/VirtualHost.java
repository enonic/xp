package com.enonic.xp.web.vhost;

import com.google.common.annotations.Beta;

@Beta
public interface VirtualHost
{
    String getName();

    String getHost();

    String getSource();

    String getTarget();
}
