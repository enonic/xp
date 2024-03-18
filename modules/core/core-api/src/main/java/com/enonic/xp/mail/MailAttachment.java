package com.enonic.xp.mail;

import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteSource;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class MailAttachment
{
    private final String fileName;

    private final ByteSource data;

    private final String mimeType;

    private final Map<String, String> headers;

    private MailAttachment( final Builder builder )
    {
        this.fileName = Objects.requireNonNull( builder.fileName );
        this.data = Objects.requireNonNull( builder.data );
        this.mimeType = builder.mimeType;
        this.headers = builder.headers != null ? ImmutableMap.copyOf( builder.headers ) : ImmutableMap.of();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public String getFileName()
    {
        return fileName;
    }

    public ByteSource getData()
    {
        return data;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public Map<String, String> getHeaders()
    {
        return headers;
    }

    public static class Builder
    {
        private String fileName;

        private ByteSource data;

        private String mimeType;

        private Map<String, String> headers;

        public Builder fileName( final String fileName )
        {
            this.fileName = fileName;
            return this;
        }

        public Builder data( final ByteSource data )
        {
            this.data = data;
            return this;
        }

        public Builder mimeType( final String mimeType )
        {
            this.mimeType = mimeType;
            return this;
        }

        public Builder headers( final Map<String, String> headers )
        {
            this.headers = headers;
            return this;
        }

        public MailAttachment build()
        {
            return new MailAttachment( this );
        }
    }
}
