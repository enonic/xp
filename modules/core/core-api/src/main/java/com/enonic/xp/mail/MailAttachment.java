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
        this.fileName = MailHeaderValue.requireSingleLine( Objects.requireNonNull( builder.fileName ), "Attachment file name" );
        this.data = Objects.requireNonNull( builder.data );
        this.mimeType = MailHeaderValue.requireSingleLine( builder.mimeType, "Attachment mime type" );
        this.headers = builder.headers != null ? ImmutableMap.copyOf( builder.headers ) : ImmutableMap.of();
        this.headers.forEach( ( name, value ) -> {
            MailHeaderValue.requireSingleLine( name, "Attachment header name" );
            MailHeaderValue.requireSingleLine( value, "Attachment header value" );
        } );
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
