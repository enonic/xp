package com.enonic.xp.portal.idprovider;

import com.google.common.base.Preconditions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.security.IdProviderKey;

public final class IdProviderControllerExecutionParams
{
    private final IdProviderKey idProviderKey;

    private final String functionName;

    private final HttpServletRequest servletRequest;

    private final PortalRequest portalRequest;

    private final HttpServletResponse response;

    private IdProviderControllerExecutionParams( final Builder builder )
    {
        idProviderKey = builder.idProviderKey;
        functionName = builder.functionName;
        servletRequest = builder.servletRequest;
        portalRequest = builder.portalRequest;
        response = builder.response;
    }

    public IdProviderKey getIdProviderKey()
    {
        return idProviderKey;
    }

    public String getFunctionName()
    {
        return functionName;
    }

    public HttpServletRequest getServletRequest()
    {
        return servletRequest;
    }

    public PortalRequest getPortalRequest()
    {
        return portalRequest;
    }

    public HttpServletResponse getResponse()
    {
        return response;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private IdProviderKey idProviderKey;

        private String functionName;

        private HttpServletRequest servletRequest;

        private PortalRequest portalRequest;

        private HttpServletResponse response;

        private Builder()
        {
        }

        /**
         * Sets the id provider whose controller is executed. If omitted, the vhost's default id
         * provider is used.
         */
        public Builder idProviderKey( final IdProviderKey idProviderKey )
        {
            this.idProviderKey = idProviderKey;
            return this;
        }

        /**
         * Sets the controller function to execute. If omitted, the request method (e.g. {@code GET})
         * is used to resolve the function.
         */
        public Builder functionName( final String functionName )
        {
            this.functionName = functionName;
            return this;
        }

        /**
         * Sets the portal request to execute with. Either this or {@link #servletRequest} is required.
         */
        public Builder portalRequest( final PortalRequest portalRequest )
        {
            this.portalRequest = portalRequest;
            return this;
        }

        /**
         * Sets the servlet request to adapt into a portal request. Either this or
         * {@link #portalRequest} is required.
         */
        public Builder servletRequest( final HttpServletRequest servletRequest )
        {
            this.servletRequest = servletRequest;
            return this;
        }

        /**
         * Sets the servlet response that the controller response is serialized to, if any.
         */
        public Builder response( final HttpServletResponse response )
        {
            this.response = response;
            return this;
        }

        private void validate()
        {
            Preconditions.checkArgument( servletRequest != null || portalRequest != null,
                                         "servletRequest and portalRequest cannot be both null" );
        }

        public IdProviderControllerExecutionParams build()
        {
            validate();
            return new IdProviderControllerExecutionParams( this );
        }
    }
}
