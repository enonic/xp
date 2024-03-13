package com.enonic.xp.lib.mail;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.mail.MailMessageParams;
import com.enonic.xp.mail.MailService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class SendMailHandler
    implements ScriptBean
{
    private static final Logger LOG = LoggerFactory.getLogger( SendMailHandler.class );

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

    private Supplier<MailService> mailService;

    public void setTo( final String[] to )
    {
        this.to = to;
    }

    public void setFrom( final String[] from )
    {
        this.from = from;
    }

    public void setCc( final String[] cc )
    {
        this.cc = cc;
    }

    public void setBcc( final String[] bcc )
    {
        this.bcc = bcc;
    }

    public void setReplyTo( final String[] replyTo )
    {
        this.replyTo = replyTo;
    }

    public void setContentType( final String contentType )
    {
        this.contentType = contentType;
    }

    public void setBody( final String body )
    {
        this.body = body;
    }

    public void setSubject( final String subject )
    {
        this.subject = subject;
    }

    public void setHeaders( final Map<String, String> headers )
    {
        this.headers = headers;
    }

    public void setAttachments( final List<Map<String, Object>> attachments )
    {
        this.attachments = attachments;
    }

    public boolean send()
    {
        try
        {
            final MailMessageParams params = createParams();
            this.mailService.get().send( params );
            return true;
        }
        catch ( final Exception e )
        {
            LOG.warn( "Mail could not be sent", e );
            return false;
        }
    }

    private MailMessageParams createParams()
    {
        return MailMessageParams.create()
            .setTo( this.to )
            .setFrom( this.from )
            .setCc( this.cc )
            .setBcc( this.bcc )
            .setReplyTo( this.replyTo )
            .setSubject( this.subject )
            .setContentType( this.contentType )
            .setBody( this.body )
            .setHeaders( this.headers )
            .setAttachments( this.attachments )
            .build();
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.mailService = context.getService( MailService.class );
    }
}
