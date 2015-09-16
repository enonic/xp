package com.enonic.xp.web.jaxrs.impl;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import com.enonic.xp.web.jaxrs.impl.rest.exception.JsonExceptionMapper;
import com.enonic.xp.web.jaxrs.impl.rest.multipart.MultipartFormReader;
import com.enonic.xp.web.jaxrs.impl.rest.provider.JsonObjectProvider;
import com.enonic.xp.web.jaxrs.impl.rest.provider.JsonSerializableProvider;
import com.enonic.xp.web.jaxrs.impl.rest.provider.RenderedImageProvider;

final class CommonJaxRsFeature
    implements Feature
{
    @Override
    public boolean configure( final FeatureContext context )
    {
        context.register( new MultipartFormReader() );
        context.register( new JsonObjectProvider() );
        context.register( new JsonSerializableProvider() );
        context.register( new RenderedImageProvider() );
        context.register( new JsonExceptionMapper() );
        return true;
    }
}
