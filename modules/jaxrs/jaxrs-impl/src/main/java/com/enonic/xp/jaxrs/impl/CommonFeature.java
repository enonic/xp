package com.enonic.xp.jaxrs.impl;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.jaxrs.impl.exception.JsonExceptionMapper;
import com.enonic.xp.jaxrs.impl.image.RenderedImageProvider;
import com.enonic.xp.jaxrs.impl.json.JsonObjectProvider;
import com.enonic.xp.jaxrs.impl.multipart.MultipartFormReader;
import com.enonic.xp.web.multipart.MultipartService;

@Component(immediate = true, service = JaxRsComponent.class)
public final class CommonFeature
    implements Feature, JaxRsComponent
{
    private MultipartService multipartService;

    @Override
    public boolean configure( final FeatureContext context )
    {
        context.register( new MultipartFormReader( this.multipartService ) );
        context.register( new JsonObjectProvider() );
        context.register( new JsonExceptionMapper() );
        context.register( new RenderedImageProvider() );
        return true;
    }

    @Reference
    public void setMultipartService( final MultipartService multipartService )
    {
        this.multipartService = multipartService;
    }
}
