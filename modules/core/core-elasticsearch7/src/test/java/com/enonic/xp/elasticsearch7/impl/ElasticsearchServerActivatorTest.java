package com.enonic.xp.elasticsearch7.impl;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ElasticsearchServerActivatorTest
{

    @Test
    @Disabled()
    void startstop()
        throws Exception
    {
        ElasticsearchServerConfig serverConfig = Mockito.mock( ElasticsearchServerConfig.class );
        ElasticsearchServerActivator activator = new ElasticsearchServerActivator(serverConfig);
        activator.deactivate();
    }
}