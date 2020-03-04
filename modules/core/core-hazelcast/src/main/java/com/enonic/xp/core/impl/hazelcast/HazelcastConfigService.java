package com.enonic.xp.core.impl.hazelcast;

import com.hazelcast.config.Config;

public interface HazelcastConfigService
{
    boolean isHazelcastEnabled();

    Config configure();
}
