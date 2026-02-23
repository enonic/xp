package com.enonic.xp.web.exception;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

public interface ExceptionRenderer
{
    WebResponse render( WebRequest req, Exception cause );

    /**
     * Throws an exception if all conditions are met:
     * - response is an error (status 4xx - 5xx)
     * - response body is empty
     * - exception is not handled earlier
     * Otherwise returns the response as-is.
     *
     * @param request  the input WebRequest containing the details of the request to process
     * @param response the WebResponse
     * @return the WebResponse unmodified
     */
    WebResponse maybeThrow( WebRequest request, WebResponse response );
}
