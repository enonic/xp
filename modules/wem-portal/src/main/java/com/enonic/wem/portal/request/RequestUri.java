package com.enonic.wem.portal.request;

public final class RequestUri
{
    private final RequestProtocol protocol;

    private final String host;

    private final int port;

    private final RequestPath path;

    private RequestUri( final Builder builder )
    {
        this.protocol = builder.protocol;
        this.host = builder.host;
        this.port = builder.port;
        this.path = builder.path;
    }

    public RequestProtocol getProtocol()
    {
        return this.protocol;
    }

    public String getHost()
    {
        return this.host;
    }

    public int getPort()
    {
        return this.port;
    }

    public RequestPath getPath()
    {
        return this.path;
    }

    @Override
    public String toString()
    {
        final StringBuilder str = new StringBuilder();
        str.append( this.protocol.getScheme() ).append( "://" );
        str.append( this.host );

        if ( this.protocol.getDefaultPort() != this.port )
        {
            str.append( ":" ).append( this.port );
        }

        if ( this.path != null )
        {
            str.append( this.path.toString() );
        }

        return str.toString();
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public final static class Builder
    {
        private RequestProtocol protocol;

        private String host;

        private int port;

        private RequestPath path;

        public Builder protocol( final String protocol )
        {
            this.protocol = RequestProtocol.from( protocol );
            return this;
        }

        public Builder protocol( final RequestProtocol protocol )
        {
            this.protocol = protocol;
            return this;
        }

        public Builder host( final String host )
        {
            this.host = host;
            return this;
        }

        public Builder port( final int port )
        {
            this.port = port;
            return this;
        }

        public Builder path( final String path )
        {
            this.path = RequestPath.from( path );
            return this;
        }

        public Builder path( final RequestPath path )
        {
            this.path = path;
            return this;
        }

        public RequestUri build()
        {
            return new RequestUri( this );
        }
    }
}
