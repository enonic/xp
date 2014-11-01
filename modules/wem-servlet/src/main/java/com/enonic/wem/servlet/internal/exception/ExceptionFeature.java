package com.enonic.wem.servlet.internal.exception;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

@Provider
public final class ExceptionFeature
    implements Feature
{
    @Override
    public boolean configure( final FeatureContext context )
    {
        context.register( new DefaultExceptionMapper() );
        context.register( new ExceptionInfoBodyWriter() );
        context.register( new ExceptionInfoFilter() );
        return true;
    }
}
