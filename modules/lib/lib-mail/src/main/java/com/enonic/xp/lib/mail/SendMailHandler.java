package com.enonic.xp.lib.mail;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.mail.Message;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.mail.MailException;
import com.enonic.xp.mail.MailMessage;
import com.enonic.xp.mail.MailService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

import static com.google.common.base.Strings.nullToEmpty;

public final class SendMailHandler
    implements MailMessage, ScriptBean
{
    private final static Logger LOG = LoggerFactory.getLogger( SendMailHandler.class );

    private String[] to;

    private String[] from;

    private String[] cc;

    private String[] bcc;

    private String[] replyTo;

    private String subject;

    private String contentType;

    private String body;

    private Supplier<MailService> mailService;

    private Map<String, String> headers;

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

    public boolean send()
    {
        try
        {
            this.mailService.get().send( this );
            return true;
        }
        catch ( final Exception e )
        {
            LOG.warn( "Mail could not be sent", e );
            return false;
        }
    }

    @Override
    public void compose( final MimeMessage message )
        throws Exception
    {
        message.setSubject( this.subject );
        message.setText( nullToEmpty( this.body ), "UTF-8" );

        message.addFrom( toAddresses( this.from ) );
        message.addRecipients( Message.RecipientType.TO, toAddresses( this.to ) );
        message.addRecipients( Message.RecipientType.CC, toAddresses( this.cc ) );
        message.addRecipients( Message.RecipientType.BCC, toAddresses( this.bcc ) );
        message.setReplyTo( toAddresses( this.replyTo ) );

        if ( this.contentType != null ) {
            message.addHeader( "Content-Type", this.contentType );
        }

        if ( this.headers != null )
        {
            for ( Map.Entry<String, String> header : this.headers.entrySet() )
            {
                message.addHeader( header.getKey(), header.getValue() );
            }
        }
    }

    private InternetAddress toAddress( final String address )
        throws MailException
    {
        try
        {
            return new InternetAddress( address );
        }
        catch ( AddressException e )
        {
            throw new MailException( e.getMessage(), e );
        }
    }

    private InternetAddress[] toAddresses( final String[] addressList )
        throws Exception
    {
        return Stream.of( addressList ).filter( StringUtils::isNotBlank ).map( ( this::toAddress ) ).toArray( InternetAddress[]::new );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.mailService = context.getService( MailService.class );
    }
}
