package com.enonic.xp.jaxrs.impl;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.ws.rs.core.FeatureContext;

import com.enonic.xp.web.multipart.MultipartService;

public class CommonFeatureTest
{
    @Test
    public void testConfigure()
    {
        final MultipartService multipartService = Mockito.mock( MultipartService.class );
        final CommonFeature feature = new CommonFeature( multipartService );

        final FeatureContext context = Mockito.mock( FeatureContext.class );
        feature.configure( context );
    }
}
