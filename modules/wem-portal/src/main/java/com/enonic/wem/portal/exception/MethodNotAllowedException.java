package com.enonic.wem.portal.exception;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public final class MethodNotAllowedException
    extends WebApplicationException
{
    public MethodNotAllowedException()
    {
        super( Response.status( new Response.StatusType()
        {
            @Override
            public int getStatusCode()
            {
                return HttpServletResponse.SC_METHOD_NOT_ALLOWED;
            }

            @Override
            public Response.Status.Family getFamily()
            {
                return Response.Status.Family.CLIENT_ERROR;
            }

            @Override
            public String getReasonPhrase()
            {
                return "Method not allowed";
            }
        } ).build() );
    }
}
