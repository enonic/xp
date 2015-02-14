package com.enonic.xp.admin.impl;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import com.enonic.xp.admin.impl.rest.exception.JsonExceptionMapper;
import com.enonic.xp.admin.impl.rest.multipart.MultipartFormReader;
import com.enonic.xp.admin.impl.rest.provider.JsonObjectProvider;
import com.enonic.xp.admin.impl.rest.provider.JsonSerializableProvider;
import com.enonic.xp.admin.impl.rest.provider.RenderedImageProvider;

final class AdminJaxRsFeature
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
