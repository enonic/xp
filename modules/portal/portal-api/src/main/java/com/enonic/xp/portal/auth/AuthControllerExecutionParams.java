package com.enonic.xp.portal.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Preconditions;

import com.enonic.xp.security.UserStoreKey;

public class AuthControllerExecutionParams
{
    private final UserStoreKey userStoreKey;

    private final String functionName;

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    private AuthControllerExecutionParams( final Builder builder )
    {
        userStoreKey = builder.userStoreKey;
        functionName = builder.functionName;
        request = builder.request;
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

    public HttpServletRequest getRequest()
    {
        return request;
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

        private HttpServletRequest request;

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

        public Builder request( final HttpServletRequest request )
        {
            this.request = request;
            return this;
        }

        public Builder response( final HttpServletResponse response )
        {
            this.response = response;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( userStoreKey, "userStoreKey cannot be null" );
            Preconditions.checkNotNull( functionName, "functionName cannot be null" );
            Preconditions.checkNotNull( request, "request cannot be null" );
        }

        public AuthControllerExecutionParams build()
        {
            validate();
            return new AuthControllerExecutionParams( this );
        }
    }
}
