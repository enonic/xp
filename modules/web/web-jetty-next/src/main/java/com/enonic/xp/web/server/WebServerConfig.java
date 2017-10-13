package com.enonic.xp.web.server;

import java.io.File;

public final class WebServerConfig
{
    private final int port;

    private final String host;

    private final int idleTimeout;

    private final boolean sendServerHeader;

    private final int requestHeaderSize;

    private final int responseHeaderSize;

    private final boolean gzipEnabled;

    private final int gzipMinSize;

    private final boolean logEnabled;

    private final boolean logAppend;

    private final boolean logExtended;

    private final String logTimeZone;

    private final int logRetainDays;

    private final File logFile;

    private final int sessionTimeout;

    private final String sessionCookieName;

    private final int multipartMaxFileSize;

    private final int multipartMaxRequestSize;

    private final int multipartFileSizeThreshold;

    private WebServerConfig( final Builder builder )
    {
        this.port = builder.port;
        this.host = builder.host;
        this.idleTimeout = builder.idleTimeout;
        this.sendServerHeader = builder.sendServerHeader;
        this.requestHeaderSize = builder.requestHeaderSize;
        this.responseHeaderSize = builder.responseHeaderSize;
        this.gzipEnabled = builder.gzipEnabled;
        this.gzipMinSize = builder.gzipMinSize;
        this.logEnabled = builder.logEnabled;
        this.logAppend = builder.logAppend;
        this.logExtended = builder.logExtended;
        this.logTimeZone = builder.logTimeZone;
        this.logRetainDays = builder.logRetainDays;
        this.logFile = builder.logFile;
        this.sessionTimeout = builder.sessionTimeout;
        this.sessionCookieName = builder.sessionCookieName;
        this.multipartMaxFileSize = builder.multipartMaxFileSize;
        this.multipartMaxRequestSize = builder.multipartMaxRequestSize;
        this.multipartFileSizeThreshold = builder.multipartFileSizeThreshold;
    }

    public int getPort()
    {
        return this.port;
    }

    public String getHost()
    {
        return this.host;
    }

    public int getIdleTimeout()
    {
        return this.idleTimeout;
    }

    public boolean getSendServerHeader()
    {
        return this.sendServerHeader;
    }

    public int getRequestHeaderSize()
    {
        return this.requestHeaderSize;
    }

    public int getResponseHeaderSize()
    {
        return this.responseHeaderSize;
    }

    public boolean isGzipEnabled()
    {
        return this.gzipEnabled;
    }

    public int getGzipMinSize()
    {
        return this.gzipMinSize;
    }

    public boolean isLogEnabled()
    {
        return this.logEnabled;
    }

    public boolean isLogAppend()
    {
        return this.logAppend;
    }

    public boolean isLogExtended()
    {
        return this.logExtended;
    }

    public String getLogTimeZone()
    {
        return this.logTimeZone;
    }

    public int getLogRetainDays()
    {
        return this.logRetainDays;
    }

    public File getLogFile()
    {
        return this.logFile;
    }

    public int getSessionTimeout()
    {
        return this.sessionTimeout;
    }

    public String getSessionCookieName()
    {
        return this.sessionCookieName;
    }

    public int getMultipartMaxFileSize()
    {
        return this.multipartMaxFileSize;
    }

    public int getMultipartMaxRequestSize()
    {
        return this.multipartMaxRequestSize;
    }

    public int getMultipartFileSizeThreshold()
    {
        return this.multipartFileSizeThreshold;
    }

    public final static class Builder
    {
        private int port;

        private String host;

        private int idleTimeout;

        private boolean sendServerHeader;

        private int requestHeaderSize;

        private int responseHeaderSize;

        private boolean gzipEnabled;

        private int gzipMinSize;

        private boolean logEnabled;

        private boolean logAppend;

        private boolean logExtended;

        private String logTimeZone;

        private int logRetainDays;

        private File logFile;

        private int sessionTimeout;

        private String sessionCookieName;

        private int multipartMaxFileSize;

        private int multipartMaxRequestSize;

        private int multipartFileSizeThreshold;

        private Builder()
        {
            this.port = -1;
            this.host = null;
            this.idleTimeout = 60000;
            this.sendServerHeader = false;
            this.requestHeaderSize = 32000;
            this.responseHeaderSize = 32000;
            this.gzipEnabled = false;
            this.gzipMinSize = 16;
            this.logEnabled = false;
            this.logAppend = true;
            this.logExtended = true;
            this.logFile = null;
            this.logRetainDays = 31;
            this.logTimeZone = "GMT";
            this.sessionTimeout = 60;
            this.sessionCookieName = "JSESSIONID";
            this.multipartMaxFileSize = -1;
            this.multipartMaxRequestSize = -1;
            this.multipartFileSizeThreshold = 1000;
        }

        public Builder port( final int value )
        {
            this.port = value;
            return this;
        }

        public Builder host( final String value )
        {
            this.host = value;
            return this;
        }

        public Builder idleTimeout( final int value )
        {
            this.idleTimeout = value;
            return this;
        }

        public Builder sendServerHeader( final boolean value )
        {
            this.sendServerHeader = value;
            return this;
        }

        public Builder gzipEnabled( final boolean value )
        {
            this.gzipEnabled = value;
            return this;
        }

        public Builder gzipMinSize( final int value )
        {
            this.gzipMinSize = value;
            return this;
        }

        public Builder logEnabled( final boolean value )
        {
            this.logEnabled = value;
            return this;
        }

        public Builder logAppend( final boolean value )
        {
            this.logAppend = value;
            return this;
        }

        public Builder logExtended( final boolean value )
        {
            this.logExtended = value;
            return this;
        }

        public Builder logFile( final File value )
        {
            this.logFile = value;
            return this;
        }

        public Builder logRetainDays( final int value )
        {
            this.logRetainDays = value;
            return this;
        }

        public Builder logTimeZone( final String value )
        {
            this.logTimeZone = value;
            return this;
        }

        public Builder sessionTimeout( final int value )
        {
            this.sessionTimeout = value;
            return this;
        }

        public Builder sessionCookieName( final String value )
        {
            this.sessionCookieName = value;
            return this;
        }

        public Builder multipartMaxFileSize( final int value )
        {
            this.multipartMaxFileSize = value;
            return this;
        }

        public Builder multipartMaxRequestSize( final int value )
        {
            this.multipartMaxRequestSize = value;
            return this;
        }

        public Builder multipartFileSizeThreshold( final int value )
        {
            this.multipartFileSizeThreshold = value;
            return this;
        }

        public WebServerConfig build()
        {
            return new WebServerConfig( this );
        }
    }

    public static Builder newBuilder()
    {
        return new Builder();
    }
}
