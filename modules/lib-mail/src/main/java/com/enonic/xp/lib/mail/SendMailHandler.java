package com.enonic.xp.lib.mail;

import java.util.Map;
import java.util.function.Supplier;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.google.common.base.Joiner;

import com.enonic.xp.mail.MailMessage;
import com.enonic.xp.mail.MailService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class SendMailHandler
    implements MailMessage, ScriptBean
{
    private String[] to;

    private String[] from;

    private String subject;

    private Supplier<MailService> mailService;

    public void setTo( final String... to )
    {
        this.to = to;
    }

    public void setFrom( final String... from )
    {
        this.from = from;
    }

    public void setSubject( final String subject )
    {
        this.subject = subject;
    }

    public void setHeaders( final Map<String, String> headers )
    {
        System.out.println( Joiner.on(",").withKeyValueSeparator( "=" ).join( headers ) );
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
            return false;
        }
    }

    @Override
    public void compose( final MimeMessage message )
        throws Exception
    {
        message.setSubject( this.subject );
        // message.addRecipients( Message.RecipientType.TO, toAddresses( this.to ) );
    }

    private InternetAddress toAddress( final String str )
        throws Exception
    {
        return new InternetAddress( str );
    }

    private InternetAddress[] toAddresses( final String[] list )
        throws Exception
    {
        return new InternetAddress[0];
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.mailService = context.getService( MailService.class );
    }
}
