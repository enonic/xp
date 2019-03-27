package com.enonic.xp.portal.idprovider;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Preconditions;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.security.IdProviderKey;

public class IdProviderControllerExecutionParams
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
        return portalRequest == null ? servletRequest : portalRequest.getRawRequest();
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

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final IdProviderControllerExecutionParams that = (IdProviderControllerExecutionParams) o;
        return Objects.equals( idProviderKey, that.idProviderKey ) && Objects.equals( functionName, that.functionName ) &&
            Objects.equals( servletRequest, that.servletRequest ) && Objects.equals( portalRequest, that.portalRequest ) &&
            Objects.equals( response, that.response );
    }

    @Override
    public int hashCode()
    {

        return Objects.hash( idProviderKey, functionName, servletRequest, portalRequest, response );
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

        private void validate()
        {
            Preconditions.checkNotNull( functionName, "functionName cannot be null" );
            if ( servletRequest == null && portalRequest == null )
            {
                throw new NullPointerException( String.valueOf( "servletRequest and portalRequest cannot be both null" ) );
            }
        }

        public IdProviderControllerExecutionParams build()
        {
            validate();
            return new IdProviderControllerExecutionParams( this );
        }
    }
}
