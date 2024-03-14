package com.enonic.xp.lib.mail;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.mail.SendMailParams;
import com.enonic.xp.mail.MailService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class SendMailHandler
    implements ScriptBean
{
    private static final Logger LOG = LoggerFactory.getLogger( SendMailHandler.class );

    private List<String> to;

    private List<String> from;

    private List<String> cc;

    private List<String> bcc;

    private List<String> replyTo;

    private String subject;

    private String contentType;

    private String body;

    private Map<String, String> headers;

    private List<Map<String, Object>> attachments;

    private Supplier<MailService> mailService;

    public void setTo( final String[] to )
    {
        this.to = arrayToList( to );
    }

    public void setFrom( final String[] from )
    {
        this.from = arrayToList( from );
    }

    public void setCc( final String[] cc )
    {
        this.cc = arrayToList( cc );
    }

    public void setBcc( final String[] bcc )
    {
        this.bcc = arrayToList( bcc );
    }

    public void setReplyTo( final String[] replyTo )
    {
        this.replyTo = arrayToList( replyTo );
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
            final SendMailParams params = createParams();
            this.mailService.get().send( params );
            return true;
        }
        catch ( final Exception e )
        {
            LOG.warn( "Mail could not be sent", e );
            return false;
        }
    }

    private List<String> arrayToList( final String[] value )
    {
        return value == null ? List.of() : Stream.of( value ).collect( Collectors.toList() );
    }

    private SendMailParams createParams()
    {
        return SendMailParams.create()
            .to( this.to )
            .from( this.from )
            .cc( this.cc )
            .bcc( this.bcc )
            .replyTo( this.replyTo )
            .subject( this.subject )
            .contentType( this.contentType )
            .body( this.body )
            .headers( this.headers )
            .attachments( this.attachments )
            .build();
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.mailService = context.getService( MailService.class );
    }
}
