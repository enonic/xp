package com.enonic.xp.web.vhost.impl.config;

import com.enonic.xp.web.vhost.impl.mapping.VirtualHostMappings;

public interface VirtualHostConfig
{
    public boolean isEnabled();

    public boolean isRequireMapping();

    public VirtualHostMappings getMappings();
}
