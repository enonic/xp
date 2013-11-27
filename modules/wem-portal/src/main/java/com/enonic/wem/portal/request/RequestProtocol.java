package com.enonic.wem.portal.request;

public enum RequestProtocol
{
    HTTP( 80 ),
    HTTPS( 443 );

    private final int defaultPort;

    private RequestProtocol( final int defaultPort )
    {
        this.defaultPort = defaultPort;
    }

    public String getScheme()
    {
        return this.name().toLowerCase();
    }

    public int getDefaultPort()
    {
        return this.defaultPort;
    }

    public static RequestProtocol from( final String scheme )
    {
        return RequestProtocol.valueOf( scheme.toUpperCase() );
    }
}