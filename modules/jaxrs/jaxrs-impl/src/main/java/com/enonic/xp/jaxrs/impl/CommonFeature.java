package com.enonic.xp.jaxrs.impl;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import com.enonic.xp.core.internal.json.ObjectMapperHelper;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.jaxrs.impl.exception.JsonExceptionMapper;
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
        context.register( new MultipartFormReader( this.multipartService ) );
        context.register( new JacksonJsonProvider( ObjectMapperHelper.create() ) );
        context.register( new JsonExceptionMapper() );
        return true;
    }
}
