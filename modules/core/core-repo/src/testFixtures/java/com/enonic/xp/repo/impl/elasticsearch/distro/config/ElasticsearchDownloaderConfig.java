package com.enonic.xp.repo.impl.elasticsearch.distro.config;

public class ElasticsearchDownloaderConfig
{

    private final int connectionTimeoutInMs;

    private final int readTimeoutInMs;

    private ElasticsearchDownloaderConfig( final Builder builder )
    {
        this.connectionTimeoutInMs = builder.connectionTimeoutInMs();
        this.readTimeoutInMs = builder.readTimeoutInMs();
    }

    public int getConnectionTimeoutInMs()
    {
        return connectionTimeoutInMs;
    }

    public int getReadTimeoutInMs()
    {
        return readTimeoutInMs;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private int connectionTimeoutInMs = 3_000;

        private int readTimeoutInMs = 300_000;

        public Builder connectionTimeoutInMs( int connectionTimeoutInMs )
        {
            this.connectionTimeoutInMs = connectionTimeoutInMs;
            return this;
        }

        public Builder readTimeoutInMs( int readTimeoutInMs )
        {
            this.readTimeoutInMs = readTimeoutInMs;
            return this;
        }

        public ElasticsearchDownloaderConfig build()
        {
            return new ElasticsearchDownloaderConfig( this );
        }

        private int connectionTimeoutInMs()
        {
            return connectionTimeoutInMs;
        }

        private int readTimeoutInMs()
        {
            return readTimeoutInMs;
        }

    }

}
