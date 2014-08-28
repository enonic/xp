package com.enonic.wem.portal.internal.exception;

import javax.inject.Singleton;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.service.StatusService;

import com.enonic.wem.api.resource.ResourceProblemException;
import com.enonic.wem.core.entity.dao.NodeNotFoundException;
import com.enonic.wem.portal.internal.exception.renderer.ExceptionRenderer;

@Singleton
public final class PortalStatusService
    extends StatusService
{
    @Override
    public Representation getRepresentation( final Status status, final Request request, final Response response )
    {
        final Throwable cause = status.getThrowable();
        final String html = renderThrowable( status, cause );
        return new StringRepresentation( html, MediaType.TEXT_HTML );
    }

    @Override
    public Status getStatus( final Throwable cause, final Request request, final Response response )
    {
        if ( cause instanceof NodeNotFoundException )
        {
            return new Status( Status.CLIENT_ERROR_NOT_FOUND, cause, cause.getMessage() );
        }

        return super.getStatus( cause, request, response );
    }

    private String renderThrowable( final Status status, final Throwable cause )
    {
        if ( cause instanceof ResourceProblemException )
        {
            return renderSourceException( status, (ResourceProblemException) cause );
        }
        else if ( status.isServerError() )
        {
            return renderServerError( status, cause );
        }
        else
        {
            return renderOtherError( status, cause );
        }
    }

    private String renderSourceException( final Status status, final ResourceProblemException cause )
    {
        return new ExceptionRenderer().
            sourceError( cause.getInnerError() ).
            exception( cause ).
            description( getDescription( status, cause.getInnerError() ) ).
            status( status.getCode() ).
            title( "Script evaluation error" ).
            render();
    }

    private String renderServerError( final Status status, final Throwable cause )
    {
        return new ExceptionRenderer().
            exception( cause ).
            description( getDescription( status, cause ) ).
            status( status.getCode() ).
            title( status.getReasonPhrase() ).
            render();
    }

    private String renderOtherError( final Status status, final Throwable cause )
    {
        return new ExceptionRenderer().
            exception( cause ).
            description( getDescription( status, cause ) ).
            status( status.getCode() ).
            title( status.getReasonPhrase() ).
            render();
    }

    private String getDescription( final Status status, final Throwable cause )
    {
        String str = null;

        if ( cause != null )
        {
            str = cause.getMessage();
        }

        if ( str == null )
        {
            str = status.getDescription();
        }

        if ( str == null )
        {
            str = status.getReasonPhrase();
        }

        return str;
    }
}
