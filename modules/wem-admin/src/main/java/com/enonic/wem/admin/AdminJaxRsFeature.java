package com.enonic.wem.admin;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import com.enonic.wem.admin.rest.exception.JsonExceptionMapper;
import com.enonic.wem.admin.rest.multipart.MultipartFormReader;
import com.enonic.wem.admin.rest.provider.JsonObjectProvider;
import com.enonic.wem.admin.rest.provider.JsonSerializableProvider;
import com.enonic.wem.admin.rest.provider.RenderedImageProvider;

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
