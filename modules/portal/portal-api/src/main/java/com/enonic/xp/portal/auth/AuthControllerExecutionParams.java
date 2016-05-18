package com.enonic.xp.portal.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Preconditions;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.security.UserStoreKey;

public class AuthControllerExecutionParams
{
    private final UserStoreKey userStoreKey;

    private final String functionName;

    private final HttpServletRequest servletRequest;

    private final PortalRequest portalRequest;

    private final HttpServletResponse response;

    private AuthControllerExecutionParams( final Builder builder )
    {
        userStoreKey = builder.userStoreKey;
        functionName = builder.functionName;
        servletRequest = builder.servletRequest;
        portalRequest = builder.portalRequest;
        response = builder.response;
    }

    public UserStoreKey getUserStoreKey()
    {
        return userStoreKey;
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


    public static final class Builder
    {
        private UserStoreKey userStoreKey;

        private String functionName;

        private HttpServletRequest servletRequest;

        private PortalRequest portalRequest;

        private HttpServletResponse response;

        private Builder()
        {
        }

        public Builder userStoreKey( final UserStoreKey userStoreKey )
        {
            this.userStoreKey = userStoreKey;
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

        public AuthControllerExecutionParams build()
        {
            validate();
            return new AuthControllerExecutionParams( this );
        }
    }
}
