package com.enonic.xp.mail;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class MailMessageParams
{
    private final String[] to;

    private final String[] from;

    private final String[] cc;

    private final String[] bcc;

    private final String[] replyTo;

    private final String subject;

    private final String contentType;

    private final String body;

    private final Map<String, String> headers;

    private final List<Map<String, Object>> attachments;

    private MailMessageParams( Builder builder )
    {
        this.to = Objects.requireNonNullElse( builder.to, new String[]{} );
        this.from = Objects.requireNonNullElse( builder.from, new String[]{} );
        this.cc = Objects.requireNonNullElse( builder.cc, new String[]{} );
        this.bcc = Objects.requireNonNullElse( builder.bcc, new String[]{} );
        this.replyTo = Objects.requireNonNullElse( builder.replyTo, new String[]{} );
        this.subject = builder.subject;
        this.contentType = builder.contentType;
        this.body = builder.body;
        this.headers = builder.headers;
        this.attachments = builder.attachments;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public String[] getTo()
    {
        return to;
    }

    public String[] getFrom()
    {
        return from;
    }

    public String[] getCc()
    {
        return cc;
    }

    public String[] getBcc()
    {
        return bcc;
    }

    public String[] getReplyTo()
    {
        return replyTo;
    }

    public String getSubject()
    {
        return subject;
    }

    public String getContentType()
    {
        return contentType;
    }

    public String getBody()
    {
        return body;
    }

    public Map<String, String> getHeaders()
    {
        return headers;
    }

    public List<Map<String, Object>> getAttachments()
    {
        return attachments;
    }

    public static class Builder
    {
        private String[] to;

        private String[] from;

        private String[] cc;

        private String[] bcc;

        private String[] replyTo;

        private String subject;

        private String contentType;

        private String body;

        private Map<String, String> headers;

        private List<Map<String, Object>> attachments;

        public Builder setTo( final String[] to )
        {
            this.to = to;
            return this;
        }

        public Builder setFrom( final String[] from )
        {
            this.from = from;
            return this;
        }

        public Builder setCc( final String[] cc )
        {
            this.cc = cc;
            return this;
        }

        public Builder setBcc( final String[] bcc )
        {
            this.bcc = bcc;
            return this;
        }

        public Builder setReplyTo( final String[] replyTo )
        {
            this.replyTo = replyTo;
            return this;
        }

        public Builder setSubject( final String subject )
        {
            this.subject = subject;
            return this;
        }

        public Builder setContentType( final String contentType )
        {
            this.contentType = contentType;
            return this;
        }

        public Builder setBody( final String body )
        {
            this.body = body;
            return this;
        }

        public Builder setHeaders( final Map<String, String> headers )
        {
            this.headers = headers;
            return this;
        }

        public Builder setAttachments( final List<Map<String, Object>> attachments )
        {
            this.attachments = attachments;
            return this;
        }

        public MailMessageParams build()
        {
            return new MailMessageParams( this );
        }
    }
}
