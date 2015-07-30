package com.enonic.xp.portal.impl.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.exception.NotFoundException;
import com.enonic.xp.portal.impl.services.PortalServices;
import com.enonic.xp.resource.ResourceService;

@Provider
public final class PortalExceptionMapper
    implements ExceptionMapper<Throwable>
{
    private final static Logger LOG = LoggerFactory.getLogger( PortalExceptionMapper.class );

    private PortalServices portalServices;

    @Override
    public final Response toResponse( final Throwable cause )
    {
        final ExceptionInfo info = toErrorInfo( cause );
        logIfNeeded( info );
        return info.toResponse();
    }

    private ExceptionInfo toErrorInfo( final Throwable cause )
    {
        if ( cause instanceof WebApplicationException )
        {
            return toErrorInfo( (WebApplicationException) cause );
        }

        if ( cause instanceof NotFoundException )
        {
            return ExceptionInfo.create( Response.Status.NOT_FOUND.getStatusCode() ).
                cause( cause ).
                resourceService( getResourceService() );
        }

        if ( cause instanceof IllegalArgumentException )
        {
            return ExceptionInfo.create( Response.Status.BAD_REQUEST.getStatusCode() ).
                cause( cause ).
                resourceService( getResourceService() );
        }

        return ExceptionInfo.create( Response.Status.INTERNAL_SERVER_ERROR.getStatusCode() ).
            cause( cause ).
            resourceService( getResourceService() );
    }

    private ExceptionInfo toErrorInfo( final WebApplicationException cause )
    {
        return ExceptionInfo.create( cause.getResponse().getStatus() ).
            cause( cause ).
            resourceService( getResourceService() );
    }

    private void logIfNeeded( final ExceptionInfo info )
    {
        if ( info.shouldLogAsError() )
        {
            LOG.error( info.getMessage(), info.getCause() );
        }
    }

    private ResourceService getResourceService()
    {
        return this.portalServices == null ? null : this.portalServices.getResourceService();
    }

    public void setPortalServices( final PortalServices portalServices )
    {
        this.portalServices = portalServices;
    }
}
