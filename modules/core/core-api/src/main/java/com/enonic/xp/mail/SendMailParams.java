package com.enonic.xp.mail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class SendMailParams
{
    private final List<String> to;

    private final List<String> from;

    private final List<String> cc;

    private final List<String> bcc;

    private final List<String> replyTo;

    private final String subject;

    private final String contentType;

    private final String body;

    private final Map<String, String> headers;

    private final List<Map<String, Object>> attachments;

    private SendMailParams( Builder builder )
    {
        this.to = builder.to;
        this.from = builder.from;
        this.cc = builder.cc;
        this.bcc = builder.bcc;
        this.replyTo = builder.replyTo;
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

    public List<String> getTo()
    {
        return to;
    }

    public List<String> getFrom()
    {
        return from;
    }

    public List<String> getCc()
    {
        return cc;
    }

    public List<String> getBcc()
    {
        return bcc;
    }

    public List<String> getReplyTo()
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
        private final List<String> to = new ArrayList<>();

        private final List<String> from = new ArrayList<>();

        private final List<String> cc = new ArrayList<>();

        private final List<String> bcc = new ArrayList<>();

        private final List<String> replyTo = new ArrayList<>();

        private String subject;

        private String contentType;

        private String body;

        private Map<String, String> headers;

        private List<Map<String, Object>> attachments;

        public Builder to( final String... to )
        {
            this.to.addAll( Arrays.asList( to ) );
            return this;
        }

        public Builder to( final List<String> to )
        {
            this.to.addAll( to );
            return this;
        }

        public Builder from( final String... from )
        {
            this.from.addAll( Arrays.asList( from ) );
            return this;
        }

        public Builder from( final List<String> from )
        {
            this.from.addAll( from );
            return this;
        }

        public Builder cc( final String... cc )
        {
            this.cc.addAll( Arrays.asList( cc ) );
            return this;
        }

        public Builder cc( final List<String> cc )
        {
            this.cc.addAll( cc );
            return this;
        }

        public Builder bcc( final String... bcc )
        {
            this.bcc.addAll( Arrays.asList( bcc ) );
            return this;
        }

        public Builder bcc( final List<String> bcc )
        {
            this.bcc.addAll( bcc );
            return this;
        }

        public Builder replyTo( final String... replyTo )
        {
            this.replyTo.addAll( Arrays.asList( replyTo ) );
            return this;
        }

        public Builder replyTo( final List<String> replyTo )
        {
            this.replyTo.addAll( replyTo );
            return this;
        }

        public Builder subject( final String subject )
        {
            this.subject = subject;
            return this;
        }

        public Builder contentType( final String contentType )
        {
            this.contentType = contentType;
            return this;
        }

        public Builder body( final String body )
        {
            this.body = body;
            return this;
        }

        public Builder headers( final Map<String, String> headers )
        {
            this.headers = headers;
            return this;
        }

        public Builder attachments( final List<Map<String, Object>> attachments )
        {
            this.attachments = attachments;
            return this;
        }

        public SendMailParams build()
        {
            return new SendMailParams( this );
        }
    }
}
