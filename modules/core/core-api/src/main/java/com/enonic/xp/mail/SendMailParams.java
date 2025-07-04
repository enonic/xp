package com.enonic.xp.mail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class SendMailParams
{
    private final List<String> to;

    private final List<String> from;

    private final List<String> cc;

    private final List<String> bcc;

    private final List<String> replyTo;

    private final List<MailHeader> headers;

    private final List<MailAttachment> attachments;

    private final String subject;

    private final String contentType;

    private final String body;

    private SendMailParams( Builder builder )
    {
        this.to = List.copyOf( builder.to );
        this.from = List.copyOf( builder.from );
        this.cc = List.copyOf( builder.cc );
        this.bcc = List.copyOf( builder.bcc );
        this.replyTo = List.copyOf( builder.replyTo );
        this.headers = List.copyOf( builder.headers );
        this.attachments = List.copyOf( builder.attachments );
        this.subject = builder.subject;
        this.contentType = builder.contentType;
        this.body = builder.body;
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

    public List<MailHeader> getHeaders()
    {
        return headers;
    }

    public List<MailAttachment> getAttachments()
    {
        return attachments;
    }

    public static final class Builder
    {
        private final List<String> to = new ArrayList<>();

        private final List<String> from = new ArrayList<>();

        private final List<String> cc = new ArrayList<>();

        private final List<String> bcc = new ArrayList<>();

        private final List<String> replyTo = new ArrayList<>();

        private final List<MailHeader> headers = new ArrayList<>();

        private final List<MailAttachment> attachments = new ArrayList<>();

        private String subject;

        private String contentType;

        private String body;

        public Builder to( final String... to )
        {
            this.to.addAll( List.of( to ) );
            return this;
        }

        public Builder to( final Collection<String> to )
        {
            this.to.addAll( to );
            return this;
        }

        public Builder from( final String... from )
        {
            this.from.addAll( List.of( from ) );
            return this;
        }

        public Builder from( final Collection<String> from )
        {
            this.from.addAll( from );
            return this;
        }

        public Builder cc( final String... cc )
        {
            this.cc.addAll( List.of( cc ) );
            return this;
        }

        public Builder cc( final Collection<String> cc )
        {
            this.cc.addAll( cc );
            return this;
        }

        public Builder bcc( final String... bcc )
        {
            this.bcc.addAll( List.of( bcc ) );
            return this;
        }

        public Builder bcc( final Collection<String> bcc )
        {
            this.bcc.addAll( bcc );
            return this;
        }

        public Builder replyTo( final String... replyTo )
        {
            this.replyTo.addAll( List.of( replyTo ) );
            return this;
        }

        public Builder replyTo( final Collection<String> replyTo )
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

        public Builder addHeader( final String key, final String value )
        {
            this.headers.add( MailHeader.from( key, value ) );
            return this;
        }

        public void addHeaders( final Map<String, String> headers )
        {
            headers.forEach( this::addHeader );
        }

        public Builder addAttachment( final MailAttachment attachment )
        {
            this.attachments.add( attachment );
            return this;
        }

        public Builder addAttachments( final Collection<MailAttachment> attachments )
        {
            this.attachments.addAll( attachments );
            return this;
        }

        public SendMailParams build()
        {
            return new SendMailParams( this );
        }
    }
}
