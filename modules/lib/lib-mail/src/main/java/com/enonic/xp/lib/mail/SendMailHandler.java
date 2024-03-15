package com.enonic.xp.lib.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;

import com.enonic.xp.mail.MailAttachment;
import com.enonic.xp.mail.MailService;
import com.enonic.xp.mail.SendMailParams;
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

    @Override
    public void initialize( final BeanContext context )
    {
        this.mailService = context.getService( MailService.class );
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
        return value == null ? List.of() : List.of( value );
    }

    private SendMailParams createParams()
    {
        final SendMailParams.Builder result = SendMailParams.create()
            .to( this.to )
            .from( this.from )
            .cc( this.cc )
            .bcc( this.bcc )
            .replyTo( this.replyTo )
            .subject( this.subject )
            .contentType( this.contentType )
            .body( this.body );

        if ( this.headers != null )
        {
            this.headers.forEach( result::addHeader );
        }

        if ( this.attachments != null )
        {
            resolveAttachments( this.attachments ).forEach( result::addAttachment );
        }

        return result.build();
    }

    @SuppressWarnings("unchecked")
    private List<MailAttachment> resolveAttachments( final List<Map<String, Object>> attachments )
    {
        final List<MailAttachment> result = new ArrayList<>();
        for ( Map<String, Object> attachmentObject : attachments )
        {
            final String name = getValue( attachmentObject, "fileName", String.class );
            final ByteSource data = getValue( attachmentObject, "data", ByteSource.class );
            final String mimeType = getValue( attachmentObject, "mimeType", String.class );
            final Map<String, String> headers = getValue( attachmentObject, "headers", Map.class );

            result.add( MailAttachment.create().data( data ).mimeType( mimeType ).fileName( name ).headers( headers ).build() );
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private <T> T getValue( final Map<String, Object> object, final String key, final Class<T> type )
    {
        final Object value = object.get( key );
        return type.isInstance( value ) ? (T) value : null;
    }
}
