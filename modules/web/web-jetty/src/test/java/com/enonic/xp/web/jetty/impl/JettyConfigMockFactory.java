package com.enonic.xp.web.jetty.impl;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

public class JettyConfigMockFactory
{
    public JettyConfig newConfig()
    {
        return Mockito.mock( JettyConfig.class, this::defaultAnswer );
    }

    private Object defaultAnswer( final InvocationOnMock invocation )
    {
        return invocation.getMethod().getDefaultValue();
    }
}
