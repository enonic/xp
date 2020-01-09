package com.enonic.xp.jaxrs.impl;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.jboss.resteasy.plugins.interceptors.encoding.AcceptEncodingGZIPFilter;
import org.jboss.resteasy.plugins.interceptors.encoding.GZIPDecodingInterceptor;

import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.jaxrs.impl.exception.JsonExceptionMapper;
import com.enonic.xp.jaxrs.impl.image.RenderedImageProvider;
import com.enonic.xp.jaxrs.impl.json.JsonObjectProvider;
import com.enonic.xp.jaxrs.impl.multipart.MultipartFormReader;
import com.enonic.xp.web.multipart.MultipartService;

final class CommonFeature
    implements Feature, JaxRsComponent
{
    private final MultipartService multipartService;

    CommonFeature( final MultipartService multipartService )
    {
        this.multipartService = multipartService;
    }

    @Override
    public boolean configure( final FeatureContext context )
    {
        context.register( AcceptEncodingGZIPFilter.class );
        context.register( GZIPDecodingInterceptor.class );
        context.register( new MultipartFormReader( this.multipartService ) );
        context.register( new JsonObjectProvider() );
        context.register( new JsonExceptionMapper() );
        context.register( new RenderedImageProvider() );
        return true;
    }
}
