package com.enonic.xp.jaxrs.impl;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.jboss.resteasy.plugins.interceptors.encoding.AcceptEncodingGZIPFilter;
import org.jboss.resteasy.plugins.interceptors.encoding.AcceptEncodingGZIPInterceptor;
import org.jboss.resteasy.plugins.interceptors.encoding.ClientContentEncodingAnnotationFeature;
import org.jboss.resteasy.plugins.interceptors.encoding.GZIPDecodingInterceptor;
import org.jboss.resteasy.plugins.interceptors.encoding.ServerContentEncodingAnnotationFeature;
import org.jboss.resteasy.plugins.providers.ByteArrayProvider;
import org.jboss.resteasy.plugins.providers.DefaultTextPlain;
import org.jboss.resteasy.plugins.providers.FileProvider;
import org.jboss.resteasy.plugins.providers.FileRangeWriter;
import org.jboss.resteasy.plugins.providers.FormUrlEncodedProvider;
import org.jboss.resteasy.plugins.providers.IIOImageProvider;
import org.jboss.resteasy.plugins.providers.InputStreamProvider;
import org.jboss.resteasy.plugins.providers.JaxrsFormProvider;
import org.jboss.resteasy.plugins.providers.ReaderProvider;
import org.jboss.resteasy.plugins.providers.StreamingOutputProvider;
import org.jboss.resteasy.plugins.providers.StringTextStar;

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
        context.register( DefaultTextPlain.class );
        context.register( StringTextStar.class );
        context.register( InputStreamProvider.class );
        context.register( ReaderProvider.class );
        context.register( ByteArrayProvider.class );
        context.register( FormUrlEncodedProvider.class );
        context.register( JaxrsFormProvider.class );
        context.register( FileProvider.class );
        context.register( FileRangeWriter.class );
        context.register( StreamingOutputProvider.class );
        context.register( IIOImageProvider.class );
        context.register( AcceptEncodingGZIPInterceptor.class );
        context.register( AcceptEncodingGZIPFilter.class );
        context.register( ClientContentEncodingAnnotationFeature.class );
        context.register( GZIPDecodingInterceptor.class );
        context.register( ServerContentEncodingAnnotationFeature.class );
        context.register( new MultipartFormReader( this.multipartService ) );
        context.register( new JsonObjectProvider() );
        context.register( new JsonExceptionMapper() );
        context.register( new RenderedImageProvider() );
        return true;
    }
}
