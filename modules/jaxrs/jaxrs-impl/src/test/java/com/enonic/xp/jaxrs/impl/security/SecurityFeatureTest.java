package com.enonic.xp.jaxrs.impl.security;

import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;

import org.junit.Test;
import org.mockito.Mockito;

public class SecurityFeatureTest
{
    @Test
    public void testConfigure()
    {
        final SecurityFeature feature = new SecurityFeature();

        final ResourceInfo info = Mockito.mock( ResourceInfo.class );
        final FeatureContext context = Mockito.mock( FeatureContext.class );
        feature.configure( info, context );
    }
}
