package com.enonic.xp.web;

import java.io.IOException;

import org.jspecify.annotations.NullMarked;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Writes a response body through the servlet API during {@link WebResponse} serialization.
 * Supply an instance to {@link WebResponse.Builder#body(Object)} to use a servlet-based transport.
 * The callback runs after the response status, content type, headers and cookies have been applied,
 * and can update the servlet response. Committed responses and statuses without content skip the callback.
 * XP may have consumed the request body before the callback runs; callers that need it must arrange replay.
 */
@NullMarked
@FunctionalInterface
public interface ServletResponseBody
{
    /**
     * Writes the body to the servlet response.
     *
     * @param request the raw servlet request associated with the web request
     * @param response the servlet response being serialized
     * @throws IOException if writing the response fails
     */
    void write( HttpServletRequest request, HttpServletResponse response )
        throws IOException;
}
