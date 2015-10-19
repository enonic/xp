package com.enonic.xp.web.jetty.impl;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class JettyConfigMockFactory
{
    public JettyConfig newConfig()
    {
        return Mockito.mock( JettyConfig.class, (Answer) this::defaultAnswer );
    }

    private Object defaultAnswer( final InvocationOnMock invocation )
    {
        return invocation.getMethod().getDefaultValue();
    }
}
