package com.enonic.xp.web.vhost.impl.config;

import java.util.List;

public interface VirtualHostConfig
{
    public boolean isEnabled();

    public boolean isRequireMapping();

    public List<VirtualHostMapping> getMappings();
}
