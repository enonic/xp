package com.enonic.xp.admin.impl.market;

import java.io.IOException;
import java.net.Proxy;

import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

final class ProxyAuthenticator
    implements Authenticator
{
    private final String userName;

    private final String password;

    ProxyAuthenticator( final String userName, final String password )
    {
        this.userName = userName;
        this.password = password;
    }

    @Override
    public Request authenticate( final Proxy proxy, final Response response )
        throws IOException
    {
        return null;
    }

    @Override
    public Request authenticateProxy( final Proxy proxy, final Response response )
        throws IOException
    {
        String credential = Credentials.basic( userName, password );
        return response.request().newBuilder().header( "Proxy-Authorization", credential ).build();
    }
}
