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

    private final Object contextArg;

    private IdProviderControllerExecutionParams( final Builder builder )
    {
        idProviderKey = builder.idProviderKey;
        functionName = builder.functionName;
        servletRequest = builder.servletRequest;
        portalRequest = builder.portalRequest;
        response = builder.response;
        contextArg = builder.contextArg;
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

    /**
     * An optional value passed as the second argument to the controller function (after the
     * request). Used to hand a function-specific context to predefined hooks - e.g. the device/native
     * approval context passed to the {@code approval} function.
     */
    public Object getContextArg()
    {
        return contextArg;
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

        private Object contextArg;

        private Builder()
        {
        }

        public Builder idProviderKey( final IdProviderKey idProviderKey )
        {
            this.idProviderKey = idProviderKey;
            return this;
        }

        public Builder functionName( final String functionName )
        {
            this.functionName = functionName;
            return this;
        }

        public Builder portalRequest( final PortalRequest portalRequest )
        {
            this.portalRequest = portalRequest;
            return this;
        }

        public Builder servletRequest( final HttpServletRequest servletRequest )
        {
            this.servletRequest = servletRequest;
            return this;
        }

        public Builder response( final HttpServletResponse response )
        {
            this.response = response;
            return this;
        }

        public Builder contextArg( final Object contextArg )
        {
            this.contextArg = contextArg;
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
