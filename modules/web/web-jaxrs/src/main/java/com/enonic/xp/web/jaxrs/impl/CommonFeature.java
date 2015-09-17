package com.enonic.xp.web.jaxrs.impl;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.web.jaxrs.JaxRsComponent;
import com.enonic.xp.web.jaxrs.impl.exception.JsonExceptionMapper;
import com.enonic.xp.web.jaxrs.impl.json.JsonObjectProvider;
import com.enonic.xp.web.jaxrs.impl.multipart.MultipartFormReader;

@Component(immediate = true, service = JaxRsComponent.class)
public final class CommonFeature
    implements Feature, JaxRsComponent
{
    @Override
    public boolean configure( final FeatureContext context )
    {
        context.register( new MultipartFormReader() );
        context.register( new JsonObjectProvider() );
        context.register( new JsonExceptionMapper() );
        return true;
    }
}
