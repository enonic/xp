package com.enonic.wem.portal.exception;

import javax.inject.Singleton;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.service.StatusService;

import com.enonic.wem.core.entity.dao.NodeNotFoundException;
import com.enonic.wem.portal.exception.renderer.ExceptionRenderer;
import com.enonic.wem.portal.script.SourceException;

@Singleton
public final class PortalStatusService
    extends StatusService
{
    @Override
    public Representation getRepresentation( final Status status, final Request request, final Response response )
    {
        final Throwable cause = status.getThrowable();
        if ( cause instanceof SourceException )
        {
            return getSourceException( status, (SourceException) cause );
        }
        else if ( status.isServerError() )
        {
            return getServerError( status, cause );
        }
        else
        {
            return getOtherError( status, cause );
        }
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

    private Representation getSourceException( final Status status, final SourceException cause )
    {
        return new ExceptionRenderer().
            sourceError( cause.getInnerSourceError() ).
            exception( cause ).
            description( cause.getMessage() ).
            status( status.getCode() ).
            title( "Script evaluation error" ).
            render();
    }

    private Representation getServerError( final Status status, final Throwable cause )
    {
        return new ExceptionRenderer().
            exception( cause ).
            description( cause.getMessage() ).
            status( status.getCode() ).
            title( status.getReasonPhrase() ).
            render();
    }

    private Representation getOtherError( final Status status, final Throwable cause )
    {
        return new ExceptionRenderer().
            exception( cause ).
            description( status.getDescription() ).
            status( status.getCode() ).
            title( status.getReasonPhrase() ).
            render();
    }
}
